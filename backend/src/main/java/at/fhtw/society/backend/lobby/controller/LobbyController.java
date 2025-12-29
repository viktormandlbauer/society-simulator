package at.fhtw.society.backend.lobby.controller;

import at.fhtw.society.backend.lobby.dto.CreateLobbyRequestDto;
import at.fhtw.society.backend.lobby.dto.LobbyListItemDto;
import at.fhtw.society.backend.lobby.dto.LobbyViewDto;
import at.fhtw.society.backend.lobby.service.LobbyCommandService;
import at.fhtw.society.backend.lobby.service.LobbyQueryService;
import at.fhtw.society.backend.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private final LobbyQueryService lobbyQueryService;
    private final JwtService jwtService;
    private final LobbyCommandService lobbyCommandService;

    public LobbyController(LobbyQueryService lobbyQueryService, JwtService jwtService, LobbyCommandService lobbyCommandService) {
        this.lobbyQueryService = lobbyQueryService;
        this.jwtService = jwtService;
        this.lobbyCommandService = lobbyCommandService;
    }

    @GetMapping
    public ResponseEntity<List<LobbyListItemDto>> getLobbies() {
        return ResponseEntity.ok(lobbyQueryService.getLobbyList());
    }

    @PostMapping
    public ResponseEntity<LobbyViewDto> createLobby(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateLobbyRequestDto request
    ) {
        var identity = jwtService.toPlayerIdentity(jwt);
        LobbyViewDto view = lobbyCommandService.createLobby(request, identity);
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }
}
