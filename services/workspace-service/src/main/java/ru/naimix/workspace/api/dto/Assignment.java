package ru.naimix.workspace.api.dto;

import java.math.BigDecimal;

/**
 * Result of assigning a project with an escrow hold.
 *
 * @param vacancyId project reference
 * @param holdId escrow hold
 * @param status escrow status
 * @param amount reserved amount
 * @param notificationId notification sent to the freelancer
 */
public record Assignment(String vacancyId, String holdId, String status, BigDecimal amount, String notificationId) {
}
