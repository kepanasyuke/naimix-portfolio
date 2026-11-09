package ru.naimix.vacancy.error;

import java.util.UUID;

/**
 * Raised when a vacancy does not exist.
 */
public class VacancyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param id missing identifier
     */
    public VacancyNotFoundException(final UUID id) {
        super("vacancy not found: " + id);
    }
}
