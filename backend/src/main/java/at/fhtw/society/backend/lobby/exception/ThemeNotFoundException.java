package at.fhtw.society.backend.lobby.exception;

import at.fhtw.society.backend.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ThemeNotFoundException extends ApiException {

    private static final String TYPE_URI = "https://example.com/probs/theme-not-found";
    private static final String TITLE = "Theme Not Found";

    public ThemeNotFoundException(UUID themeId) {
        super(HttpStatus.NOT_FOUND, TYPE_URI, TITLE, "Theme not found: " + themeId);
    }
}
