package ru.naimix.billing.error;

import java.util.UUID;

/**
 * Raised when a hold does not exist.
 */
public class HoldNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param id missing identifier
     */
    public HoldNotFoundException(final UUID id) {
        super("hold not found: " + id);
    }
}
