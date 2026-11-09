package ru.naimix.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naimix.notification.domain.Notification;

/**
 * Channel implementation that writes deliveries to the application log.
 */
@Component
public class LoggingSender implements ChannelSender {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingSender.class);
    private static final String FAILURE_MARKER = "[fail]";

    @Override
    public boolean send(final Notification notification) {
        LOG.info("delivering {} to {} via {}", notification.getId(), notification.getRecipient(),
                notification.getChannel());
        return !notification.getBody().contains(FAILURE_MARKER);
    }
}
