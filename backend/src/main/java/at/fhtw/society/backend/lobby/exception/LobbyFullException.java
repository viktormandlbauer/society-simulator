package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class LobbyFullException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/lobby-full";
    private static final String TITLE = "Lobby Full";

    public LobbyFullException(UUID lobbyId) {
        super(HttpStatus.CONFLICT, TYPE_URI, TITLE, "Lobby is full: " + lobbyId);
    }
}
