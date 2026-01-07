package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class LobbyMemberNotFoundException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/lobby-member-not-found";
    private static final String TITLE = "Lobby Member Not Found";

    public LobbyMemberNotFoundException(UUID lobbyId, UUID playerId) {
        super(HttpStatus.NOT_FOUND, TYPE_URI, TITLE,
                "Player " + playerId + " is not a member of lobby " + lobbyId);
    }
}
