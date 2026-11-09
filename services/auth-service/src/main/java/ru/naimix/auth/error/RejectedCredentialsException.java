package ru.naimix.auth.error;

/**
 * Raised when credentials cannot be verified.
 */
public class RejectedCredentialsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param reason failure description
     */
    public RejectedCredentialsException(final String reason) {
        super(reason);
    }
}
