package ru.naimix.workspace.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

/**
 * Translates downstream and validation failures into HTTP responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String INVALID = "invalid_request";
    private static final String UPSTREAM = "service_unavailable";

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

    /**
     * Handles failures returned by platform services.
     *
     * @param exception failure
     * @return 502 response
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiError> onUpstreamFailure(final RestClientResponseException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiError(UPSTREAM, exception.getStatusCode() + ": " + exception.getResponseBodyAsString()));
    }
}
