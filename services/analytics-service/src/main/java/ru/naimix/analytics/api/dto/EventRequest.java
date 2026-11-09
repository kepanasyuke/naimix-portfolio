package ru.naimix.analytics.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Event ingestion payload.
 *
 * @param name event name
 * @param value event weight, defaults to one
 */
public record EventRequest(@NotBlank String name, @Min(1) Long value) {

    /**
     * Returns the event weight with a default applied.
     *
     * @return weight
     */
    public long weight() {
        return value == null ? 1L : value;
    }
}
