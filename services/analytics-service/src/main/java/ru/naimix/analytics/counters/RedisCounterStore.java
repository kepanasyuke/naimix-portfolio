package ru.naimix.analytics.counters;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Redis backed counter storage for multi-instance deployments.
 */
@Repository
@ConditionalOnProperty(name = "analytics.store", havingValue = "redis")
public class RedisCounterStore implements CounterStore {

    private static final String PREFIX = "analytics:counter:";

    private final StringRedisTemplate template;

    /**
     * Creates the store.
     *
     * @param template redis template
     */
    public RedisCounterStore(final StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public void increment(final String key, final long delta) {
        template.opsForValue().increment(PREFIX + key, delta);
    }

    @Override
    public Map<String, Long> snapshot() {
        return Objects.requireNonNull(template.keys(PREFIX + "*")).stream()
                .collect(Collectors.toMap(key -> key.substring(PREFIX.length()), key -> value(key)));
    }

    private long value(final String key) {
        return Long.parseLong(Objects.requireNonNull(template.opsForValue().get(key)));
    }
}
