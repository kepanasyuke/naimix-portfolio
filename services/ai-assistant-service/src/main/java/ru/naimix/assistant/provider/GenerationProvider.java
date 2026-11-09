package ru.naimix.assistant.provider;

import ru.naimix.assistant.api.dto.CoverLetterRequest;
import ru.naimix.assistant.api.dto.GenerationView;
import ru.naimix.assistant.api.dto.VacancyDescriptionRequest;

/**
 * Text generation backend used by the assistant.
 */
public interface GenerationProvider {

    /**
     * Builds a vacancy description.
     *
     * @param request generation request
     * @return generated text
     */
    GenerationView describe(VacancyDescriptionRequest request);

    /**
     * Builds a cover letter.
     *
     * @param request generation request
     * @return generated text
     */
    GenerationView coverLetter(CoverLetterRequest request);
}
