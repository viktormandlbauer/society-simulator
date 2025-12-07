package at.fhtw.society.backend.player.controller;

import at.fhtw.society.backend.player.dto.AuthRequest;
import at.fhtw.society.backend.player.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/gamemaster")
    public ResponseEntity<Object> registerGamemaster(@Valid @RequestBody AuthRequest req) {
        try {
            UUID id = authService.registerGamemaster(req.username());
            return ResponseEntity
                    .created(URI.create("/api/players/gamemaster/" + id))
                    .body(Map.of(
                            "status", "success",
                            "data", Map.of("id", id, "role", "GAMEMASTER")
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/login/gamemaster")
    public ResponseEntity<Object> loginGamemaster(@Valid @RequestBody AuthRequest req) {
        try {
            UUID id = authService.loginGamemaster(req.username());
            return ResponseEntity
                    .created(URI.create("/api/players/gamemaster/" + id))
                    .body(Map.of(
                            "status", "success",
                            "data", Map.of("id", id, "role", "GAMEMASTER")
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/player")
    public ResponseEntity<Object> registerPlayer(@Valid @RequestBody AuthRequest req) {
        try {
            UUID id = authService.registerPlayer(req.username());
            return ResponseEntity
                    .created(URI.create("/api/players/player/" + id))
                    .body(Map.of(
                            "status", "success",
                            "data", Map.of("id", id, "role", "PLAYER")
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
