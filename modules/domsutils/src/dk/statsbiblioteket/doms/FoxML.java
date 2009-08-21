package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.util.qa.QAInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/** Utility methods for working with FoxML documents. */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "unknown",
        reviewers = {"abr"})
public class FoxML {
    /** Utility class, do not instantiate. */
    private FoxML() {
    }

    /**
     * Extracts the PID from a String containing foxml1.0 or foxml1.1. The String will be
     * parsed as xml, and a exception thrown, if it cannot be verified as valid
     * xml.
     *
     * @param foxml a foxml String.
     * @return the PID contained in the foxml.
     *
     * @throws IllegalArgumentException if the foxml did not contain a PID
     * @throws org.xml.sax.SAXException if the string could not be parsed as xml
     */
    public static String extractPid(String foxml) throws SAXException {
        InputSource in = new InputSource();
        in.setCharacterStream(new StringReader(foxml));
        Document dom;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder();
            // the code prints errors to standard err, if there is no error
            // handler, even if the handler does not catch errors
            builder.setErrorHandler(
                    new ErrorHandler() {

                        public void warning(SAXParseException exception)
                                throws SAXException {
                            throw new SAXException(
                                    exception.toString(), exception);
                        }

                        public void error(SAXParseException exception)
                                throws SAXException {
                            throw new SAXException(
                                    exception.toString(), exception);
                        }

                        public void fatalError(SAXParseException exception)
                                throws SAXException {
                            throw new SAXException(
                                    exception.toString(), exception);
                        }
                    });
            dom = builder.parse(in);
        } catch (ParserConfigurationException e) {//Not gonna happen
            throw new Error("Could not make a DocumentBuilder object!", e);

        } catch (IOException e) {
            throw new Error("Could not read the string!", e);
        }

        Element docElement = dom.getDocumentElement();
        //the top element should be the 'foxml:digitalObject' element
        String pid = docElement.getAttribute("PID");
        if (pid != null && pid.equals("")) {
            //System.out.println("Could not get PID from xml file: " + xmlFile);
            throw new IllegalArgumentException(
                    "The foxml did not contain a "
                            + "PID and thus was invalid");
        }
        return pid;
    }

    public static String documentToString(Document doc)
            throws TransformerException {

        StringWriter writer = new StringWriter();

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new Error("This should not happen", e);
        }

        t.transform(new DOMSource(doc), new StreamResult(writer));

        String result = writer.toString();
        return result;
    }

}
