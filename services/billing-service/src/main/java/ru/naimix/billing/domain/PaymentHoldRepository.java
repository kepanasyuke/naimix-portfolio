package ru.naimix.billing.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Storage of payment holds.
 */
public interface PaymentHoldRepository extends JpaRepository<PaymentHold, UUID> {

    /**
     * Finds a hold by its idempotency key.
     *
     * @param idempotencyKey client supplied key
     * @return hold when present
     */
    Optional<PaymentHold> findByIdempotencyKey(String idempotencyKey);
}
