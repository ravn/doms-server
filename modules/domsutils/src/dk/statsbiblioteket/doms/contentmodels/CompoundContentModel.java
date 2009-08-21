package dk.statsbiblioteket.doms.contentmodels;

import dk.statsbiblioteket.util.qa.QAInfo;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class describing a compund content model, that is the result of combining
 * information in all content models for an object.
 */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "kfc",
        reviewers = {"jrg"})
public class CompoundContentModel {
    /**
     * A list of content model pids used to generate the compound content
     * model, in the order they were resolved.
     */
    List<String> pids;

    /** A list of datastreams defined by the model. */
    List<Datastream> datastreams;

    /** The ontology defined by the model. */
    Document ontology;

    /** The view defined by the model. */
    Map<String, View> view;

    /**
     * Initialise a content model.
     *
     * @throws RuntimeException on trouble generating document.
     */
    public CompoundContentModel() {
        pids = new ArrayList<String>();
        datastreams = new ArrayList<Datastream>();
        try {
            ontology = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(
                    "Error initialising default document builder", e);
        }
        ontology.appendChild(
                ontology.createElementNS(
                        "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "rdf:RDF"));
        view = new HashMap<String, View>();
    }

    /**
     * Get the list of content model pids used to generate the compound content
     * model, in the order they were resolved.
     *
     * @return A list of pids.
     */
    public List<String> getPids() {
        return pids;
    }

    /**
     * Set the list of content model pids used to generate the compound content
     * model, in the order they were resolved.
     *
     * @param pids A list of pids.
     */
    public void setPids(List<String> pids) {
        this.pids = pids;
    }

    /**
     * Get the list of datastreams defined by the model.
     *
     * @return The list of datastreams defined by the model.
     */
    public List<Datastream> getDatastreams() {
        return datastreams;
    }

    /**
     * Set the list of datastreams defined by the model.
     *
     * @param datastreams The list of datastreams defined by the model.
     */
    public void setDatastreams(List<Datastream> datastreams) {
        this.datastreams = datastreams;
    }

    /**
     * Get the ontology defined by the model.
     *
     * @return The ontology defined by the model.
     */
    public Document getOntology() {
        return ontology;
    }

    /**
     * Set the ontology defined by the model.
     *
     * @param ontology The ontology defined by the model.
     */
    public void setOntology(Document ontology) {
        this.ontology = ontology;
    }

    /**
     * Get the view defined by the model.
     *
     * @return The view defined by the model.
     */
    public Map<String, View> getView() {
        return view;
    }

    /**
     * Set the view defined by the model.
     *
     * @param view The view defined by the model.
     */
    public void setView(Map<String, View> view) {
        this.view = view;
    }
}
