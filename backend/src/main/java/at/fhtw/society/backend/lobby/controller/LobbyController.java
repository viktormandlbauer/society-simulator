package at.fhtw.society.backend.lobby.controller;

import at.fhtw.society.backend.lobby.dto.LobbyListItemDto;
import at.fhtw.society.backend.lobby.service.LobbyQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private final LobbyQueryService lobbyQueryService;

    public LobbyController(LobbyQueryService lobbyQueryService) {
        this.lobbyQueryService = lobbyQueryService;
    }

    @GetMapping
    public ResponseEntity<List<LobbyListItemDto>> getLobbies() {
        return ResponseEntity.ok(lobbyQueryService.getLobbyList());
    }
}
