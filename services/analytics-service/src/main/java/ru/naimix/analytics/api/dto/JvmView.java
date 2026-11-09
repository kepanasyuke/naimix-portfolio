package ru.naimix.analytics.api.dto;

/**
 * JVM health snapshot.
 *
 * @param heapUsed used heap in bytes
 * @param heapTotal committed heap in bytes
 * @param heapMax maximum heap in bytes
 * @param threads live thread count
 * @param uptimeMs process uptime in milliseconds
 */
public record JvmView(long heapUsed, long heapTotal, long heapMax, int threads, long uptimeMs) {
}
