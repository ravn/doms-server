package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.util.InvalidPropertiesException;
import dk.statsbiblioteket.util.XProperties;
import dk.statsbiblioteket.util.qa.QAInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Abstract class used for loading of property files. Classes should extend this, to have a uniform way to access the
 * property files
 */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_NEEDED,
        author = "unknown",
        reviewers = {"abr"})
public abstract class PropAccess {

    private static Log log = LogFactory.getLog(PropAccess.class);

    /**
     * The Xproperties object. Can be accessed without the accessor, due to
     * static loading requirements
     */
    protected static XProperties xprop = new XProperties();

    /**
     * Parse the filename as properties and load it in
     *
     * @param filename the file to load
     */
    protected static void load(String filename) {
        try {
            xprop.load(filename, false, false);
        } catch (InvalidPropertiesException e) {
            log.warn(
                    "Unable to parse the properties file, using default"
                            + " properties", e);
        } catch (IOException e) {
            log.warn(
                    "ClassLoader could not read " + filename
                            + ", using default properties");
        }


    }

    protected PropAccess() {
    }
}
