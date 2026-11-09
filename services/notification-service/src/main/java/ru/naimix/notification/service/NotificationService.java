package ru.naimix.notification.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naimix.notification.api.dto.CreateNotificationRequest;
import ru.naimix.notification.api.dto.NotificationView;
import ru.naimix.notification.domain.Notification;
import ru.naimix.notification.domain.NotificationRepository;
import ru.naimix.notification.error.NotificationNotFoundException;

/**
 * Idempotent notification intake with retry accounting.
 */
@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final ChannelSender sender;
    private final int maxAttempts;

    /**
     * Creates the service.
     *
     * @param repository notification storage
     * @param sender delivery channel
     * @param maxAttempts attempt budget
     */
    public NotificationService(final NotificationRepository repository, final ChannelSender sender,
                               @Value("${notification.max-attempts:3}") final int maxAttempts) {
        this.repository = repository;
        this.sender = sender;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Accepts a notification exactly once per idempotency key.
     *
     * @param request intake payload
     * @param idempotencyKey client supplied key
     * @return stored notification
     */
    @Transactional
    public NotificationView create(final CreateNotificationRequest request, final String idempotencyKey) {
        return toView(existing(idempotencyKey).orElseGet(() -> store(request, idempotencyKey)));
    }

    /**
     * Performs one delivery attempt.
     *
     * @param id notification identifier
     * @return updated notification
     */
    @Transactional
    public NotificationView deliver(final UUID id) {
        return toView(attempted(require(id)));
    }

    /**
     * Lists notifications of a recipient.
     *
     * @param recipient address
     * @return notifications
     */
    @Transactional(readOnly = true)
    public List<NotificationView> byRecipient(final String recipient) {
        return repository.findByRecipientIgnoreCase(recipient).stream().map(NotificationService::toView).toList();
    }

    private Optional<Notification> existing(final String idempotencyKey) {
        return repository.findByDedupKey(idempotencyKey);
    }

    private Notification store(final CreateNotificationRequest request, final String idempotencyKey) {
        return repository.save(new Notification(request.recipient(), request.channel(), request.subject(),
                request.body(), idempotencyKey));
    }

    private Notification require(final UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
    }

    private Notification attempted(final Notification notification) {
        final boolean delivered = sender.send(notification);
        return repository.save(notification.afterAttempt(delivered, maxAttempts));
    }

    private static NotificationView toView(final Notification notification) {
        return new NotificationView(notification.getId(), notification.getRecipient(), notification.getChannel(),
                notification.getSubject(), notification.getStatus(), notification.getAttempts(),
                notification.getCreatedAt());
    }
}
