// src/main/java/at/fhtw/society/backend/player/PlayerController.java
package at.fhtw.society.backend.player;

import at.fhtw.society.backend.player.dto.RegisterRequest;
import at.fhtw.society.backend.player.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/gamemaster/register")
    public ResponseEntity<Object> registerGamemaster(@Valid @RequestBody RegisterRequest req) {
        try {
            long id = registrationService.registerGamemaster(req.username());
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

    @PostMapping("/register")
    public ResponseEntity<Object> registerPlayer(@Valid @RequestBody RegisterRequest req) {
        try {
            long id = registrationService.registerPlayer(req.username());
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
