package dk.statsbiblioteket.doms.central.connectors.builderpattern;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ObjectStructure extends SimpleObject {

    private Map<String, Datastream> datastreams;

    private Set<Relation> externalRelations;
    private Map<String, Set<Relation>> internalRelations;

    protected ObjectStructure(String pid, String label, State state, Date creationDate, Date lastModifiedDate,
                              String owner, Set<Relation> externalRelations,
                              Map<String, Set<Relation>> internalRelations, Map<String, Datastream> datastreams) {
        super(pid, label, state, creationDate, lastModifiedDate, owner);
        this.externalRelations = Collections.unmodifiableSet(externalRelations);
        this.internalRelations = Collections.unmodifiableMap(internalRelations);
        this.datastreams = Collections.unmodifiableMap(datastreams);
    }

    public Map<String, Datastream> getDatastreams() {
        return datastreams;
    }

    public Set<Relation> getExternalRelations() {
        return externalRelations;
    }

    public Map<String, Set<Relation>> getInternalRelations() {
        return internalRelations;
    }
}
