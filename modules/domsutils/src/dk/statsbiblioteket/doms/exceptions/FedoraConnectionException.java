package dk.statsbiblioteket.doms.exceptions;

/**
 * Exception wrapping any kind of trouble connecting to Fedora. This is a
 * non-checked exception, expected to be caught by a fault barrier.
 */
public class FedoraConnectionException extends RuntimeException {
    public FedoraConnectionException(String message) {
        super(message);
    }

    public FedoraConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
