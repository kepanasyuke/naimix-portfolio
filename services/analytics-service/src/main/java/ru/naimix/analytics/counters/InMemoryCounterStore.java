package ru.naimix.analytics.counters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/**
 * In-process counter storage used when Redis is not configured.
 */
@Repository
@ConditionalOnProperty(name = "analytics.store", havingValue = "memory", matchIfMissing = true)
public class InMemoryCounterStore implements CounterStore {

    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

    @Override
    public void increment(final String key, final long delta) {
        counters.computeIfAbsent(key, ignored -> new AtomicLong()).addAndGet(delta);
    }

    @Override
    public Map<String, Long> snapshot() {
        return counters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }
}
