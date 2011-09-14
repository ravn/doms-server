package dk.statsbiblioteket.doms.central.connectors.fedora;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/14/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatastreamProfile {
    private String ID;

    private String label;

    private String mimeType;

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getID() {
        return ID;
    }

    public String getLabel() {
        return label;
    }

    public String getMimeType() {
        return mimeType;
    }
}
