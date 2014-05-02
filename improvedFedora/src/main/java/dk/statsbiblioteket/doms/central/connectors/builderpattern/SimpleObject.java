package dk.statsbiblioteket.doms.central.connectors.builderpattern;

import java.util.Date;

public class SimpleObject {

    private final String pid;

    private final String label;

    private final State state;

    private final Date creationDate;

    private final Date lastModifiedDate;

    private final String owner;

    protected SimpleObject(String pid, String label, State state, Date creationDate, Date lastModifiedDate, String owner) {
        this.pid = pid;
        this.label = label;
        this.state = state;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
        this.owner = owner;
    }

    public String getPid() {
        return pid;
    }

    public String getLabel() {
        return label;
    }

    public State getState() {
        return state;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "SimpleObject{" +
               "pid='" + pid + '\'' +
               ", label='" + label + '\'' +
               ", state=" + state +
               ", creationDate=" + creationDate +
               ", lastModifiedDate=" + lastModifiedDate +
               ", owner='" + owner + '\'' +
               '}';
    }
}
