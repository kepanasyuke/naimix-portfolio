package ru.naimix.matching.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.naimix.matching.api.dto.MatchRequest;
import ru.naimix.matching.api.dto.MatchView;
import ru.naimix.matching.domain.FreelancerCatalog;

class MatchServiceTest {

    private final MatchService service = new MatchService(new FreelancerCatalog());

    @Test
    void ranksJavaProfileFirst() {
        final List<MatchView> views = service.match(new MatchRequest("java spring postgresql kafka", 3));
        assertTrue(views.get(0).skills().contains("java"));
    }

    @Test
    void neverReturnsNegativeScores() {
        final List<MatchView> views = service.match(new MatchRequest("нерелевантный набор слов без стека", 5));
        assertTrue(views.stream().allMatch(view -> view.score() >= 0));
    }
}
