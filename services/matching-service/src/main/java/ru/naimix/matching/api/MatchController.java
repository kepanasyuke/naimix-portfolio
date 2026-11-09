package ru.naimix.matching.api;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.naimix.matching.api.dto.MatchRequest;
import ru.naimix.matching.api.dto.MatchView;
import ru.naimix.matching.service.MatchService;

/**
 * Matching endpoints.
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    /**
     * Creates the controller.
     *
     * @param matchService matching service
     */
    public MatchController(final MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Ranks freelancers for the given text.
     *
     * @param request match request
     * @return ordered suggestions
     */
    @PostMapping
    public List<MatchView> match(@Valid @RequestBody final MatchRequest request) {
        return matchService.match(request);
    }
}
