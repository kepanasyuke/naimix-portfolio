package ru.naimix.billing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.naimix.billing.error.InsufficientFundsException;
import ru.naimix.billing.error.IllegalHoldStateException;

class WalletTest {

    private static final UUID OWNER = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    void reducesBalanceOnHold() {
        final Wallet wallet = walletWith("100.00");
        assertEquals(new BigDecimal("60.00"), balanceAfterHold(wallet));
    }

    @Test
    void rejectsHoldAboveBalance() {
        final Wallet wallet = walletWith("10.00");
        assertThrows(InsufficientFundsException.class, () -> wallet.hold(new BigDecimal("50.00")));
    }

    @Test
    void rejectsSecondCapture() {
        assertThrows(IllegalHoldStateException.class, capturedHold()::capture);
    }

    private PaymentHold capturedHold() {
        return captured(holdFor());
    }

    private PaymentHold captured(final PaymentHold hold) {
        hold.capture();
        return hold;
    }

    private BigDecimal balanceAfterHold(final Wallet wallet) {
        wallet.hold(new BigDecimal("40.00"));
        return wallet.getBalance();
    }

    private Wallet walletWith(final String balance) {
        return new Wallet(OWNER, new BigDecimal(balance), "RUB");
    }

    private PaymentHold holdFor() {
        return new PaymentHold(OWNER, new BigDecimal("40.00"), "key-1");
    }
}
