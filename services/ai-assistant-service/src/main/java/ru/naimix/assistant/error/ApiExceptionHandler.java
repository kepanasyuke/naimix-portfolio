package ru.naimix.assistant.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates domain failures into HTTP responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String INVALID = "invalid_request";
    private static final String LIMIT = "quota_exceeded";
    private static final String RETRY_AFTER = "Retry-After";

    /**
     * Handles exhausted quotas.
     *
     * @param exception failure
     * @return 429 response with a retry hint
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiError> onRateLimit(final RateLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(RETRY_AFTER, String.valueOf(exception.retryAfterSeconds()))
                .body(new ApiError(LIMIT, exception.getMessage()));
    }

    /**
     * Handles payload validation failures.
     *
     * @param exception failure
     * @return 400 response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> onValidationFailure(final MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(new ApiError(INVALID, "payload validation failed"));
    }
}
