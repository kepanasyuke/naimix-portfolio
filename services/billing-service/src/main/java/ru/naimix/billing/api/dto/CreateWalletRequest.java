package ru.naimix.billing.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Wallet creation payload.
 *
 * @param ownerId wallet owner
 * @param initialBalance opening amount
 * @param currency ISO currency code
 */
public record CreateWalletRequest(
        @NotNull UUID ownerId,
        @NotNull @DecimalMin("0.0") BigDecimal initialBalance,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency) {
}
