package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import at.fhtw.society.backend.lobby.entity.LobbyStatus;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class LobbyNotJoinableException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/lobby-not-joinable";
    private static final String TITLE = "Lobby Not Joinable";

    public LobbyNotJoinableException(UUID lobbyId, LobbyStatus lobbyStatus) {
        super(HttpStatus.FORBIDDEN, TYPE_URI, TITLE, "Lobby " + lobbyId + " is not joinable. Current status: " + lobbyStatus);
    }
}
