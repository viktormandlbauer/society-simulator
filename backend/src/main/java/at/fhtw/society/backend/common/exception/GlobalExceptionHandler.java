package at.fhtw.society.backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Produces appropriate {@link ProblemDetail} responses for:
 * - all {@link ApiException} subclasses
 * - Bean Validation errors (@Valid on request DTOs)
 * - constraint violations (@Validated on path variables / request params)
 * - malformed JSON requests / deserialization errors
 * - generic/unexpected errors (as HTTP 500)
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO: customize base URI for problem types
    private String BASE_TYPE_URI = "https://example.com/probs/";

    // -------------------------------------------------------------------------
    // ApiException Handlers (domain specific errors)
    // -------------------------------------------------------------------------

    /**
     * Handles all ApiException instances thrown in the application.
     * (e.g.
     * - InvalidGuestSessionException,
     * - ResourceNotFoundException,
     * - GameAlreadyRunningException,
     * etc.)
     * @param ex The ApiException that was thrown.
     * @param request The HttpServletRequest that resulted in the exception.
     * @return A ProblemDetail object representing the error.
     */
    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatus());
        problem.setTitle(ex.getTitle());
        problem.setDetail(ex.getMessage());

        if (ex.getType() != null && !ex.getType().isBlank()) {
            problem.setType(URI.create(ex.getType()));
        }

        enrich(problem, request);
        return problem;
    }

    // -------------------------------------------------------------------------
    // Method Argument Not Valid Exception Handler @Valid (e.g. for request bodies)
    // -------------------------------------------------------------------------

    /**
     * Handles validation errors that occur when method arguments annotated with @Valid fail validation.
     *
     * @param ex The MethodArgumentNotValidException containing validation error details.
     * @param request The HttpServletRequest that resulted in the exception.
     * @return A ProblemDetail object representing the validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation Failed");
        problem.setDetail("One or more validation errors occurred.");
        problem.setType(URI.create(BASE_TYPE_URI + "validation-failed"));

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", fieldErrors);

        enrich(problem, request);
        return problem;
    }

    // -------------------------------------------------------------------------
    // Constraint Violation Exception Handler @Validated (e.g. for path variables / request params)
    // -------------------------------------------------------------------------

    /**
     * Handles constraint violations that occur when method parameters annotated with @Validated fail validation.
     *
     * @param ex The ConstraintViolationException containing violation details.
     * @param request The HttpServletRequest that resulted in the exception.
     * @return A ProblemDetail object representing the constraint violations.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex,
                                                            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Constraint Violation");
        problem.setDetail("One or more request parameters are invalid.");
        problem.setType(URI.create(BASE_TYPE_URI + "constraint-violation"));

        Map<String, String> parameterErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            parameterErrors.put(field, violation.getMessage());
        });
        problem.setProperty("errors", parameterErrors);

        enrich(problem, request);
        return problem;
    }

    // -------------------------------------------------------------------------
    // Http Message Not Readable Exception Handler (malformed JSON / deserialization errors)
    // -------------------------------------------------------------------------

    /**
     * Handles errors that occur when the HTTP message is not readable,
     * typically due to malformed JSON or deserialization issues.
     *
     * @param ex The HttpMessageNotReadableException containing error details.
     * @param request The HttpServletRequest that resulted in the exception.
     * @return A ProblemDetail object representing the error.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                                                               HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Malformed JSON Request");
        problem.setDetail("The request body contains malformed JSON or invalid values.");
        problem.setType(URI.create(BASE_TYPE_URI + "malformed-json"));

        // include root cause message for more details (useful for enum/formatting errors)
        Throwable rootCause = ex.getRootCause();
        if (rootCause != null && rootCause.getMessage() != null) {
            problem.setProperty("error", rootCause.getMessage());
        }

        enrich(problem, request);
        return problem;
    }

    // -------------------------------------------------------------------------
    // Illegal Argument Exception Handler (generic invalid arguments)
    // -------------------------------------------------------------------------

    /**
     * Handles IllegalArgumentException instances thrown in the application.
     *
     * @param ex The IllegalArgumentException that was thrown.
     * @param request The HttpServletRequest that resulted in the exception.
     * @return A ProblemDetail object representing the error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex,
                                                        HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid Argument");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create(BASE_TYPE_URI + "invalid-argument"));

        enrich(problem, request);
        return problem;
    }

    // -------------------------------------------------------------------------
    // Generic Exception Handler (unexpected errors)
    // -------------------------------------------------------------------------

    /**
     * Handles all unexpected exceptions that are not explicitly handled elsewhere.
     *
     * @param ex The Exception that was thrown.
     * @param request The HttpServletRequest that resulted in the exception.
     * @return A ProblemDetail object representing the error.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex,
                                                     HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred. Please try again later.");
        problem.setType(URI.create(BASE_TYPE_URI + "internal-server-error"));

        enrich(problem, request);
        return problem;
    }

    // -------------------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------------------

    /**
     * Enriches the ProblemDetail with additional properties like timestamp and request path.
     * @param problem The ProblemDetail to enrich.
     * @param request The HttpServletRequest that resulted in the exception.
     */
    private void enrich(ProblemDetail problem, HttpServletRequest request) {
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("httpMethod", request.getMethod());
        problem.setInstance(URI.create(request.getRequestURI()));
    }
}
