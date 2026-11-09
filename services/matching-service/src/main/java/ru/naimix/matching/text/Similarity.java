package ru.naimix.matching.text;

import java.util.Map;

/**
 * Cosine similarity of weighted vectors.
 */
public final class Similarity {

    private Similarity() {
    }

    /**
     * Computes cosine similarity of two weighted vectors.
     *
     * @param left first vector
     * @param right second vector
     * @return similarity in the range from 0 to 1
     */
    public static double cosine(final Map<String, Double> left, final Map<String, Double> right) {
        final double denominator = norm(left) * norm(right);
        return denominator == 0 ? 0 : dot(left, right) / denominator;
    }

    private static double dot(final Map<String, Double> left, final Map<String, Double> right) {
        return left.entrySet().stream().filter(entry -> right.containsKey(entry.getKey()))
                .mapToDouble(entry -> entry.getValue() * right.get(entry.getKey())).sum();
    }

    private static double norm(final Map<String, Double> vector) {
        return Math.sqrt(vector.values().stream().mapToDouble(value -> value * value).sum());
    }
}
