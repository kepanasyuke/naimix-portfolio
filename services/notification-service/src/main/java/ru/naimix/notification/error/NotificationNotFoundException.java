package ru.naimix.notification.error;

import java.util.UUID;

/**
 * Raised when a notification does not exist.
 */
public class NotificationNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param id missing identifier
     */
    public NotificationNotFoundException(final UUID id) {
        super("notification not found: " + id);
    }
}
