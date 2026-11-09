package ru.naimix.auth.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.naimix.auth.service.TokenStore;

/**
 * Rejects tokens that were revoked by logout.
 */
public class RevocationValidator implements OAuth2TokenValidator<Jwt> {

    private static final String DESCRIPTION = "token was revoked";
    private static final OAuth2Error ERROR = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, DESCRIPTION, null);

    private final TokenStore tokenStore;

    /**
     * Creates the validator.
     *
     * @param tokenStore revocation list
     */
    public RevocationValidator(final TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public OAuth2TokenValidatorResult validate(final Jwt token) {
        return revoked(token) ? OAuth2TokenValidatorResult.failure(ERROR) : OAuth2TokenValidatorResult.success();
    }

    private boolean revoked(final Jwt token) {
        return tokenStore.isRevoked(token.getId());
    }
}
