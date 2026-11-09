package ru.naimix.notification.api;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.notification.api.dto.CreateNotificationRequest;
import ru.naimix.notification.api.dto.NotificationView;
import ru.naimix.notification.service.NotificationService;

/**
 * Notification endpoints.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Creates the controller.
     *
     * @param notificationService notification service
     */
    public NotificationController(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Accepts a notification exactly once.
     *
     * @param request intake payload
     * @param idempotencyKey client supplied key
     * @return stored notification
     */
    @PostMapping
    public ResponseEntity<NotificationView> create(@Valid @RequestBody final CreateNotificationRequest request,
                                                   @RequestHeader("Idempotency-Key") final String idempotencyKey) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(request, idempotencyKey));
    }

    /**
     * Performs one delivery attempt.
     *
     * @param id notification identifier
     * @return updated notification
     */
    @PostMapping("/{id}/deliver")
    public NotificationView deliver(@PathVariable final UUID id) {
        return notificationService.deliver(id);
    }

    /**
     * Lists notifications of a recipient.
     *
     * @param recipient address
     * @return notifications
     */
    @GetMapping
    public List<NotificationView> byRecipient(@RequestParam final String recipient) {
        return notificationService.byRecipient(recipient);
    }
}
