package ru.naimix.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Registered account of the platform.
 */
@Entity
@Table(name = "accounts")
public class UserAccount {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * JPA constructor.
     */
    protected UserAccount() {
    }

    /**
     * Creates a new account.
     *
     * @param email account email
     * @param passwordHash bcrypt hash of the password
     * @param role account role
     */
    public UserAccount(final String email, final String passwordHash, final String role) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = Instant.now();
    }

    /**
     * Returns the account identifier.
     *
     * @return identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the account email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password hash.
     *
     * @return bcrypt hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Returns the account role.
     *
     * @return role name
     */
    public String getRole() {
        return role;
    }
}
