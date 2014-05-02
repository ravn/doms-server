package dk.statsbiblioteket.doms.central.connectors.builderpattern;

import java.util.Date;

public class Datastream {

    public final String id;
    public final String label;
    public final String versionID;
    public final Date creationDate;
    public final State state;
    public final String mimetype;
    public final String formatURI;
    public final ControlGroup controlGroup;
    public final boolean versionable;
    public final String location;
    public final String content;
    public final String checksumType;
    public final String checksum;


    protected Datastream(String id, String label, String versionID, Date creationDate, State state, String mimetype,
                         String formatURI, ControlGroup controlGroup, boolean versionable, String location,
                         String content, String checksumType, String checksum) {
        this.id = id;
        this.label = label;
        this.versionID = versionID;
        this.creationDate = creationDate;
        this.state = state;
        this.mimetype = mimetype;
        this.formatURI = formatURI;
        this.controlGroup = controlGroup;
        this.versionable = versionable;
        this.location = location;
        this.content = content;
        this.checksumType = checksumType;
        this.checksum = checksum;
    }


    public static enum ControlGroup {
        Managed,
        External,
        Redirect,
        Inline

    }




    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getVersionID() {
        return versionID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public State getState() {
        return state;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getFormatURI() {
        return formatURI;
    }

    public ControlGroup getControlGroup() {
        return controlGroup;
    }

    public boolean isVersionable() {
        return versionable;
    }

    public String getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public String getChecksum() {
        return checksum;
    }

}
