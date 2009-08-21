package dk.statsbiblioteket.doms.contentmodels;

import dk.statsbiblioteket.util.qa.QAInfo;

/** Constants for use in Content Model handling */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "kfc",
        reviewers = {"jrg"})
public class ContentModelConstants {
    /** Utility class, do not instantiate. */
    private ContentModelConstants() {
    }

    // Names of datastreams used when describing content models.

    /** The name of the datastream containing ontology information. */
    public static final String ONTOLOGY_DATASTREAM = "ONTOLOGY";
    /** The name of the datastream containing datastream information. */
    public static final String DS_COMPOSITE_MODEL_DATASTREAM
            = "DS-COMPOSITE-MODEL";
    /** The name of the datastream containing view information */
    public static final String VIEW_DATASTREAM = "VIEW";

    // Properties used when describing ontologies

    /** The owl property defining a class */
    public static final String RDF_PROPERTY_OWL_CLASS = "owl:Class";

    /** The rdf property defining rdf properties. */
    public static final String RDF_PROPERTY_RDF_ABOUT = "rdf:about";

    /** The RDFS property defining that one class is a subclass of another. */
    public static final String RDF_PROPERTY_RDFS_SUB_CLASS_OF
            = "rdfs:subClassOf";

    /** The RDF property defining the object of a property. */
    public static final String RDF_PROPERTY_RDF_RESOURCE = "rdf:resource";

    // DOMS RDF properties for working with content models

    /** The RDF property defining if this is a main object for a view. */
    public static final String RELATION_MAIN_VIEW
            = "http://doms.statsbiblioteket.dk/relations/default/0/1/#isMainForNamedView";

    /** The RDF property defining this is a subclass of another content model. */
    public static final String RELATION_EXTENDS_MODEL
            = "http://doms.statsbiblioteket.dk/relations/default/0/1/#extendsModel";
}
