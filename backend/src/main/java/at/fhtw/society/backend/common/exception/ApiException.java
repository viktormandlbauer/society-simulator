package at.fhtw.society.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

/**
 * Base class for all domain specific API exceptions that should result in a 4xx problem detail response.
 * Each subclass represents a specific error condition.
 * - HTTP status (e.g. 400, 404) is determined by the exception handler based on the exception type.
 * - a short human-readable message is provided via the exception message.
 * - a stable error type URI can be derived from the exception class name.
 * - an optional map of field-specific validation errors can be added in subclasses.
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String type;
    private final String title;

    protected ApiException(HttpStatus status, String type, String title, String message) {
        super(message);
        this.status = status;
        this.type = type;
        this.title = title;
    }
}
