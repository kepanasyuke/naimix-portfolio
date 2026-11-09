package ru.naimix.notification.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Notification intake payload.
 *
 * @param recipient address
 * @param channel delivery channel
 * @param subject short subject
 * @param body message body
 */
public record CreateNotificationRequest(
        @NotBlank @Email String recipient,
        @NotBlank @Pattern(regexp = "EMAIL|PUSH|SMS") String channel,
        @NotBlank @Size(max = 200) String subject,
        @NotBlank @Size(max = 4000) String body) {
}
