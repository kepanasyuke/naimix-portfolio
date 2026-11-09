package ru.naimix.billing.error;

import java.util.UUID;

/**
 * Raised when a wallet does not exist.
 */
public class WalletNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param id missing identifier
     */
    public WalletNotFoundException(final UUID id) {
        super("wallet not found: " + id);
    }
}
