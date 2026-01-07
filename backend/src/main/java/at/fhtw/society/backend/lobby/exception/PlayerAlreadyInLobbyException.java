package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class PlayerAlreadyInLobbyException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/player-already-in-lobby";
    private static final String TITLE = "Player Already In Lobby";

    public PlayerAlreadyInLobbyException(UUID playerId) {
        super(HttpStatus.CONFLICT, TYPE_URI, TITLE, "Player " + playerId + " is already in another lobby");
    }
}
