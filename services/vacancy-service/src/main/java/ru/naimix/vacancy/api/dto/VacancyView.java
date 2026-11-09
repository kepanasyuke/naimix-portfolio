package ru.naimix.vacancy.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Public projection of a vacancy.
 *
 * @param id identifier
 * @param title title
 * @param description description
 * @param city city
 * @param rateMin lower rate bound
 * @param rateMax upper rate bound
 * @param status status
 * @param createdAt creation timestamp
 */
public record VacancyView(UUID id, String title, String description, String city,
                          BigDecimal rateMin, BigDecimal rateMax, String status, Instant createdAt) {
}
