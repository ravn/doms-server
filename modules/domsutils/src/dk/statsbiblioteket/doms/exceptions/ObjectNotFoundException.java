package dk.statsbiblioteket.doms.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 4, 2008
 * Time: 3:27:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObjectNotFoundException extends Exception {

    private String message;

    public ObjectNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}