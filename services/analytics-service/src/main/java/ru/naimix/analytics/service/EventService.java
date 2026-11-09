package ru.naimix.analytics.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import ru.naimix.analytics.api.dto.CountView;
import ru.naimix.analytics.api.dto.EventRequest;
import ru.naimix.analytics.api.dto.SummaryView;
import ru.naimix.analytics.counters.CounterStore;

/**
 * Event ingestion and aggregation.
 */
@Service
public class EventService {

    private static final int TOP_LIMIT = 5;

    private final CounterStore counters;

    /**
     * Creates the service.
     *
     * @param counters counter storage
     */
    public EventService(final CounterStore counters) {
        this.counters = counters;
    }

    /**
     * Records a single event.
     *
     * @param request event payload
     */
    public void record(final EventRequest request) {
        counters.increment(request.name(), request.weight());
    }

    /**
     * Builds the aggregation report.
     *
     * @return summary of all counters
     */
    public SummaryView summary() {
        return summaryOf(counters.snapshot());
    }

    private SummaryView summaryOf(final Map<String, Long> snapshot) {
        return new SummaryView(total(snapshot), top(snapshot));
    }

    private long total(final Map<String, Long> snapshot) {
        return snapshot.values().stream().mapToLong(Long::longValue).sum();
    }

    private List<CountView> top(final Map<String, Long> snapshot) {
        return snapshot.entrySet().stream().sorted(frequentFirst()).limit(TOP_LIMIT)
                .map(entry -> new CountView(entry.getKey(), entry.getValue())).toList();
    }

    private Comparator<Map.Entry<String, Long>> frequentFirst() {
        return Map.Entry.<String, Long>comparingByValue().reversed();
    }
}
