package ru.naimix.auth.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Storage of registered accounts.
 */
public interface UserRepository extends JpaRepository<UserAccount, UUID> {

    /**
     * Finds an account by email.
     *
     * @param email account email
     * @return account when present
     */
    Optional<UserAccount> findByEmail(String email);

    /**
     * Checks whether an email is already registered.
     *
     * @param email account email
     * @return true when the email exists
     */
    boolean existsByEmail(String email);
}
