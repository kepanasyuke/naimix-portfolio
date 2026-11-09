package ru.naimix.analytics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.naimix.analytics.api.dto.EventRequest;
import ru.naimix.analytics.counters.CounterStore;
import ru.naimix.analytics.counters.InMemoryCounterStore;

class EventServiceTest {

    private static final long VIEW_WEIGHT = 5L;
    private static final long MATCH_WEIGHT = 2L;
    private static final long TOTAL_WEIGHT = 7L;

    private final CounterStore counters = new InMemoryCounterStore();
    private final EventService service = new EventService(counters);

    @Test
    void putsFrequentEventFirst() {
        recordBoth();
        assertEquals("vacancy_view", service.summary().top().get(0).name());
    }

    @Test
    void sumsWeightsOfAllEvents() {
        recordBoth();
        assertEquals(TOTAL_WEIGHT, service.summary().total());
    }

    private void recordBoth() {
        service.record(new EventRequest("vacancy_view", VIEW_WEIGHT));
        service.record(new EventRequest("match_requested", MATCH_WEIGHT));
    }
}
