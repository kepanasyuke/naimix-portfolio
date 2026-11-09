package ru.naimix.matching.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Match request payload.
 *
 * @param text vacancy description or search query
 * @param limit maximum number of matches
 */
public record MatchRequest(@NotBlank @Size(min = 10, max = 4000) String text, Integer limit) {
}
