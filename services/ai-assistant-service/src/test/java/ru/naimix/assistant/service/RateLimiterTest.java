package ru.naimix.assistant.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.naimix.assistant.api.dto.GenerationView;
import ru.naimix.assistant.api.dto.VacancyDescriptionRequest;
import ru.naimix.assistant.error.RateLimitExceededException;
import ru.naimix.assistant.provider.TemplateGenerationProvider;

class RateLimiterTest {

    private static final Duration WINDOW = Duration.ofMinutes(1);
    private static final int SINGLE_REQUEST = 1;

    private final RateLimiter limiter = new RateLimiter(SINGLE_REQUEST, WINDOW);

    @Test
    void rejectsRequestsAboveLimit() {
        limiter.acquire("client");
        assertThrows(RateLimitExceededException.class, () -> limiter.acquire("client"));
    }

    @Test
    void reportsRemainingQuota() {
        limiter.acquire("client");
        assertTrue(limiter.remaining("client") <= 0);
    }

    @Test
    void templateProviderMentionsRole() {
        final GenerationView view = new TemplateGenerationProvider().describe(request());
        assertTrue(view.text().contains("Старший backend-разработчик"));
    }

    private VacancyDescriptionRequest request() {
        return new VacancyDescriptionRequest("Старший backend-разработчик", "Москва",
                List.of("Java 17", "Spring Boot"), new BigDecimal("2500"), new BigDecimal("3800"), "нейтральный");
    }
}
