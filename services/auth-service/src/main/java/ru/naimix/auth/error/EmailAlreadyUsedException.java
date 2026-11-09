package ru.naimix.auth.error;

/**
 * Raised when an email is already registered.
 */
public class EmailAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param email duplicated email
     */
    public EmailAlreadyUsedException(final String email) {
        super("email is already registered: " + email);
    }
}
