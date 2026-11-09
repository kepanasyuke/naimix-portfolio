package ru.naimix.workspace.gateway;

import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Typed access to every platform service over HTTP.
 */
@Component
public class ServiceGateway {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient assistant;
    private final RestClient vacancy;
    private final RestClient matching;
    private final RestClient billing;
    private final RestClient notification;
    private final RestClient analytics;

    /**
     * Creates the gateway.
     *
     * @param assistant assistant client
     * @param vacancy vacancy client
     * @param matching matching client
     * @param billing billing client
     * @param notification notification client
     * @param analytics analytics client
     */
    public ServiceGateway(@Qualifier("assistantClient") final RestClient assistant,
                          @Qualifier("vacancyClient") final RestClient vacancy,
                          @Qualifier("matchingClient") final RestClient matching,
                          @Qualifier("billingClient") final RestClient billing,
                          @Qualifier("notificationClient") final RestClient notification,
                          @Qualifier("analyticsClient") final RestClient analytics) {
        this.assistant = assistant;
        this.vacancy = vacancy;
        this.matching = matching;
        this.billing = billing;
        this.notification = notification;
        this.analytics = analytics;
    }

    /**
     * Generates a vacancy description.
     *
     * @param payload assistant payload
     * @return generated text
     */
    public Map<String, Object> describe(final Map<String, Object> payload) {
        return assistant.post().uri("/api/assistant/vacancy-description").body(payload).retrieve().body(MAP);
    }

    /**
     * Publishes a vacancy.
     *
     * @param payload vacancy payload
     * @return stored vacancy
     */
    public Map<String, Object> createVacancy(final Map<String, Object> payload) {
        return vacancy.post().uri("/api/vacancies").body(payload).retrieve().body(MAP);
    }

    /**
     * Returns the total number of vacancies.
     *
     * @return total count
     */
    public Map<String, Object> vacancies() {
        return vacancy.get().uri("/api/vacancies?page=0&size=1").retrieve().body(MAP);
    }

    /**
     * Ranks freelancers against a text.
     *
     * @param payload match payload
     * @return ranked candidates
     */
    public List<Map<String, Object>> match(final Map<String, Object> payload) {
        return matching.post().uri("/api/match").body(payload).retrieve().body(LIST);
    }

    /**
     * Opens a wallet.
     *
     * @param payload wallet payload
     * @return stored wallet
     */
    public Map<String, Object> createWallet(final Map<String, Object> payload) {
        return billing.post().uri("/api/billing/wallets").body(payload).retrieve().body(MAP);
    }

    /**
     * Reserves funds for a project.
     *
     * @param walletId wallet identifier
     * @param payload hold payload
     * @return created hold
     */
    public Map<String, Object> hold(final String walletId, final Map<String, Object> payload) {
        return billing.post().uri("/api/billing/wallets/{id}/holds", walletId).body(payload).retrieve().body(MAP);
    }

    /**
     * Captures a hold.
     *
     * @param holdId hold identifier
     * @return updated hold
     */
    public Map<String, Object> capture(final String holdId) {
        return billing.post().uri("/api/billing/holds/{id}/capture", holdId).retrieve().body(MAP);
    }

    /**
     * Releases a hold.
     *
     * @param holdId hold identifier
     * @return updated hold
     */
    public Map<String, Object> release(final String holdId) {
        return billing.post().uri("/api/billing/holds/{id}/release", holdId).retrieve().body(MAP);
    }

    /**
     * Accepts a notification exactly once.
     *
     * @param payload notification payload
     * @param idempotencyKey idempotency key
     * @return stored notification
     */
    public Map<String, Object> notify(final Map<String, Object> payload, final String idempotencyKey) {
        return notification.post().uri("/api/notifications").header("Idempotency-Key", idempotencyKey)
                .body(payload).retrieve().body(MAP);
    }

    /**
     * Delivers a notification.
     *
     * @param notificationId notification identifier
     * @return updated notification
     */
    public Map<String, Object> deliver(final String notificationId) {
        return notification.post().uri("/api/notifications/{id}/deliver", notificationId).retrieve().body(MAP);
    }

    /**
     * Records an analytics event.
     *
     * @param name event name
     */
    public void recordEvent(final String name) {
        analytics.post().uri("/api/analytics/events").body(Map.of("name", name)).retrieve().toBodilessEntity();
    }

    /**
     * Returns the analytics summary.
     *
     * @return aggregated counters
     */
    public Map<String, Object> summary() {
        return analytics.get().uri("/api/analytics/summary").retrieve().body(MAP);
    }
}
