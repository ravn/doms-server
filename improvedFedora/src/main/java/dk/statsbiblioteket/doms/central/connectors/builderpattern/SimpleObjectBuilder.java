package dk.statsbiblioteket.doms.central.connectors.builderpattern;

import java.util.Date;

public class SimpleObjectBuilder {
    private String pid;
    private String label;
    private State state;
    private Date creationDate;
    private Date lastModifiedDate;
    private String owner;

    private SimpleObjectBuilder() {
    }

    public static SimpleObjectBuilder newObject() {
        return new SimpleObjectBuilder();
    }

    public static SimpleObjectBuilder existing(SimpleObject simpleObject) {
        return new SimpleObjectBuilder().pid(simpleObject.getPid())
                                        .label(simpleObject.getLabel())
                                        .state(simpleObject.getState())
                                        .creationdate(simpleObject.getCreationDate())
                                        .lastmodifieddate(simpleObject.getLastModifiedDate())
                                        .owner(simpleObject.getOwner());
    }


    public SimpleObjectBuilder pid(String pid) {
        this.pid = pid;
        return this;
    }

    public SimpleObjectBuilder label(String label) {
        this.label = label;
        return this;
    }

    public SimpleObjectBuilder state(State state) {
        this.state = state;
        return this;
    }

    public SimpleObjectBuilder creationdate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public SimpleObjectBuilder lastmodifieddate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public SimpleObjectBuilder owner(String owner) {
        this.owner = owner;
        return this;
    }

    public SimpleObject build() {
        SimpleObject simpleObject = new SimpleObject(pid, label, state, creationDate, lastModifiedDate, owner);
        return simpleObject;
    }
}
