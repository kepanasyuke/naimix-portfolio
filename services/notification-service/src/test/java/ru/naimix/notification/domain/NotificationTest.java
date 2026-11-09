package ru.naimix.notification.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NotificationTest {

    private static final int SINGLE_ATTEMPT = 1;

    @Test
    void parksNotificationAfterFailedBudget() {
        final Notification notification = sample();
        assertEquals("DEAD", notification.afterAttempt(false, SINGLE_ATTEMPT).getStatus());
    }

    @Test
    void marksNotificationAsSentOnSuccess() {
        final Notification notification = sample();
        assertEquals("SENT", notification.afterAttempt(true, SINGLE_ATTEMPT).getStatus());
    }

    private Notification sample() {
        return new Notification("anna@example.com", "EMAIL", "Задача назначена", "Текст уведомления", "key-1");
    }
}
