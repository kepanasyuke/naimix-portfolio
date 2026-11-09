package ru.naimix.workspace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * HTTP clients for the platform services.
 */
@Configuration
public class ClientsConfig {

    /**
     * Client of the auth service.
     *
     * @param url base url
     * @return configured client
     */
    @Bean
    public RestClient authClient(@Value("${services.auth-url}") final String url) {
        return clientFor(url);
    }

    /**
     * Client of the vacancy service.
     *
     * @param url base url
     * @return configured client
     */
    @Bean
    public RestClient vacancyClient(@Value("${services.vacancy-url}") final String url) {
        return clientFor(url);
    }

    /**
     * Client of the matching service.
     *
     * @param url base url
     * @return configured client
     */
    @Bean
    public RestClient matchingClient(@Value("${services.matching-url}") final String url) {
        return clientFor(url);
    }

    /**
     * Client of the billing service.
     *
     * @param url base url
     * @return configured client
     */
    @Bean
    public RestClient billingClient(@Value("${services.billing-url}") final String url) {
        return clientFor(url);
    }

    /**
     * Client of the notification service.
     *
     * @param url base url
     * @return configured client
     */
    @Bean
    public RestClient notificationClient(@Value("${services.notification-url}") final String url) {
        return clientFor(url);
    }

    /**
     * Client of the analytics service.
     *
     * @param url base url
     * @return configured client
     */
    @Bean
    public RestClient analyticsClient(@Value("${services.analytics-url}") final String url) {
        return clientFor(url);
    }

    /**
     * Client of the assistant service.
     *
     * @param url base url
     * @return configured client
     */
    @Bean
    public RestClient assistantClient(@Value("${services.assistant-url}") final String url) {
        return clientFor(url);
    }

    private RestClient clientFor(final String url) {
        return RestClient.builder().baseUrl(url).build();
    }
}
