package ru.naimix.assistant.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.assistant.api.dto.CoverLetterRequest;
import ru.naimix.assistant.api.dto.GenerationView;
import ru.naimix.assistant.api.dto.VacancyDescriptionRequest;
import ru.naimix.assistant.service.AssistantService;

/**
 * Assistant generation endpoints.
 */
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private static final String CLIENT_HEADER = "X-Client-Id";
    private static final String ANONYMOUS = "anonymous";

    private final AssistantService assistantService;

    /**
     * Creates the controller.
     *
     * @param assistantService assistant service
     */
    public AssistantController(final AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    /**
     * Generates a vacancy description.
     *
     * @param request generation request
     * @param clientId client identifier
     * @return generated text
     */
    @PostMapping("/vacancy-description")
    public GenerationView describe(@Valid @RequestBody final VacancyDescriptionRequest request,
                                   @RequestHeader(value = CLIENT_HEADER, defaultValue = ANONYMOUS)
                                   final String clientId) {
        return assistantService.describe(request, clientId);
    }

    /**
     * Generates a cover letter.
     *
     * @param request generation request
     * @param clientId client identifier
     * @return generated text
     */
    @PostMapping("/cover-letter")
    public GenerationView coverLetter(@Valid @RequestBody final CoverLetterRequest request,
                                      @RequestHeader(value = CLIENT_HEADER, defaultValue = ANONYMOUS)
                                      final String clientId) {
        return assistantService.coverLetter(request, clientId);
    }
}
