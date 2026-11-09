package ru.naimix.notification.service;

import ru.naimix.notification.domain.Notification;

/**
 * Delivery channel used by the notification service.
 */
public interface ChannelSender {

    /**
     * Delivers a notification.
     *
     * @param notification notification to deliver
     * @return true when delivery succeeded
     */
    boolean send(Notification notification);
}
