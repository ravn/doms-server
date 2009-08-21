package dk.statsbiblioteket.doms.contentmodels;

import dk.statsbiblioteket.util.qa.QAInfo;

import java.util.ArrayList;
import java.util.List;

/** A representation of a Doms View. */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "kfc",
        reviewers = {"jrg"})
public class View {
    /** A list of names of RDF properties to follow to define the view. */
    private List<String> properties;

    /** A list of names of RDF properties into this view defining the view. */
    private List<String> inverseProperties;

    /** Whether this is a main object in the view. */
    private boolean main;

    /** Initialises the view, with two empty datastreams. */
    public View() {
        this.properties = new ArrayList<String>();
        this.inverseProperties = new ArrayList<String>();
    }

    /**
     * Get the list of names of properties to follow to define the view.
     *
     * @return A list of names of properties to follow to define the view.
     */
    public List<String> getProperties() {
        return properties;
    }

    /**
     * Set the list of names of properties to follow to define the view.
     *
     * @param properties A list of names of properties to follow to define the
     *                   view.
     */
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    /**
     * Get the list of names of properties into this view defining the view.
     *
     * @return A list of names of properties into this view defining the view.
     */
    public List<String> getInverseProperties() {
        return inverseProperties;
    }

    /**
     * Set the list of names of properties into this view defining the view.
     *
     * @param inverseProperties A list of names of properties into this view
     *                          defining the view.
     */
    public void setInverseProperties(List<String> inverseProperties) {
        this.inverseProperties = inverseProperties;
    }

    /**
     * Get whether this is the main object in this view.
     *
     * @return Whether this is the main object in this view.
     */
    public boolean isMain() {
        return main;
    }

    /**
     * Set whether this is the main object in this view.
     *
     * @param main Whether this is the main object in this view.
     */
    public void setMain(boolean main) {
        this.main = main;
    }
}
