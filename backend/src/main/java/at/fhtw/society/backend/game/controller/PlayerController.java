package at.fhtw.society.backend.game.controller;

import at.fhtw.society.backend.game.dto.CreatePlayerDto;
import at.fhtw.society.backend.game.dto.PlayerDto;
import at.fhtw.society.backend.game.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<Object> getAllPlayers() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", playerService.getAllPlayers()
        ));
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<Object> getPlayer(@PathVariable UUID playerId) {
        try {
            PlayerDto dto = playerService.getPlayer(playerId);
            return ResponseEntity.ok(Map.of("status", "success", "data", dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<Object> getByUsername(@PathVariable String username) {
        try {
            PlayerDto dto = playerService.getByUsername(username);
            return ResponseEntity.ok(Map.of("status", "success", "data", dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createPlayer(@RequestBody CreatePlayerDto dto) {
        try {
            UUID id = playerService.createPlayer(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "data", Map.of("playerId", id)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<Object> deletePlayer(@PathVariable UUID playerId) {
        try {
            playerService.deletePlayer(playerId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", Map.of("deletedPlayerId", playerId)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
