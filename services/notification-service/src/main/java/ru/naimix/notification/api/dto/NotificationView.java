package ru.naimix.notification.api.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Public projection of a notification.
 *
 * @param id identifier
 * @param recipient address
 * @param channel channel
 * @param subject subject
 * @param status status
 * @param attempts attempt count
 * @param createdAt creation timestamp
 */
public record NotificationView(UUID id, String recipient, String channel, String subject,
                               String status, int attempts, Instant createdAt) {
}
