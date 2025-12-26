package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class LobbyNotFoundException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/lobby-not-found";
    private static final String TITLE = "Lobby Not Found";

    public LobbyNotFoundException(UUID lobbyId) {
        super(HttpStatus.NOT_FOUND, TYPE_URI, TITLE, "Lobby not found: " + lobbyId);
    }
}
