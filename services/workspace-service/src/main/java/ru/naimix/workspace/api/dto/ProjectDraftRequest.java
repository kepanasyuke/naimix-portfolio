package ru.naimix.workspace.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * Draft request for a new business project.
 *
 * @param ownerId client identifier
 * @param title project title
 * @param city work city
 * @param skills required skills
 * @param budget hourly budget
 */
public record ProjectDraftRequest(
        @NotBlank String ownerId,
        @NotBlank String title,
        @NotBlank String city,
        @NotEmpty List<String> skills,
        @DecimalMin("1.0") BigDecimal budget) {
}
