package ru.naimix.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Outbound notification with delivery attempts.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    private static final String STATUS_NEW = "NEW";
    private static final String STATUS_SENT = "SENT";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_DEAD = "DEAD";

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String recipient;

    @Column(nullable = false, length = 20)
    private String channel;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 4000)
    private String body;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "dedup_key", nullable = false, unique = true, length = 120)
    private String dedupKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * JPA constructor.
     */
    protected Notification() {
    }

    /**
     * Creates a queued notification.
     *
     * @param recipient address
     * @param channel delivery channel
     * @param subject short subject
     * @param body message body
     * @param dedupKey idempotency key
     */
    public Notification(final String recipient, final String channel, final String subject,
                        final String body, final String dedupKey) {
        this.id = UUID.randomUUID();
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.body = body;
        this.dedupKey = dedupKey;
        this.status = STATUS_NEW;
        this.attempts = 0;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Records a delivery attempt.
     *
     * @param delivered whether the attempt succeeded
     * @param maxAttempts attempt budget before the message is parked
     * @return updated notification
     */
    public Notification afterAttempt(final boolean delivered, final int maxAttempts) {
        this.attempts = attempts + 1;
        return settled(delivered ? STATUS_SENT : failedStatus(maxAttempts));
    }

    private String failedStatus(final int maxAttempts) {
        return attempts >= maxAttempts ? STATUS_DEAD : STATUS_FAILED;
    }

    private Notification settled(final String newStatus) {
        this.status = newStatus;
        return stamped();
    }

    private Notification stamped() {
        this.updatedAt = Instant.now();
        return this;
    }

    /**
     * Returns the identifier.
     *
     * @return identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the recipient.
     *
     * @return address
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Returns the channel.
     *
     * @return channel name
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Returns the subject.
     *
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the body.
     *
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the status.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the attempt count.
     *
     * @return attempts
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Returns the deduplication key.
     *
     * @return dedup key
     */
    public String getDedupKey() {
        return dedupKey;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
