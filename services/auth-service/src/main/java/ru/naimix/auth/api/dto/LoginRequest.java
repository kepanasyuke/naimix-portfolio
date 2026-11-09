package ru.naimix.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Login payload.
 *
 * @param email account email
 * @param password raw password
 */
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
}
