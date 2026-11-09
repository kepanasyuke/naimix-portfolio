package ru.naimix.billing.error;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Raised when a wallet cannot cover the requested amount.
 */
public class InsufficientFundsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param walletId wallet identifier
     * @param balance available amount
     * @param requested requested amount
     */
    public InsufficientFundsException(final UUID walletId, final BigDecimal balance, final BigDecimal requested) {
        super("wallet " + walletId + " has " + balance + ", requested " + requested);
    }
}
