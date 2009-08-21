package dk.statsbiblioteket.doms.fedora;

import dk.statsbiblioteket.doms.DomsUserToken;
import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;
import dk.statsbiblioteket.util.qa.QAInfo;
import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.MIMETypedStream;
import org.apache.axis.types.URI;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

/** Utility methods for working with fedora. */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_NEEDED,
        author = "kfc",
        reviewers = {""})
public class FedoraUtils {
    // FIXME Move these two to XML Utility class
    /** A default document builder, namespace aware. */
    public static final DocumentBuilder DOCUMENT_BUILDER;

    static {
        try {
            DocumentBuilderFactory documentBuilderFactory
                    = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DOCUMENT_BUILDER = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error("Error initialising default document builder", e);
        }
    }

    /** A default document transformer. */
    public static final Transformer DOCUMENT_TRANSFORMER;

    static {
        try {
            DOCUMENT_TRANSFORMER = TransformerFactory.newInstance()
                    .newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new Error(
                    "Error initialising default document transformer", e);
        }
    }

    /**
     * Gets a Fedora client. If this is the first connect, or if the client has
     * been reset, the client is initialised, and connection to Fedora
     * initialised. Otherwise, the existing client is reused.
     *
     * @return The fedora client instance.
     *
     * @throws FedoraConnectionException on trouble connectng to Fedora.
     */
    public static FedoraClient getFedoraClient(DomsUserToken userToken) {

        try {
            FedoraClient client = userToken.getCachedFedoraClient();
            if (client == null) {

                client = new FedoraClient(
                        userToken.getServerurl(), userToken.getUsername(),
                        userToken.getPassword());
                userToken.setFedoraClient(client);
            }
            return client;
        } catch (MalformedURLException e) {
            throw new FedoraConnectionException(
                    "Error connecting to Fedora", e);
        } catch (IOException e) {
            throw new FedoraConnectionException(
                    "Error connecting to Fedora", e);
        }

    }

    /**
     * Get the API-M interface to Fedora.
     *
     * @return The API-M interface to Fedora.
     *
     * @throws FedoraConnectionException on trouble connecting to Fedora.
     */
    public static FedoraAPIM getAPIM(DomsUserToken userToken)
            throws FedoraConnectionException {
        try {
            return getFedoraClient(userToken).getAPIM();
        } catch (ServiceException e) {
            throw new FedoraConnectionException(
                    "Error connecting to Fedora", e);
        } catch (IOException e) {
            throw new FedoraConnectionException(
                    "Error connecting to Fedora", e);
        }
    }

    /**
     * Get the API-A interface to Fedora.
     *
     * @return The API-A interface to Fedora.
     *
     * @throws FedoraConnectionException on trouble connecting to Fedora.
     */
    public static FedoraAPIA getAPIA(DomsUserToken userToken) {
        FedoraAPIA fedoraAPIA;
        try {
            fedoraAPIA = getFedoraClient(userToken).getAPIA();
        } catch (IOException e) {
            throw new FedoraConnectionException(
                    "Error connecting to Fedora", e);
        } catch (ServiceException e) {
            throw new FedoraConnectionException(
                    "Error connecting to Fedora", e);
        }
        return fedoraAPIA;
    }

    /**
     * If the given string starts with "info:fedora/", remove it.
     *
     * @param pid A pid, possibly as a URI
     * @return The pid, with the possible URI prefix removed.
     */
    public static String ensurePID(String pid) {
        if (pid.startsWith(FedoraClient.FEDORA_URI_PREFIX)) {
            pid = pid.substring(FedoraClient.FEDORA_URI_PREFIX.length());
        }
        return pid;
    }

    /**
     * If the given string does not start with "info:fedora/", remove it.
     *
     * @param uri An URI, possibly as a PID
     * @return The uri, with the possible URI prefix prepended.
     */
    public static String ensureURI(String uri) {
        if (!uri.startsWith(FedoraClient.FEDORA_URI_PREFIX)) {
            uri = FedoraClient.FEDORA_URI_PREFIX + uri;
        }
        return uri;
    }

    /**
     * Get object XML from Fedora, and return it as a DOM document
     *
     * @param pid       The PID of the document to retrieve. May be represented as
     *                  a PID, or as a Fedora URI.
     * @param userToken
     * @return The object parsed in a DOM.
     *
     * @throws dk.statsbiblioteket.doms.exceptions.FedoraConnectionException
     *          On trouble communicating with Fedora.
     * @throws dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException
     *          If the contents cannot be parsed
     *          as XML.
     */
    public static Document getObjectXml(String pid, DomsUserToken userToken) {
        pid = ensurePID(pid);
        byte[] objectXML;
        try {
            objectXML = getAPIM(userToken).getObjectXML(pid);
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Error getting XML for '" + pid + "' from Fedora", e);
        } catch (IOException e) {
            throw new FedoraConnectionException(
                    "Error getting XML for '" + pid + "' from Fedora", e);
        }
        try {
            return DOCUMENT_BUILDER.parse(new ByteArrayInputStream(objectXML));
        } catch (SAXException e) {
            throw new FedoraIllegalContentException(
                    "Error parsing XML for '" + pid + "' from Fedora", e);
        } catch (IOException e) {
            throw new Error(
                    "IOTrouble reading from byte array stream, "
                            + "this should never happen", e);
        }
    }

