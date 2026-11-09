package ru.naimix.assistant.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * Vacancy description generation request.
 *
 * @param title role title
 * @param city work city
 * @param skills required skills
 * @param rateMin lower rate bound
 * @param rateMax upper rate bound
 * @param tone communication tone
 */
public record VacancyDescriptionRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String city,
        @NotEmpty List<String> skills,
        @DecimalMin("0.0") BigDecimal rateMin,
        @DecimalMin("0.0") BigDecimal rateMax,
        @NotBlank String tone) {
}
