package ru.naimix.billing.error;

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

    private static final String NOT_FOUND = "not_found";
    private static final String INVALID = "invalid_request";
    private static final String FUNDS = "insufficient_funds";
    private static final String CONFLICT = "hold_state_conflict";

    /**
     * Handles missing wallets and holds.
     *
     * @param exception failure
     * @return 404 response
     */
    @ExceptionHandler({WalletNotFoundException.class, HoldNotFoundException.class})
    public ResponseEntity<ApiError> onNotFound(final RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(NOT_FOUND, exception.getMessage()));
    }

    /**
     * Handles rejected holds.
     *
     * @param exception failure
     * @return 409 response
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiError> onInsufficientFunds(final InsufficientFundsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(FUNDS, exception.getMessage()));
    }

    /**
     * Handles repeated capture or release.
     *
     * @param exception failure
     * @return 409 response
     */
    @ExceptionHandler(IllegalHoldStateException.class)
    public ResponseEntity<ApiError> onIllegalState(final IllegalHoldStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(CONFLICT, exception.getMessage()));
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
