package at.fhtw.society.backend.game.controller;


import at.fhtw.society.backend.game.dto.CreateGameDto;
import at.fhtw.society.backend.game.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<Object> getGames() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", this.gameService.getAllGames()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Failed to fetch games: " + e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createGame(@RequestBody CreateGameDto createGameDto) {
        try {

            UUID gameId = this.gameService.createGame(createGameDto);

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

    @PostMapping("/start/{gameId}")
    public ResponseEntity<Object> startGame(@PathVariable UUID gameId) {
        try {

            this.gameService.startGame(gameId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", "Successfully started game with id: " + gameId
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Failed to start game with id: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{gameId}/intro")
    public ResponseEntity<Object> getIntro(@PathVariable UUID gameId) {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", this.gameService.getIntro(gameId)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Failed to start game with id: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{gameId}/dilemma")
    public ResponseEntity<Object> requestDilemma(@PathVariable UUID gameId) {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", this.gameService.requestNewDilemma(gameId)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Failed to start game with id: " + e.getMessage()
            ));
        }
    }
}
