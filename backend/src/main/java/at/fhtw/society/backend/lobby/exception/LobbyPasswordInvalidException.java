package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class LobbyPasswordInvalidException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/lobby-password-invalid";
    private static final String TITLE = "Lobby Password Invalid";

    public LobbyPasswordInvalidException(UUID lobbyId) {
        super(HttpStatus.FORBIDDEN, TYPE_URI, TITLE, "Invalid password for lobby: " + lobbyId);
    }
}
