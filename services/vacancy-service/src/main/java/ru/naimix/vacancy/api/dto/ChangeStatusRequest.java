package ru.naimix.vacancy.api.dto;

import jakarta.validation.constraints.Pattern;

/**
 * Status transition payload.
 *
 * @param status target status
 */
public record ChangeStatusRequest(@Pattern(regexp = "OPEN|PAUSED|CLOSED") String status) {
}
