package ru.naimix.analytics.api.dto;

import java.util.List;

/**
 * Aggregated event report.
 *
 * @param total total weight of all events
 * @param top most frequent events
 */
public record SummaryView(long total, List<CountView> top) {
}
