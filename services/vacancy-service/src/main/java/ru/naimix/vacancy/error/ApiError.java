package ru.naimix.vacancy.error;

/**
 * Uniform error payload.
 *
 * @param code machine readable code
 * @param message human readable message
 */
public record ApiError(String code, String message) {
}
