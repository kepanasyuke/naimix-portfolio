package ru.naimix.billing.error;

/**
 * Raised when a hold is captured or released twice.
 */
public class IllegalHoldStateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param status actual hold status
     */
    public IllegalHoldStateException(final String status) {
        super("hold is already in status " + status);
    }
}
