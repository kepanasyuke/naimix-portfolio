package ru.naimix.workspace.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * Assignment of a drafted project to a freelancer.
 *
 * @param freelancerId chosen freelancer
 * @param freelancerName display name
 * @param amount escrow amount
 */
public record AssignRequest(
        @NotBlank String freelancerId,
        @NotBlank String freelancerName,
        @DecimalMin("1.0") BigDecimal amount) {
}
