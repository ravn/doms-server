package dk.statsbiblioteket.doms.central.connectors.builderpattern;

public class Relation {

    private final String predicate;
    private final String object;
    private final boolean literal;
    private final String datatype;

    protected Relation( String predicate, String object, boolean literal, String datatype) {
        this.predicate = predicate;
        this.object = object;
        this.literal = literal;
        this.datatype = datatype;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    public boolean isLiteral() {
        return literal;
    }

    public String getDatatype() {
        return datatype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Relation)) {
            return false;
        }

        Relation relation = (Relation) o;

        if (literal != relation.literal) {
            return false;
        }
        if (datatype != null ? !datatype.equals(relation.datatype) : relation.datatype != null) {
            return false;
        }
        if (!object.equals(relation.object)) {
            return false;
        }
        if (!predicate.equals(relation.predicate)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = predicate.hashCode();
        result = 31 * result + object.hashCode();
        result = 31 * result + (literal ? 1 : 0);
        result = 31 * result + (datatype != null ? datatype.hashCode() : 0);
        return result;
    }
}
