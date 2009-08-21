package dk.statsbiblioteket.doms.contentmodels;

import dk.statsbiblioteket.util.qa.QAInfo;
import org.w3c.dom.Document;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/** A class representing a datastream description from a content model. */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "kfc",
        reviewers = {"jrg"})
public class Datastream {
    /** Name of datastream. */
    private String name;

    /**
     * Mimetype of datastream, as declared in DS-COMPOSITE. May be null, if
     * not set.
     */
    private List<String> mimetypes = new ArrayList<String>();

    /**
     * Format URI of datastream as declared in DS-COMPOSITE. May be null, if
     * not set.
     */
    private List<URI> formatUris = new ArrayList<URI>();

    /**
     * XML Schema for datastream as declared in DS-COMPOSITE with DOMS
     * extension. May be null, for no set schema.
     */
    private Document xmlSchema = null;

    /**
     * How a GUI should represent this datastream, as declared in DS-COMPOSITE
     * with DOMS extension. Null means not set, and is treated as invisible.
     */
    private GUIRepresentation guiRepresentation = null;

    /**
     * Get name of datastream.
     *
     * @return Name of datastream.
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of datastream.
     *
     * @param name Name of datastream.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get mimetype of datastream, as declared in DS-COMPOSITE.
     *
     * @return Mimetype of datastream, as declared in DS-COMPOSITE.
     */
    public List<String> getMimetypes() {
        return mimetypes;
    }

    /**
     * Set mimetype of datastream, as declared in DS-COMPOSITE.
     *
     * @param mimetypes Mimetype of datastream, as declared in DS-COMPOSITE.
     */
    public void setMimetypes(List<String> mimetypes) {
        this.mimetypes = mimetypes;
    }

    /**
     * Get format URI of datastream as declared in DS-COMPOSITE.
     *
     * @return Format URI of datastream as declared in DS-COMPOSITE.
     */
    public List<URI> getFormatUris() {
        return formatUris;
    }

    /**
     * Set format URI of datastream as declared in DS-COMPOSITE.
     *
     * @param formatUris Format URI of datastream as declared in DS-COMPOSITE.
     */
    public void setFormatUris(List<URI> formatUris) {
        this.formatUris = formatUris;
    }

    /**
     * Get XML Schema for datastream as declared in DS-COMPOSITE with DOMS
     * extension.
     *
     * @return XML Schema for datastream as declared in DS-COMPOSITE with DOMS
     *         extension.
     */
    public Document getXmlSchema() {
        return xmlSchema;
    }

    /**
     * Set XML Schema for datastream as declared in DS-COMPOSITE with DOMS
     * extension.
     *
     * @param xmlSchema XML Schema for datastream as declared in DS-COMPOSITE
     *                  with DOMS extension.
     */
    public void setXmlSchema(Document xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    /**
     * Get how a GUI should represent this datastream, as declared in
     * DS-COMPOSITE with DOMS extension.
     *
     * @return How a GUI should represent this datastream, as declared in
     *         DS-COMPOSITE with DOMS extension.
     */
    public GUIRepresentation getGuiRepresentation() {
        return guiRepresentation;
    }

    /**
     * Set how a GUI should represent this datastream, as declared in
     * DS-COMPOSITE with DOMS extension.
     *
     * @param guiRepresentation How a GUI should represent this datastream, as
     *                          declared in DS-COMPOSITE with DOMS extension.
     */
    public void setGuiRepresentation(GUIRepresentation guiRepresentation) {
        this.guiRepresentation = guiRepresentation;
    }

    /** Possible values for how a GUI should present a datastream. */
    public static enum GUIRepresentation {
        /** This datastream may be imported. */
        IMPORTABLE,
        /** This datastream may be edited. */
        EDITABLE,
        /** This datastream is an externally referenced file. */
        UPLOADABLE,
        /** This datastream may be shown, but not imported or edited. */
        READONLY,
        /** This datastream should be ignored. This is the default. */
        INVISIBLE
    }
}
