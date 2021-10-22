package org.testcontainers.containers;

/**
 * Exception to indicate that a {@link ClientProviderStrategyReplacement} fails.
 */
public class InvalidConfigurationException extends RuntimeException {

    public InvalidConfigurationException(String s) {
        super(s);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
