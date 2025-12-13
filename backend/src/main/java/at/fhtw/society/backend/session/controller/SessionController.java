package at.fhtw.society.backend.session.controller;

import at.fhtw.society.backend.session.dto.GuestSessionRequestDto;
import at.fhtw.society.backend.session.dto.GuestSessionResponseDto;
import at.fhtw.society.backend.session.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Create a new guest session and returns a JWT token for the player.
     * @param request the guest session request DTO containing player name and avatar ID
     * @return ResponseEntity with status 201 and the guest session response DTO
     */
    @PostMapping("/guest")
    public ResponseEntity<GuestSessionResponseDto> createGuestSession(
            @Valid @RequestBody GuestSessionRequestDto request
    ) {
        GuestSessionResponseDto response = sessionService.createGuestSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
