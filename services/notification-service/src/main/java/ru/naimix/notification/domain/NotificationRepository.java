package ru.naimix.notification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Storage of notifications.
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Finds a notification by its idempotency key.
     *
     * @param dedupKey idempotency key
     * @return notification when present
     */
    Optional<Notification> findByDedupKey(String dedupKey);

    /**
     * Lists notifications of a recipient.
     *
     * @param recipient address
     * @return notifications ordered by storage order
     */
    List<Notification> findByRecipientIgnoreCase(String recipient);
}
