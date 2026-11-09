package ru.naimix.auth.api.dto;

import java.util.UUID;

/**
 * Public projection of an account.
 *
 * @param id account identifier
 * @param email account email
 * @param role account role
 */
public record UserView(UUID id, String email, String role) {
}
