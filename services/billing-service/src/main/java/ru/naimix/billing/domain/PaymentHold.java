package ru.naimix.billing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import ru.naimix.billing.error.IllegalHoldStateException;

/**
 * Payment hold: reserved funds that are either captured or released.
 */
@Entity
@Table(name = "payment_holds")
public class PaymentHold {

    private static final String STATUS_HELD = "HELD";
    private static final String STATUS_CAPTURED = "CAPTURED";
    private static final String STATUS_RELEASED = "RELEASED";

    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 120)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * JPA constructor.
     */
    protected PaymentHold() {
    }

    /**
     * Creates a new hold.
     *
     * @param walletId wallet identifier
     * @param amount reserved amount
     * @param idempotencyKey client supplied key
     */
    public PaymentHold(final UUID walletId, final BigDecimal amount, final String idempotencyKey) {
        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.status = STATUS_HELD;
        this.createdAt = Instant.now();
    }

    /**
     * Captures the reserved funds.
     */
    public void capture() {
        guardHeld();
        this.status = STATUS_CAPTURED;
    }

    /**
     * Releases the reserved funds.
     *
     * @return released amount
     */
    public BigDecimal release() {
        guardHeld();
        return released();
    }

    private BigDecimal released() {
        this.status = STATUS_RELEASED;
        return amount;
    }

    private void guardHeld() {
        if (!STATUS_HELD.equals(status)) {
            throw new IllegalHoldStateException(status);
        }
    }

    /**
     * Returns the hold identifier.
     *
     * @return identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the wallet identifier.
     *
     * @return wallet identifier
     */
    public UUID getWalletId() {
        return walletId;
    }

    /**
     * Returns the reserved amount.
     *
     * @return amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the hold status.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }
}
