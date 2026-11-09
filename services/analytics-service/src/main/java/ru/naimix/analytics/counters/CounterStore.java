package ru.naimix.analytics.counters;

import java.util.Map;

/**
 * Counter storage used by the analytics pipeline.
 */
public interface CounterStore {

    /**
     * Adds a delta to a counter.
     *
     * @param key counter name
     * @param delta increment
     */
    void increment(String key, long delta);

    /**
     * Returns a consistent snapshot of all counters.
     *
     * @return counter snapshot
     */
    Map<String, Long> snapshot();
}
