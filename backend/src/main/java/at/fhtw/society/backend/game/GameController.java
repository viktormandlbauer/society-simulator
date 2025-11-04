package at.fhtw.society.backend.game;


import at.fhtw.society.backend.game.dto.CreateGameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createGame(@RequestBody CreateGameDto createGameDto) {
        try {

            long gameId = this.gameService.createGame(createGameDto);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", "Successfully created game with id: " + gameId
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Failed to create game: " + e.getMessage()
            ));
        }
    }


}
