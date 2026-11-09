package ru.naimix.billing.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public projection of a wallet.
 *
 * @param id identifier
 * @param ownerId owner identifier
 * @param balance current balance
 * @param currency currency code
 */
public record WalletView(UUID id, UUID ownerId, BigDecimal balance, String currency) {
}
