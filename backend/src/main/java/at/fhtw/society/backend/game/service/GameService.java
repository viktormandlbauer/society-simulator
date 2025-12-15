package at.fhtw.society.backend.game.service;

import at.fhtw.society.backend.ai.DeepinfraService;
import at.fhtw.society.backend.ai.Message;
import at.fhtw.society.backend.game.dto.*;
import at.fhtw.society.backend.game.entity.*;
import at.fhtw.society.backend.game.repo.*;
import at.fhtw.society.backend.game.entity.Player;
import at.fhtw.society.backend.game.repo.PlayerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ThemeRepository themeRepository;
    private final PlayerRepository playerRepository;

    private final RoundRepository roundRepository;
    private final VotingRepository votingRepository;

    private final DeepinfraService deepinfraService;
    private final ObjectMapper objectMapper;

    /* -----------------------------
       GAME APIs
       ----------------------------- */

    @Transactional
    public UUID createGame(CreateGameDto dto) {
        Theme theme = themeRepository.findByTheme(dto.getThemeName());
        if (theme == null) throw new IllegalArgumentException("Theme not found: " + dto.getThemeName());

        Player gm = playerRepository.findByUsername(dto.getGamemaster())
                .orElseThrow(() -> new IllegalArgumentException("Gamemaster player not found: " + dto.getGamemaster()));

        Game game = new Game(gm, theme, dto.getMaxRounds(), dto.getMaxPlayers());
        game.setStatus(GameStatus.CREATED);

        // optional: add GM as joined player
        gm.getGames().add(game);
        game.getPlayers().add(gm);

        gameRepository.save(game);
        playerRepository.save(gm);

        return game.getId();
    }

    @Transactional
    public void joinGame(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        if (game.getPlayers().size() >= game.getMaxPlayers()) {
            throw new IllegalStateException("Game is full.");
        }

        // owning side = Player.games
        player.getGames().add(game);
        game.getPlayers().add(player);

        playerRepository.save(player);
    }

    /**
     * Initializes the AI conversation with a system context that sets THEME + forces JSON-only replies.
     * Also creates Round 1 and sets game.currentRound.
     */
    @Transactional
    public void startGame(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        if (game.getStatus() == GameStatus.ACTIVE) {
            throw new IllegalStateException("Game already active.");
        }

        game.setStatus(GameStatus.ACTIVE);
        game.setStartedAt(OffsetDateTime.now());

        String systemContext = buildSystemContext(game.getTheme().getTheme(), game.getMaxRounds());

        // This will produce JSON intro (type="intro") as assistant message
        List<Message> convo = deepinfraService.initConversation(systemContext);
        game.setConversationList(convo);

        // Create Round 1 immediately
        newRoundInternal(game, 1);
    }

    public String getIntro(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        List<Message> convo = game.getConversationList();
        // initConversation: [system, assistant(intro-json)]
        if (convo.size() < 2) return "";
        return convo.get(1).getContent(); // intro JSON string
    }

    /* -----------------------------
       ROUND / DILEMMA APIs
       ----------------------------- */

    /** Returns the currently stored dilemma from the current round (NO AI call). */
    public DilemmaDto getCurrentDilemma(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Round current = game.getCurrentRound();
        if (current == null) throw new IllegalStateException("No current round. Start the game first.");
        return objectMapper.convertValue(current.getDilemma(), DilemmaDto.class);
    }

    /**
     * Explicitly create the next round (e.g. Gamemaster triggers it).
     * Will prompt the AI for a new dilemma JSON and persist it to Round.dilemma and Game.conversation.
     */
    @Transactional
    public DilemmaDto newRound(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();

        int nextNumber = 1;
        if (game.getCurrentRound() != null && game.getCurrentRound().getNumber() != null) {
            nextNumber = game.getCurrentRound().getNumber() + 1;
        }

        if (nextNumber > game.getMaxRounds()) {
            throw new IllegalStateException("Max rounds reached.");
        }

        return newRoundInternal(game, nextNumber);
    }

    private DilemmaDto newRoundInternal(Game game, int roundNumber) {
        // Close previous round if exists
        Round prev = game.getCurrentRound();
        if (prev != null) prev.setActive(false);

        // Ask AI for the dilemma JSON
        List<Message> history = new ArrayList<>(game.getConversationList());
        history.add(new Message("user", buildNewRoundPrompt(roundNumber, game.getMaxRounds())));

        List<Message> updated = deepinfraService.chatConversion(history);
        game.setConversationList(updated); // keep full conversation on track

        DilemmaDto dilemma = parseDilemmaFromAi(updated);

        Round round = new Round(game, roundNumber);
        round.setDilemma(objectMapper.convertValue(dilemma, Map.class));

        roundRepository.save(round);
        game.setCurrentRound(round);

        return dilemma;
    }

    /* -----------------------------
       VOTING API (stores Voting entity + keeps conversation)
       ----------------------------- */

    @Transactional
    public VoteResultDto vote(UUID gameId, VoteRequestDto req) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Round round = game.getCurrentRound();
        if (round == null) throw new IllegalStateException("No active round to vote on.");

        Player player = playerRepository.findById(req.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + req.getPlayerId()));

        // ensure player is in this game
        boolean inGame = game.getPlayers().stream().anyMatch(p -> p.getId().equals(player.getId()));
        if (!inGame) throw new IllegalArgumentException("Player is not part of this game.");

        // validate choiceId exists in current dilemma
        DilemmaDto dilemma = objectMapper.convertValue(round.getDilemma(), DilemmaDto.class);
        boolean choiceValid = dilemma.getChoices() != null &&
                dilemma.getChoices().stream().anyMatch(c -> c.getId() == req.getChoiceId());
        if (!choiceValid) throw new IllegalArgumentException("Invalid choiceId: " + req.getChoiceId());

        // prevent double vote
        if (votingRepository.existsByRound_IdAndPlayer_Id(round.getId(), player.getId())) {
            throw new IllegalArgumentException("Player already voted in this round.");
        }

        // store vote
        votingRepository.save(new Voting(round, player, req.getChoiceId()));

        // keep conversation "on track" (record the action in the chat history)
        List<Message> history = new ArrayList<>(game.getConversationList());
        history.add(new Message(
                "user",
                "{\"type\":\"vote\",\"round\":" + round.getNumber() +
                        ",\"playerId\":\"" + player.getId() +
                        "\",\"choiceId\":" + req.getChoiceId() + "}"
        ));
        game.setConversationList(history);

        // compute counts
        List<Voting> votes = votingRepository.findAllByRound_Id(round.getId());
        Map<Integer, Long> counts = votes.stream()
                .collect(Collectors.groupingBy(Voting::getChoiceId, Collectors.counting()));

        VoteResultDto result = new VoteResultDto();
        result.setAccepted(true);
        result.setRoundNumber(round.getNumber());
        result.setCounts(counts);

        long expectedVotes = game.getPlayers().size();
        boolean completed = expectedVotes > 0 && votes.size() >= expectedVotes;
        result.setRoundCompleted(completed);

        // If round isn't complete yet, stop here (no AI call).
        if (!completed) {
            result.setNextDilemma(null);
            return result;
        }

        // Round completed: ask AI for outcome JSON and append to conversation
        List<Message> outcomeHistory = new ArrayList<>(game.getConversationList());
        outcomeHistory.add(new Message("user", buildOutcomePrompt(round.getNumber(), counts)));

        List<Message> updated = deepinfraService.chatConversion(outcomeHistory);
        game.setConversationList(updated);

        // Close round
        round.setActive(false);

        // End game if last round
        if (round.getNumber() >= game.getMaxRounds()) {
            game.setStatus(GameStatus.ENDED);
            game.setEndedAt(OffsetDateTime.now());
            result.setNextDilemma(null);
            return result;
        }

        // Auto-create next round (optional; remove this if you want GM to trigger newRound manually)
        DilemmaDto next = newRoundInternal(game, round.getNumber() + 1);
        result.setNextDilemma(next);
        return result;
    }

    /* -----------------------------
       Helpers
       ----------------------------- */

    private String buildSystemContext(String themeName, int maxRounds) {
        return """
        You are the narrator for a dilemma voting game.

        THEME: %s

        CRITICAL OUTPUT RULE:
        - You MUST reply with ONLY valid JSON.
        - No markdown, no backticks, no extra text.

        Use these schemas:

        Intro:
        {
          "type": "intro",
          "theme": string,
          "message": string
        }

        Dilemma:
        {
          "type": "dilemma",
          "id": number,
          "title": string,
          "context": string,
          "choices": [
            { "id": number, "title": string, "description": string }
          ]
        }
        Rules: exactly 4 choices, concise text.

        Outcome:
        {
          "type": "outcome",
          "round": number,
          "winningChoiceId": number,
          "summary": string
        }

        The game has %d rounds total.
        """.formatted(themeName, maxRounds);
    }

    private String buildNewRoundPrompt(int roundNumber, int maxRounds) {
        return "Create the dilemma for round %d of %d. Respond with Dilemma JSON only."
                .formatted(roundNumber, maxRounds);
    }

    private String buildOutcomePrompt(int roundNumber, Map<Integer, Long> counts) {
        return """
        The round %d voting has completed.
        Vote counts: %s

        Produce Outcome JSON only (type="outcome"):
        - round
        - winningChoiceId
        - summary (1-2 sentences)
        """.formatted(roundNumber, counts);
    }

    private static String extractJson(String content) {
        if (content == null) return "";
        int start = content.indexOf("```");
        if (start >= 0) {
            int jsonStart = content.indexOf("{", start);
            int endFence = content.indexOf("```", start + 3);
            if (jsonStart >= 0 && endFence > jsonStart) {
                return content.substring(jsonStart, endFence).trim();
            }
        }
        return content.trim();
    }

    private DilemmaDto parseDilemmaFromAi(List<Message> updated) {
        Message last = updated.get(updated.size() - 1);
        String json = extractJson(last.getContent());
        try {
            return objectMapper.readValue(json, DilemmaDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("AI returned invalid dilemma JSON. Raw:\n" + last.getContent(), e);
        }
    }
}
