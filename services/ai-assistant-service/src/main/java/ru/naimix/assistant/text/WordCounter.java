package ru.naimix.assistant.text;

/**
 * Rough word counter used for generation statistics.
 */
public final class WordCounter {

    private static final String SEPARATOR = "\\s+";

    private WordCounter() {
    }

    /**
     * Counts words in a text.
     *
     * @param text source text
     * @return word count
     */
    public static int count(final String text) {
        return text.isBlank() ? 0 : text.trim().split(SEPARATOR).length;
    }
}
