package ru.naimix.auth.service;

import java.time.Duration;

/**
 * Storage of revoked access tokens.
 */
public interface TokenStore {

    /**
     * Marks a token as revoked until it expires.
     *
     * @param tokenId token identifier
     * @param ttl remaining token lifetime
     */
    void revoke(String tokenId, Duration ttl);

    /**
     * Checks whether a token was revoked.
     *
     * @param tokenId token identifier
     * @return true when the token is revoked
     */
    boolean isRevoked(String tokenId);
}
