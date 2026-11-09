package ru.naimix.assistant.error;

/**
 * Raised when a client exceeds the generation quota.
 */
public class RateLimitExceededException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final long retryAfterSeconds;

    /**
     * Creates the exception.
     *
     * @param retryAfterSeconds seconds until the window frees up
     */
    public RateLimitExceededException(final long retryAfterSeconds) {
        super("generation quota exceeded");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * Returns the retry hint in seconds.
     *
     * @return seconds
     */
    public long retryAfterSeconds() {
        return retryAfterSeconds;
    }
}
