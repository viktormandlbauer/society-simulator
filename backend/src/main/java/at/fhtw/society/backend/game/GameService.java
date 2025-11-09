package at.fhtw.society.backend.game;

import at.fhtw.society.backend.ai.DeepinfraService;
import at.fhtw.society.backend.game.entity.Game;
import at.fhtw.society.backend.player.entity.Gamemaster;
import at.fhtw.society.backend.player.repo.GamemasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import at.fhtw.society.backend.game.dto.CreateGameDto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final GamemasterRepository gamemasterRepository;
    private final DeepinfraService deepinfraService;

    public GameService(GameRepository gameRepository, GamemasterRepository gamemasterRepository, DeepinfraService deepinfraService) {
        this.gameRepository = gameRepository;
        this.gamemasterRepository = gamemasterRepository;
        this.deepinfraService = deepinfraService;
    }

    public UUID createGame(CreateGameDto createGameDto) {

        Gamemaster gm = this.gamemasterRepository.findById(createGameDto.getGamemasterId());

        Game newGame = new Game(createGameDto, gm);

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
        conversation.put("messages", this.deepinfraService.initConversation(game));
        game.setConversation(conversation);
        this.gameRepository.save(game);
    }


}
