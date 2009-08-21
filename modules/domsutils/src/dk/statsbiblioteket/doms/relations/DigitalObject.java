package dk.statsbiblioteket.doms.relations;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

import java.io.Serializable;

/**
 * Interface class for use in web services that need to send a title and a PID
 * of a Fedora object to a client.
 */
public class DigitalObject implements Serializable {

    private String title;
    private URI pid;

    // This is a bean, so a default constructor is mandatory.
    public DigitalObject() {
    }

    /**
     * Construct and initialise a <code>DigitalObject</code> instance.
     *
     * @param title Title of the digital object.
     * @param pid   The Fedora PID of the digital object.
     * @throws org.apache.axis.types.URI.MalformedURIException
     *          if conversion from java.net.URI to org.apache.axis.types.URI
     *          failed - which cannot fail.
     */
    public DigitalObject(String title, URI pid) throws MalformedURIException {

        setTitle(title);
        setPid(pid);
    }

    /**
     * Get the title of this digital object.
     *
     * @return Title string of this digital object.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Individually change the title of this digital object.
     *
     * @param title Title string to apply.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public URI getPid() {
        return pid;
    }

    public void setPid(URI pid) {
        this.pid = pid;
    }

    public int hashCode() {
        return pid.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof DigitalObject) {
            return pid.equals(((DigitalObject) o).getPid());
        } else {
            return false;
        }
    }

    public String toString() {
        return "title = '" + title + "'" + "  PID = '" + pid + "'";
    }
}