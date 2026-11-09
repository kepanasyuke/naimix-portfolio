package ru.naimix.billing.domain;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Storage of wallets.
 */
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    /**
     * Loads a wallet with a pessimistic write lock.
     *
     * @param id wallet identifier
     * @return locked wallet when present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select wallet from Wallet wallet where wallet.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") UUID id);

    /**
     * Finds the wallet of an owner.
     *
     * @param ownerId owner identifier
     * @return wallet when present
     */
    Optional<Wallet> findByOwnerId(UUID ownerId);
}
