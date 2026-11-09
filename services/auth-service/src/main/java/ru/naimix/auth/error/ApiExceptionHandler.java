package ru.naimix.auth.error;

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
    private static final String CONFLICT = "email_conflict";
    private static final String UNAUTHORIZED = "unauthorized";

    /**
     * Handles duplicated email registration.
     *
     * @param exception failure
     * @return 409 response
     */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ApiError> onEmailConflict(final EmailAlreadyUsedException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(CONFLICT, exception.getMessage()));
    }

    /**
     * Handles wrong credentials.
     *
     * @param exception failure
     * @return 401 response
     */
    @ExceptionHandler(RejectedCredentialsException.class)
    public ResponseEntity<ApiError> onRejectedCredentials(final RejectedCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(UNAUTHORIZED, exception.getMessage()));
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
