package at.fhtw.society.backend.session.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;


public class InvalidSessionRequestException extends ApiException {

    private static final String TYPE_URI =
            "https://example.com/probs/invalid-session-request";

    private static final String TITLE = "Invalid Session Request";

    public InvalidSessionRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, TYPE_URI, TITLE, message);
    }
}
