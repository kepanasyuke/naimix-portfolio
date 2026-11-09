package ru.naimix.assistant.service;

import org.springframework.stereotype.Service;
import ru.naimix.assistant.api.dto.CoverLetterRequest;
import ru.naimix.assistant.api.dto.GenerationView;
import ru.naimix.assistant.api.dto.VacancyDescriptionRequest;
import ru.naimix.assistant.provider.GenerationProvider;

/**
 * Quota-aware facade over the generation provider.
 */
@Service
public class AssistantService {

    private final GenerationProvider provider;
    private final RateLimiter limiter;

    /**
     * Creates the service.
     *
     * @param provider generation backend
     * @param limiter client quota
     */
    public AssistantService(final GenerationProvider provider, final RateLimiter limiter) {
        this.provider = provider;
        this.limiter = limiter;
    }

    /**
     * Generates a vacancy description.
     *
     * @param request generation request
     * @param subject client identifier
     * @return generated text with quota details
     */
    public GenerationView describe(final VacancyDescriptionRequest request, final String subject) {
        limiter.acquire(subject);
        return withQuota(provider.describe(request), subject);
    }

    /**
     * Generates a cover letter.
     *
     * @param request generation request
     * @param subject client identifier
     * @return generated text with quota details
     */
    public GenerationView coverLetter(final CoverLetterRequest request, final String subject) {
        limiter.acquire(subject);
        return withQuota(provider.coverLetter(request), subject);
    }

    private GenerationView withQuota(final GenerationView view, final String subject) {
        return new GenerationView(view.text(), view.words(), view.provider(), limiter.remaining(subject));
    }
}
