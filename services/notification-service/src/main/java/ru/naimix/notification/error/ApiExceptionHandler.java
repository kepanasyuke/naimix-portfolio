package ru.naimix.notification.error;

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

    private static final String NOT_FOUND = "notification_not_found";
    private static final String INVALID = "invalid_request";

    /**
     * Handles missing notifications.
     *
     * @param exception failure
     * @return 404 response
     */
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiError> onNotFound(final NotificationNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(NOT_FOUND, exception.getMessage()));
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
