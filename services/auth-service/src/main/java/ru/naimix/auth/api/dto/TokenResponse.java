package ru.naimix.auth.api.dto;

/**
 * Issued access token.
 *
 * @param accessToken compact JWT
 * @param tokenType token type
 * @param expiresIn lifetime in seconds
 * @param email account email
 * @param role account role
 */
public record TokenResponse(String accessToken, String tokenType, long expiresIn, String email, String role) {
}
