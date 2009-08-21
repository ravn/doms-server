package dk.statsbiblioteket.doms.exceptions;

/**
 * Exception wrapping any kind of trouble with illegal content in Fedora.
 * This is a non-checked exception, expected to be caught by a fault barrier.
 */
public class FedoraIllegalContentException extends RuntimeException {
    public FedoraIllegalContentException(String message) {
        super(message);
    }

    public FedoraIllegalContentException(String message, Throwable cause) {
        super(message, cause);
    }
}