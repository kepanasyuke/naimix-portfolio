package ru.naimix.assistant.api.dto;

/**
 * Generated text.
 *
 * @param text generated content
 * @param words word count
 * @param provider provider identifier
 * @param quotaLeft requests left in the current window
 */
public record GenerationView(String text, int words, String provider, int quotaLeft) {
}
