package ru.naimix.vacancy.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Vacancy creation payload.
 *
 * @param title short title
 * @param description full description
 * @param city work city
 * @param rateMin lower hourly rate bound
 * @param rateMax upper hourly rate bound
 */
public record CreateVacancyRequest(
        @NotBlank @Size(min = 5, max = 200) String title,
        @NotBlank @Size(min = 20, max = 4000) String description,
        @NotBlank String city,
        @DecimalMin("0.0") BigDecimal rateMin,
        @DecimalMin("0.0") BigDecimal rateMax) {
}
