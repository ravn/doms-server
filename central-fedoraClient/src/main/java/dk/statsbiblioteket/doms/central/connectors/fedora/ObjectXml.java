package dk.statsbiblioteket.doms.central.connectors.fedora;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.util.xml.XSLT;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ObjectXml {

    private String pid;
    private final String rawXml;
    private final FedoraRest fedora;
    private final Long asOfTime;

    public ObjectXml(String pid, String rawXml, FedoraRest fedora, Long asOfTime){
        this.pid = pid;

        this.rawXml = rawXml;
        this.fedora = fedora;
        this.asOfTime = asOfTime;
    }

    public String getCleaned() throws
                               TransformerException,
                               BackendInvalidResourceException,
                               BackendMethodFailedException,
                               BackendInvalidCredsException {
        String xml = modifyForDate(rawXml, asOfTime);
        xml = stripHiddenDatastreams(xml);
        xml = stripOldVersions(xml);
        xml = stubManagedDatastreams(xml);

        List<String> datastreamIDs = getManagedXmlDatastreams(xml);

        StringBuilder temp = new StringBuilder(xml);

        for (String datastreamID : datastreamIDs) {
            temp = inlineDatastream(temp, datastreamID, pid, asOfTime);
        }


        return temp.toString();


    }

    private String stubManagedDatastreams(String xml) throws TransformerException {
        Transformer transformer = XSLT.getLocalTransformer(
                Thread.currentThread().getContextClassLoader().getResource("xslt/placeHoldersForManagedDatastreams.xslt"));
        StringWriter result = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(result));
        return result.toString();
    }


    protected String stripHiddenDatastreams(String xml) throws TransformerException {
        Transformer transformer = XSLT.getLocalTransformer(
                Thread.currentThread().getContextClassLoader().getResource("xslt/stripHiddenDatastreams.xslt"));
        StringWriter result = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(result));
        return result.toString();
    }


    protected StringBuilder inlineDatastream(StringBuilder xml, String dsid, String pid, Long asOfTime) throws
                                                                                                        BackendInvalidResourceException,
                                                                                                        BackendInvalidCredsException,
                                                                                                        BackendMethodFailedException {


        String realContent = fedora.getXMLDatastreamContents(pid, dsid, asOfTime);
        String cleanedContent = realContent.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();

        String str = "$" + dsid + "$";
        int placeholderIndex = xml.indexOf(str);
        return xml.replace(placeholderIndex, placeholderIndex + str.length(), cleanedContent);
    }

    protected List<String> getManagedXmlDatastreams(String xml) throws TransformerException {
        Transformer transformer = XSLT.getLocalTransformer(
                Thread.currentThread().getContextClassLoader().getResource("xslt/getManagedDatastreams.xslt"));
        StringWriter result = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(result));
        String[] lines = result.toString().split("\n");
        ArrayList<String> realResult = new ArrayList<String>();
        for (String line : lines) {
            if (!line.isEmpty()) {
                realResult.add(line);
            }
        }
        return realResult;
    }

    protected String stripOldVersions(String xml) throws TransformerException {
        Transformer transformer = XSLT.getLocalTransformer(
                Thread.currentThread().getContextClassLoader().getResource("xslt/stripOldVersions.xslt"));
        StringWriter result = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(result));
        return result.toString();
    }

    protected String modifyForDate(String xml, Long asOfTime) {
        //TODO filter out all versions newer than asOfTime
        return xml;
    }


}
