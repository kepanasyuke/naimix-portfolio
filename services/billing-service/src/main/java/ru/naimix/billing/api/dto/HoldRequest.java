package ru.naimix.billing.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Hold creation payload.
 *
 * @param amount amount to reserve
 * @param idempotencyKey client supplied key
 */
public record HoldRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String idempotencyKey) {
}
