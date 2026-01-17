package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NotGamemasterException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/not-gamemaster";
    private static final String TITLE = "Not Gamemaster";

    public NotGamemasterException(UUID lobbyId, UUID playerId) {
        super(HttpStatus.FORBIDDEN, TYPE_URI, TITLE,
                "Player " + playerId + " is not the gamemaster of lobby " + lobbyId);
    }
}