    /**
     * Retrieve a datastream from Fedora, and parse it as document.
     *
     * @param pid        The ID of the object to get the datastream from.
     * @param datastream The ID of the datastream.
     * @param userToken
     * @return The datastream parsed as a SOM document.
     *
     * @throws dk.statsbiblioteket.doms.exceptions.FedoraConnectionException
     *          On trouble communicating with Fedora,
     *          including if datastream or object does not exist.
     * @throws dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException
     *          If datastream cannot be parsed as
     *          a DOM.
     */
    public static Document getDatastreamAsDocument(String pid,
                                                   String datastream,
                                                   DomsUserToken userToken) {
        pid = ensurePID(pid);
        MIMETypedStream dsCompositeDatastream;
        byte[] buf;
        try {
            dsCompositeDatastream = getAPIA(userToken)
                    .getDatastreamDissemination(pid, datastream, null);
            buf = dsCompositeDatastream.getStream();
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Error getting datastream'" + datastream + "' from '" + pid
                            + "'", e);
        }

        Document dsCompositeXml;
        try {
            dsCompositeXml = DOCUMENT_BUILDER.parse(
                    new ByteArrayInputStream(buf));
        } catch (SAXException e) {
            throw new FedoraIllegalContentException(
                    "Error parsing datastream '" + datastream + "'  from '"
                            + pid + "'", e);
        } catch (IOException e) {
            throw new Error(
                    "IOTrouble reading from byte array stream, "
                            + "this should never happen", e);
        }
        return dsCompositeXml;
    }

    public static void ingestDocument(Document document,
                                      DomsUserToken userToken) {
        ByteArrayOutputStream byteArrayOutputStream
                = new ByteArrayOutputStream();
        try {
            DOCUMENT_TRANSFORMER.transform(
                    new DOMSource(document),
                    new StreamResult(byteArrayOutputStream));
        } catch (TransformerException e) {
            //TODO: Exception handling
            throw new RuntimeException(
                    "Trouble transforming with exception", e);
        }
        try {
            getAPIM(userToken).ingest(
                    byteArrayOutputStream.toByteArray(),
                    FedoraClient.FOXML1_1.uri, "Ingested by DomsUserToken");
        } catch (RemoteException e) {
            throw new FedoraConnectionException("", e);
        }
    }

    /**
     * Adds a datastream by URL, always using externally referenced.
     *
     * @param pid         The object to add to
     * @param name        The name of the datastream
     * @param fileurl     The URL of the contents
     * @param md5CheckSum The md5 checksum of the contents
     * @param formatUri   The format URI of the contents (mimetype will always
     *                    be application/octet-stream)
     * @param userToken   Login information
     */
    public static void addDatastreamByURI(URI pid, String name, URI fileurl,
                                          String md5CheckSum, URI formatUri,
                                          DomsUserToken userToken) {
        FedoraAPIM fedoraAPIM = getAPIM(userToken);
        try {
            fedoraAPIM.addDatastream(
                    ensurePID(pid.toString()), name, new String[]{}, name, true,
                    "application/octet-stream", formatUri.toString(),
                    fileurl.toString(), "E", "A", "MD5", md5CheckSum,
                    "Datastream added by DOMS Utilities");
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Unable to add datastream '" + name + "' from url '"
                            + fileurl + "' to object '" + pid + "'", e);
        }

    }

    /**
     * Adds a datastream by value, always using inline XML.
     *
     * @param pid       The object to add to
     * @param name      The name of the datastream
     * @param contents  The contents
     * @param userToken Login information
     */
    public static void addDatastreamByDocument(URI pid, String name,
                                               Document contents,
                                               DomsUserToken userToken) {
        FedoraAPIM fedoraAPIM = getAPIM(userToken);
        FedoraClient client = getFedoraClient(userToken);
        try {
            File tempFile = File.createTempFile("charac", null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DOCUMENT_TRANSFORMER.transform(
                    new DOMSource(contents), new StreamResult(out));
            new FileOutputStream(tempFile).write(out.toByteArray());
            String url = client.uploadFile(tempFile);
            fedoraAPIM.addDatastream(
                    ensurePID(pid.toString()), name, new String[]{}, name, true,
                    "text/xml", null, url, "X", "A", "DISABLED", null,
                    "Datastream added by DOMS Utilities");
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Unable to add datastream '" + name + "' to object '" + pid
                            + "'", e);
        } catch (IOException e) {
            throw new FedoraConnectionException(
                    "Unable to add datastream '" + name + "' to object '" + pid
                            + "'", e);
        } catch (TransformerException e) {
            throw new FedoraIllegalContentException(
                    "Unable to add datastream '" + name + "' to object '" + pid
                            + "'", e);
        }
    }
}
