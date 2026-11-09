package ru.naimix.matching.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class SimilarityTest {

    private static final double DELTA = 0.0001;

    @Test
    void identicalVectorsAreFullySimilar() {
        final Map<String, Double> vector = Map.of("java", 1.0, "spring", 2.0);
        assertEquals(1.0, Similarity.cosine(vector, vector), DELTA);
    }

    @Test
    void disjointVectorsAreNotSimilar() {
        assertEquals(0.0, Similarity.cosine(java(), design()), DELTA);
    }

    private Map<String, Double> java() {
        return Map.of("java", 1.0);
    }

    private Map<String, Double> design() {
        return Map.of("design", 1.0);
    }
}
