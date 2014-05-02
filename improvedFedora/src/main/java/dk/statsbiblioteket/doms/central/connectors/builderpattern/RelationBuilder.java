package dk.statsbiblioteket.doms.central.connectors.builderpattern;

import java.util.regex.Pattern;

public class RelationBuilder {
    private static final String PREFIX = "info:fedora/";
    public static final String DOMS_RELATIONS
            = "http://doms.statsbiblioteket.dk/relations/default/0/1/#";

    public static final String FEDORA_MODEL = "info:fedora/fedora-system:def/model#";
    private String predicate;
    private String object;
    private boolean literal;
    private String datatype;

    private RelationBuilder() {
    }

    public static RelationBuilder newRelation() {
        return new RelationBuilder();
    }


    public static Relation hasModel(String contentModelPid) {
        return newRelation().withLiteral(false)
                            .withPredicate(FEDORA_MODEL,"hasModel")
                            .withObject(uri(contentModelPid))
                            .build();
    }

    private static String uri(String pid) {
        if (!pid.startsWith(PREFIX)) {
            return PREFIX + pid;
        }
        return pid;
    }

    private static String pid(String uri) {
        if (!uri.startsWith(PREFIX)) {
            return uri.replaceFirst("^"+ Pattern.quote(PREFIX),"");
        }
        return uri;
    }


    public static Relation isPartOfCollection(String collectionPid) {
        return newRelation().withLiteral(false)
                            .withPredicate(DOMS_RELATIONS,"isPartOfCollection")
                            .withObject(uri(collectionPid))
                            .build();
    }

    public static Relation hasPart(String pid) {
        return newRelation().withLiteral(false)
                            .withPredicate(DOMS_RELATIONS,"hasPart")
                            .withObject(uri(pid))
                            .build();
    }


    public RelationBuilder withPredicate(String predicate) {
        this.predicate = predicate;
        return this;
    }

    public RelationBuilder withPredicate(String namespace,String shortName) {
        this.predicate = namespace+shortName;
        return this;
    }


    public RelationBuilder withObject(String object) {
        this.object = object;
        return this;
    }

    public RelationBuilder withLiteral(boolean literal) {
        this.literal = literal;
        return this;
    }

    public RelationBuilder withDatatype(String datatype) {
        this.datatype = datatype;
        return this;
    }

    public Relation build() {
        Relation relation = new Relation(predicate, object, literal, datatype);
        return relation;
    }
}
