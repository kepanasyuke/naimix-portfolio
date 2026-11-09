package ru.naimix.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/**
 * In-memory revocation list used when Redis is not available.
 */
@Repository
@ConditionalOnProperty(name = "auth.token-store", havingValue = "memory")
public class InMemoryTokenStore implements TokenStore {

    private final Map<String, Instant> revoked = new ConcurrentHashMap<>();

    @Override
    public void revoke(final String tokenId, final Duration ttl) {
        revoked.put(tokenId, Instant.now().plus(ttl));
    }

    @Override
    public boolean isRevoked(final String tokenId) {
        return revoked.containsKey(tokenId) && revoked.get(tokenId).isAfter(Instant.now());
    }
}
