package at.fhtw.society.backend.game;

import at.fhtw.society.backend.ai.DeepinfraService;
import at.fhtw.society.backend.game.model.Game;
import org.springframework.stereotype.Service;
import at.fhtw.society.backend.game.dto.CreateGameDto;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final DeepinfraService deepinfraService;

    public GameService(GameRepository gameRepository, DeepinfraService deepinfraService) {
        this.gameRepository = gameRepository;
        this.deepinfraService = deepinfraService;
    }

    public long createGame(CreateGameDto createGameDto) {

        Game newGame = new Game(createGameDto);

        this.gameRepository.save(newGame);
        return newGame.getId();
    }

    public List<Game> getGamesByStatus(GameStatus status) {
        return this.gameRepository.findAllByStatusOrderByCreatedAtDesc(String.valueOf(status));
    }
}
