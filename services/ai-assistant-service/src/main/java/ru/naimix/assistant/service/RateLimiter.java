package ru.naimix.assistant.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.naimix.assistant.error.RateLimitExceededException;

/**
 * Sliding window quota per client.
 */
@Component
public class RateLimiter {

    private final Map<String, Deque<Instant>> windows = new ConcurrentHashMap<>();
    private final int limit;
    private final Duration window;

    /**
     * Creates the limiter.
     *
     * @param limit requests per window
     * @param window window length
     */
    public RateLimiter(@Value("${assistant.rate-limit.requests:10}") final int limit,
                       @Value("${assistant.rate-limit.window:PT1M}") final Duration window) {
        this.limit = limit;
        this.window = window;
    }

    /**
     * Reserves one request or fails with a quota error.
     *
     * @param subject client identifier
     */
    public void acquire(final String subject) {
        guard(remaining(subject));
        history(subject).add(Instant.now());
    }

    /**
     * Returns the number of requests left in the current window.
     *
     * @param subject client identifier
     * @return remaining requests
     */
    public int remaining(final String subject) {
        return left(pruned(history(subject)));
    }

    private Deque<Instant> pruned(final Deque<Instant> history) {
        purge(history);
        return history;
    }

    private int left(final Deque<Instant> history) {
        return limit - history.size();
    }

    private void guard(final int remaining) {
        if (remaining <= 0) {
            throw new RateLimitExceededException(window.toSeconds());
        }
    }

    private Deque<Instant> history(final String subject) {
        return windows.computeIfAbsent(subject, key -> new ConcurrentLinkedDeque<>());
    }

    private void purge(final Deque<Instant> history) {
        history.removeIf(instant -> instant.isBefore(Instant.now().minus(window)));
    }
}
