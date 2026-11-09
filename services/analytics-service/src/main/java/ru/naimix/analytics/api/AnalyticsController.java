package ru.naimix.analytics.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.analytics.api.dto.EventRequest;
import ru.naimix.analytics.api.dto.JvmView;
import ru.naimix.analytics.api.dto.SummaryView;
import ru.naimix.analytics.service.EventService;
import ru.naimix.analytics.service.JvmProbe;

/**
 * Analytics endpoints.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final EventService eventService;
    private final JvmProbe jvmProbe;

    /**
     * Creates the controller.
     *
     * @param eventService event service
     * @param jvmProbe JVM metrics probe
     */
    public AnalyticsController(final EventService eventService, final JvmProbe jvmProbe) {
        this.eventService = eventService;
        this.jvmProbe = jvmProbe;
    }

    /**
     * Records an event.
     *
     * @param request event payload
     * @return empty 202 response
     */
    @PostMapping("/events")
    public ResponseEntity<Void> record(@Valid @RequestBody final EventRequest request) {
        eventService.record(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * Returns the aggregation report.
     *
     * @return summary of all counters
     */
    @GetMapping("/summary")
    public SummaryView summary() {
        return eventService.summary();
    }

    /**
     * Returns JVM metrics.
     *
     * @return metrics snapshot
     */
    @GetMapping("/jvm")
    public JvmView jvm() {
        return jvmProbe.snapshot();
    }
}
