package ru.naimix.matching.text;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Tokenisation helpers for the similarity index.
 */
public final class TextVector {

    private static final Pattern SEPARATOR = Pattern.compile("[^\\p{L}\\p{N}+#]+");
    private static final int MIN_TOKEN_LENGTH = 2;

    private TextVector() {
    }

    /**
     * Splits a text into meaningful tokens.
     *
     * @param text source text
     * @return ordered token list
     */
    public static List<String> tokens(final String text) {
        return SEPARATOR.splitAsStream(text.toLowerCase(Locale.ROOT)).filter(TextVector::meaningful).toList();
    }

    /**
     * Counts token occurrences.
     *
     * @param tokens token list
     * @return token to count map
     */
    public static Map<String, Long> frequencies(final List<String> tokens) {
        return tokens.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private static boolean meaningful(final String token) {
        return token.length() > MIN_TOKEN_LENGTH;
    }
}
