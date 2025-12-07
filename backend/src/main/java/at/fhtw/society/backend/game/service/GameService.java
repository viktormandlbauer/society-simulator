package at.fhtw.society.backend.game.service;

import at.fhtw.society.backend.ai.DeepinfraService;
import at.fhtw.society.backend.ai.Message;
import at.fhtw.society.backend.game.dto.GameDto;
import at.fhtw.society.backend.game.entity.Theme;
import at.fhtw.society.backend.game.repo.GameRepository;
import at.fhtw.society.backend.game.dto.GameStatus;
import at.fhtw.society.backend.game.entity.Game;
import at.fhtw.society.backend.game.repo.ThemeRepository;
import at.fhtw.society.backend.game.mappers.GameMapper;
import at.fhtw.society.backend.player.entity.Gamemaster;
import at.fhtw.society.backend.player.repo.GamemasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import at.fhtw.society.backend.game.dto.CreateGameDto;

import java.util.*;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final GamemasterRepository gamemasterRepository;
    private final ThemeRepository themeRepository;
    private final DeepinfraService deepinfraService;

    private final GameMapper gameMapper;

    public GameService(GameRepository gameRepository, GamemasterRepository gamemasterRepository, ThemeRepository themeRepository, DeepinfraService deepinfraService, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.gamemasterRepository = gamemasterRepository;
        this.themeRepository = themeRepository;
        this.deepinfraService = deepinfraService;
        this.gameMapper = gameMapper;
    }

    public UUID createGame(CreateGameDto createGameDto) {

        Theme theme = themeRepository.findByTheme(createGameDto.getThemeName());
        Gamemaster gm = this.gamemasterRepository.findByUsername(createGameDto.getGamemaster());
        Game newGame = new Game(gm, theme, createGameDto.getMaxRounds(), createGameDto.getMaxRounds());

        logger.info("Creating new game {}", newGame);
        this.gameRepository.save(newGame);
        return newGame.getId();
    }

    public void startGame(UUID gameId) {
        Game game = this.gameRepository.findById(gameId);

        if (game.getStatus() == GameStatus.ACTIVE) {
            throw new IllegalArgumentException("Game is already active: " + gameId);
        }

        game.setStatus(GameStatus.ACTIVE);
        Map<String, Object> conversation = new HashMap<>();

        String systemContext = "You are the AI Game Master for a turn-based social game.\n" +
                "Create dilemma questions that should be in the format with 1) Option, 2) Option, 3) Option, 4) Option.\n\n" +
                "Dilemma Theme: " + game.getTheme().getTheme() + '\n' +
                "Max rounds: " + game.getMaxrounds() + '\n';

        conversation.put("messages", this.deepinfraService.initConversation(systemContext));
        game.setConversation(conversation);
        this.gameRepository.save(game);
    }

    public String getIntro(UUID gameId) {
        Game game = this.gameRepository.findById(gameId);
        return game.getConversationList().get(0).getContent();
    }

    public String requestNewDilemma(UUID gameId) {
        Game game = this.gameRepository.findById(gameId);

        List<Message> messages =  game.getConversationList();
        
        Message newDilemmaPrompt = new Message("user", "Create a dilemma for the given context with 2 choices");
        messages.add(newDilemmaPrompt);

        List<Message> newMessages = this.deepinfraService.chatConversion(messages);
        game.setConversationList(newMessages);
        this.gameRepository.save(game);

        return newMessages.get(messages.size()).getContent();

    }

    public List<GameDto> getAllGames() {

        return null;
        //return this.gameMapper.toDtos(this.gameRepository.findAll());
    }
}
