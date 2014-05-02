package dk.statsbiblioteket.doms.central.connectors.builderpattern;

import java.util.Date;


//TODO altIDs
public class DatastreamBuilder {
    private String id;
    private String label;
    private String versionID;
    private Date creationDate;
    private State state;
    private String mimetype;
    private String formatURI;
    private Datastream.ControlGroup controlGroup;
    private boolean versionable;
    private String location;
    private String content;
    private String checksumType;
    private String checksum;

    private DatastreamBuilder(Datastream.ControlGroup controlGroup) {
        this.controlGroup = controlGroup;
    }

    public static DatastreamBuilder inlineDatastream() {
        return new DatastreamBuilder(Datastream.ControlGroup.Inline);
    }

    public static DatastreamBuilder managedDatastream() {
        return new DatastreamBuilder(Datastream.ControlGroup.Managed);
    }

    public static DatastreamBuilder externalDatastream() {
        return new DatastreamBuilder(Datastream.ControlGroup.External);
    }

    public static DatastreamBuilder redirectDatastream() {
        return new DatastreamBuilder(Datastream.ControlGroup.Redirect);
    }

    public static DatastreamBuilder existing(Datastream datastream) {
        DatastreamBuilder builder = new DatastreamBuilder(datastream.controlGroup);
        return builder.id(datastream.id)
                      .label(datastream.label)
                      .versionid(datastream.versionID)
                      .state(datastream.state)
                      .mimetype(datastream.mimetype)
                      .formaturi(datastream.formatURI)
                      .versionable(datastream.versionable)
                      .location(datastream.location)
                      .content(datastream.content)
                      .checksumtype(datastream.checksumType)
                      .checksum(datastream.checksum);
    }


    public DatastreamBuilder id(String id) {
        this.id = id;
        return this;
    }

    public DatastreamBuilder label(String label) {
        this.label = label;
        return this;
    }

    public DatastreamBuilder versionid(String versionID) {
        this.versionID = versionID;
        return this;
    }

    public DatastreamBuilder creationdate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public DatastreamBuilder state(State state) {
        this.state = state;
        return this;
    }

    public DatastreamBuilder mimetype(String mimetype) {
        this.mimetype = mimetype;
        return this;
    }

    public DatastreamBuilder formaturi(String formatURI) {
        this.formatURI = formatURI;
        return this;
    }


    public DatastreamBuilder versionable(boolean versionable) {
        this.versionable = versionable;
        return this;
    }

    public DatastreamBuilder location(String location) {
        this.location = location;
        return this;
    }

    public DatastreamBuilder content(String content) {
        this.content = content;
        return this;
    }

    public DatastreamBuilder checksumtype(String checksumType) {
        this.checksumType = checksumType;
        return this;
    }

    public DatastreamBuilder checksum(String checksum) {
        this.checksum = checksum;
        return this;
    }

    public Datastream build() {
        return new Datastream(
                id,
                label,
                versionID,
                creationDate,
                state,
                mimetype,
                formatURI,
                controlGroup,
                versionable,
                location,
                content,
                checksumType,
                checksum);
    }
}
