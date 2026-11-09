package ru.naimix.matching.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ranked freelancer suggestion.
 *
 * @param id profile identifier
 * @param name display name
 * @param city home city
 * @param rate hourly rate
 * @param skills declared skills
 * @param score similarity score in the range from 0 to 1
 */
public record MatchView(String id, String name, String city, BigDecimal rate, List<String> skills, double score) {
}
