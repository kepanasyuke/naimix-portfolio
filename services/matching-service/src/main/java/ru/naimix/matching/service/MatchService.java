package ru.naimix.matching.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import ru.naimix.matching.api.dto.MatchRequest;
import ru.naimix.matching.api.dto.MatchView;
import ru.naimix.matching.domain.FreelancerCatalog;
import ru.naimix.matching.domain.FreelancerProfile;
import ru.naimix.matching.text.Similarity;
import ru.naimix.matching.text.TfIdfIndex;
import ru.naimix.matching.text.TextVector;

/**
 * Ranks freelancers against a vacancy description.
 */
@Service
public class MatchService {

    private static final int DEFAULT_LIMIT = 5;
    private static final double SCORE_SCALE = 1000.0;

    private final FreelancerCatalog catalog;
    private final TfIdfIndex index;

    /**
     * Creates the service and builds the index from the catalogue.
     *
     * @param catalog freelancer catalogue
     */
    public MatchService(final FreelancerCatalog catalog) {
        this.catalog = catalog;
        this.index = new TfIdfIndex(catalog.all().stream().map(profile -> TextVector.tokens(profile.document()))
                .toList());
    }

    /**
     * Ranks freelancers against the request text.
     *
     * @param request match request
     * @return ordered suggestions
     */
    public List<MatchView> match(final MatchRequest request) {
        final Map<String, Double> target = index.vector(TextVector.tokens(request.text()));
        return catalog.all().stream().map(profile -> toView(profile, target)).sorted(bestFirst())
                .limit(limit(request)).toList();
    }

    private MatchView toView(final FreelancerProfile profile, final Map<String, Double> target) {
        return new MatchView(profile.id(), profile.name(), profile.city(), profile.rate(), profile.skills(),
                score(target, profile));
    }

    private double score(final Map<String, Double> target, final FreelancerProfile profile) {
        return rounded(Similarity.cosine(target, index.vector(TextVector.tokens(profile.document()))));
    }

    private double rounded(final double value) {
        return Math.round(value * SCORE_SCALE) / SCORE_SCALE;
    }

    private long limit(final MatchRequest request) {
        return request.limit() == null ? DEFAULT_LIMIT : request.limit();
    }

    private Comparator<MatchView> bestFirst() {
        return Comparator.comparingDouble(MatchView::score).reversed();
    }
}
