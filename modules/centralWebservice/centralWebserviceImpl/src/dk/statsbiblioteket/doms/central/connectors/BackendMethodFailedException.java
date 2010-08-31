package dk.statsbiblioteket.doms.central.connectors;

import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 27, 2010
 * Time: 1:04:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class BackendMethodFailedException extends Exception {
    public BackendMethodFailedException(String message) {
        super(message);
    }

    public BackendMethodFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
