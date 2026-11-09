package ru.naimix.matching.text;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Inverse document frequency weighted vector builder.
 */
public final class TfIdfIndex {

    private static final double SMOOTHING = 1.0;

    private final Map<String, Double> idf;

    /**
     * Builds the index from a corpus.
     *
     * @param documents tokenised documents
     */
    public TfIdfIndex(final List<List<String>> documents) {
        this.idf = compute(documents);
    }

    /**
     * Converts tokens into a weighted vector.
     *
     * @param tokens document tokens
     * @return term to weight map
     */
    public Map<String, Double> vector(final List<String> tokens) {
        return weighted(TextVector.frequencies(tokens), tokens.size());
    }

    private Map<String, Double> weighted(final Map<String, Long> counts, final int total) {
        return counts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> weight(entry.getValue(), total, entry.getKey())));
    }

    private double weight(final long count, final int total, final String token) {
        return ((double) count / total) * idf.getOrDefault(token, SMOOTHING);
    }

    private static Map<String, Double> compute(final List<List<String>> documents) {
        return vocabulary(documents).stream()
                .collect(Collectors.toMap(Function.identity(), token -> idfOf(token, documents)));
    }

    private static Set<String> vocabulary(final List<List<String>> documents) {
        return documents.stream().flatMap(List::stream).collect(Collectors.toSet());
    }

    private static double idfOf(final String token, final List<List<String>> documents) {
        final long containing = documents.stream().filter(document -> document.contains(token)).count();
        return Math.log((double) documents.size() / (SMOOTHING + containing)) + SMOOTHING;
    }
}
