package ru.naimix.billing.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public projection of a payment hold.
 *
 * @param id identifier
 * @param walletId wallet identifier
 * @param amount reserved amount
 * @param status hold status
 */
public record HoldView(UUID id, UUID walletId, BigDecimal amount, String status) {
}
