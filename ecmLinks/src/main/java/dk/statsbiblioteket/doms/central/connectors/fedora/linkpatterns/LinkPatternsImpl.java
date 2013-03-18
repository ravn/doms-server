package dk.statsbiblioteket.doms.central.connectors.fedora.linkpatterns;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectProfile;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 3/15/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkPatternsImpl implements LinkPatterns {


    Fedora fedora;
    private String fedoraLocation;
    private Unmarshaller jaxb;
    XPathSelector xpath = DOM.createXPathSelector("lp", "http://doms.statsbiblioteket.dk/types/linkpattern/0/1/#");

    public LinkPatternsImpl(Fedora fedora, String fedoraLocation) throws JAXBException {
        this.fedora = fedora;
        this.fedoraLocation = fedoraLocation;
        jaxb = JAXBContext.newInstance("dk.statsbiblioteket.doms.central.connectors.fedora.linkpatterns.generated").createUnmarshaller();
    }

    @Override
    public List<LinkPattern> getLinkPatterns(String pid, Long asOfDate) throws BackendInvalidResourceException, BackendInvalidCredsException, BackendMethodFailedException {

        List<LinkPattern> linkPatterns = new ArrayList<LinkPattern>();

        ObjectProfile profile = fedora.getObjectProfile(pid, asOfDate);
        List<String> contentmodels = profile.getContentModels();
        for (String contentmodel : contentmodels) {
            try {
                String linkPatternStream = fedora.getXMLDatastreamContents(contentmodel, "LINK_PATTERN", asOfDate);
                Document doc = DOM.stringToDOM(linkPatternStream,true);
                NodeList linkPatternNodes = xpath.selectNodeList(doc, "/lp:linkPatterns/lp:linkPattern");
                for (int i = 0; i < linkPatternNodes.getLength(); i++) {
                    Node linkPatternNode = linkPatternNodes.item(i);
                    String name = xpath.selectString(linkPatternNode, "lp:name");
                    String alt_text = xpath.selectString(linkPatternNode, "lp:description");
                    String value = xpath.selectString(linkPatternNode, "lp:value");

                    value = replaceStandardValues(value,profile);
                    NodeList replacements = xpath.selectNodeList(linkPatternNode, "lp:replacements/lp:replacement");

                    for (int j = 0; j < replacements.getLength(); j++) {
                        Node replacement = replacements.item(j);
                        value = linkReplace(value,replacement, pid,asOfDate);
                    }

                    LinkPattern linkPattern = new LinkPattern(name, alt_text, value);
                    linkPatterns.add(linkPattern);
                }
            } catch (BackendInvalidResourceException e) {
                continue;
            }
        }
        return linkPatterns;


    }

    private String replaceStandardValues(String value, ObjectProfile profile) {
        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        value = value.replaceAll(Pattern.quote("{objectId}"),encode(profile.getPid().replaceAll("^.*:","")));
        value = value.replaceAll(Pattern.quote("{domsPid}"),encode(profile.getPid()));
        value = value.replaceAll(Pattern.quote("{domsUser}"),encode(fedora.getUsername()));
        value = value.replaceAll(Pattern.quote("{domsPassword}"),encode(fedora.getPassword()));
        value = value.replaceAll(Pattern.quote("{domsLocation}"),encode(fedoraLocation));
        value = value.replaceAll(Pattern.quote("{domsLocationRaw}"),fedoraLocation);

        value = value.replaceAll(Pattern.quote("{label}"),encode(profile.getLabel()));
        value = value.replaceAll(Pattern.quote("{owner}"),encode(profile.getOwnerID()));
        value = value.replaceAll(Pattern.quote("{state}"),encode(profile.getState()));
        value = value.replaceAll(Pattern.quote("{createdISO}"),encode(isoFormat.format(profile.getObjectCreatedDate())));
        value = value.replaceAll(Pattern.quote("{lastModifiedISO}"),encode(isoFormat.format(profile.getObjectLastModifiedDate())));
        value = value.replaceAll(Pattern.quote("{createdUnixMillis}"),encode(""+profile.getObjectCreatedDate().getTime()));
        value = value.replaceAll(Pattern.quote("{lastModifiedUnixMillis}"),encode(""+profile.getObjectLastModifiedDate().getTime()));

        return value;
    }

    private String linkReplace(String link, Node replacement, String pid, Long asOfDateTime) throws BackendInvalidResourceException, BackendInvalidCredsException, BackendMethodFailedException {
        String key = null;
        String datastream = null;
        String xpathValue = null;

        key = xpath.selectString(replacement,"lp:key");
        datastream = xpath.selectString(replacement,"lp:datastream");
        xpathValue = xpath.selectString(replacement,"lp:xpath");

        if (key == null || datastream == null || xpathValue == null) {
            //TODO log
            return link;
        }
        key = "{" + key + "}";

        if (!link.contains(key)) {
            return link;
        }

        String datastreamContents = fedora.getXMLDatastreamContents(pid, datastream, asOfDateTime);
        Document doc = DOM.stringToDOM(datastreamContents);
        XPathSelector xpathSelector = DOM.createXPathSelector();
        String value = xpathSelector.selectString(doc, xpathValue);
        link = link.replaceAll(Pattern.quote(key), encode(value));
        return link;
    }

    private String encode(String value){
        try {
            return URLEncoder.encode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error("UTF-8 not known");
        }
    }


}
