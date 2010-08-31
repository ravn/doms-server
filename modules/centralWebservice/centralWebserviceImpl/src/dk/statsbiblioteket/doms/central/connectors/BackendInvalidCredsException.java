package dk.statsbiblioteket.doms.central.connectors;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 30, 2010
 * Time: 12:45:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class BackendInvalidCredsException extends Exception{
    public BackendInvalidCredsException(String message) {
        super(message);
    }

    public BackendInvalidCredsException(String message, Throwable cause) {
        super(message, cause);
    }
}
