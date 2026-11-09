package ru.naimix.billing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.UUID;
import ru.naimix.billing.error.InsufficientFundsException;

/**
 * Wallet of a platform participant.
 */
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false, unique = true)
    private UUID ownerId;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, length = 3)
    private String currency;

    @Version
    private long version;

    /**
     * JPA constructor.
     */
    protected Wallet() {
    }

    /**
     * Creates an empty wallet.
     *
     * @param ownerId wallet owner
     * @param initialBalance opening amount
     * @param currency ISO currency code
     */
    public Wallet(final UUID ownerId, final BigDecimal initialBalance, final String currency) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.balance = initialBalance;
        this.currency = currency;
    }

    /**
     * Reserves funds for a payment hold.
     *
     * @param amount amount to reserve
     * @return wallet identifier
     */
    public UUID hold(final BigDecimal amount) {
        guardFunds(amount);
        return reserved(amount);
    }

    private UUID reserved(final BigDecimal amount) {
        this.balance = balance.subtract(amount);
        return id;
    }

    /**
     * Returns reserved funds to the wallet.
     *
     * @param amount amount to return
     */
    public void refund(final BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    private void guardFunds(final BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(id, balance, amount);
        }
    }

    /**
     * Returns the wallet identifier.
     *
     * @return identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the wallet owner.
     *
     * @return owner identifier
     */
    public UUID getOwnerId() {
        return ownerId;
    }

    /**
     * Returns the current balance.
     *
     * @return balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Returns the currency code.
     *
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }
}
