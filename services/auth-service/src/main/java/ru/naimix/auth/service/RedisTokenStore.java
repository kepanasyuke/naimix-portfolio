package ru.naimix.auth.service;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Redis backed revocation list.
 */
@Repository
@ConditionalOnProperty(name = "auth.token-store", havingValue = "redis", matchIfMissing = true)
public class RedisTokenStore implements TokenStore {

    private static final String PREFIX = "auth:revoked:";

    private final StringRedisTemplate template;

    /**
     * Creates the store.
     *
     * @param template redis template
     */
    public RedisTokenStore(final StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void revoke(final String tokenId, final Duration ttl) {
        template.opsForValue().set(PREFIX + tokenId, "1", ttl);
    }

    @Override
    public boolean isRevoked(final String tokenId) {
        return Boolean.TRUE.equals(template.hasKey(PREFIX + tokenId));
    }
}
