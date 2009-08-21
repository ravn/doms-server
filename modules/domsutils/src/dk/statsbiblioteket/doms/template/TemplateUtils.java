package dk.statsbiblioteket.doms.template;

import dk.statsbiblioteket.doms.DomsUserToken;
import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;
import dk.statsbiblioteket.doms.fedora.FedoraUtils;
import dk.statsbiblioteket.doms.gen.pidgenerator.DomsPIDGeneratorServiceLocator;
import dk.statsbiblioteket.doms.namespace.NamespaceUtils;
import dk.statsbiblioteket.util.qa.QAInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;

@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "kfc",
        reviewers = {"jrg"})
/** Utility methods for working with templates. */
public class TemplateUtils {

    /**
     * Given a pid of a template, return a clone of that template object.
     *
     * @param pid The template object to clone.
     * @return The pid of a clone of that template object.
     *
     * @throws PIDGeneratorException     on trouble getting PID from PID
     *                                   generator
     * @throws FedoraConnectionException on trouble getting template from
     *                                   Fedora
     * @throws dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException
     *                                   on finding illegal Fedora template.
     */
    public static String cloneTemplate(String pid, DomsUserToken userToken) {
        pid = FedoraUtils.ensurePID(pid);

        // Get the document
        Document document = FedoraUtils.getObjectXml(pid, userToken);

        // Get new pid
        String infix = pid.substring(pid.indexOf(":") + 1);
        infix = infix.substring(0, infix.indexOf("_"));
        String newPid;
        try {
            newPid = new DomsPIDGeneratorServiceLocator().getDomsPIDGenerator()
                    .generateNextAvailablePID(infix);
        } catch (Exception e) {
            throw new PIDGeneratorException("Cannot get PID for new object", e);
        }

        // Replace PID
        replacePid(document, pid, newPid);

        // Remove template relation
        NodeList relationNodes;
        try {
            relationNodes = NamespaceUtils.xpathQuery(
                    document,
                    "/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/"
                            + "foxml:datastreamVersion[position()=last()]/"
                            + "foxml:xmlContent/rdf:RDF/"
                            + "rdf:Description/doms:isTemplateFor");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);

        }
        if (relationNodes.getLength() == 1
                && relationNodes.item(0).getNodeType()
                == Document.ELEMENT_NODE) {
            Node node = relationNodes.item(0);
            node.getParentNode().removeChild(node);
        } else {
            throw new FedoraIllegalContentException(
                    "The given PID is not the PID of a template");
        }
        //TODO: Remove relations in view

        FedoraUtils.ingestDocument(document, userToken);

        return newPid;
    }

    /**
     * Given a FoxML document, replace the old PID with a new. Replaces PID in
     * header, RELS-EXT, and oai:itemID
     *
     * @param document The document to replace PID in
     * @param oldPid   The old PID
     * @param newPid   The new PID
     */
    private static void replacePid(Document document, String oldPid,
                                   String newPid) {
        // 1. Main PID
        NodeList pidNodes;
        try {
            pidNodes = NamespaceUtils.xpathQuery(
                    document, "/foxml:digitalObject/@PID");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);

        }
        if (pidNodes.getLength() != 1
                || pidNodes.item(0).getNodeType() != Document.ATTRIBUTE_NODE) {
            throw new FedoraIllegalContentException(
                    "No PID attribute in document for PID " + oldPid + "'");
        }
        pidNodes.item(0).setNodeValue(FedoraUtils.ensurePID(newPid));

        // 2. RDF class
        NodeList rdfNodes;
        try {
            rdfNodes = NamespaceUtils.xpathQuery(
                    document,
                    "/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/"
                            + "foxml:datastreamVersion[position()=last()]/"
                            + "foxml:xmlContent/rdf:RDF/"
                            + "rdf:Description/@rdf:about");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }
        if (rdfNodes.getLength() == 1
                && rdfNodes.item(0).getNodeType() == Document.ATTRIBUTE_NODE) {
            rdfNodes.item(0).setNodeValue(FedoraUtils.ensureURI(newPid));
        }

        // 3. oai:itemID
        NodeList itemIdNodes;
        try {
            itemIdNodes = NamespaceUtils.xpathQuery(
                    document,
                    "/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/"
                            + "foxml:datastreamVersion[position()=last()]/"
                            + "foxml:xmlContent/rdf:RDF/"
                            + "rdf:Description/oai:itemID");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }
        if (itemIdNodes.getLength() == 1
                && itemIdNodes.item(0).getNodeType() == Document.ELEMENT_NODE) {
            itemIdNodes.item(0).setNodeValue(FedoraUtils.ensurePID(newPid));
        }

        // 4. dc:identifier
        NodeList dcIdNodes;
        try {
            dcIdNodes = NamespaceUtils.xpathQuery(
                    document, "/foxml:digitalObject/foxml:datastream[@ID='DC']/"
                            + "foxml:datastreamVersion[position()=last()]/"
                            + "dc:identifier[text()='" + newPid + "']");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }
        if (dcIdNodes.getLength() == 1
                && dcIdNodes.item(0).getNodeType() == Document.ELEMENT_NODE) {
            dcIdNodes.item(0).setNodeValue(FedoraUtils.ensurePID(newPid));
        }

        // 5. dc:title
        NodeList dcIdTitle;
        try {
            dcIdTitle = NamespaceUtils.xpathQuery(
                    document, "/foxml:digitalObject/foxml:datastream[@ID='DC']/"
                            + "foxml:datastreamVersion[position()=last()]/"
                            + "dc:title");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }
        if (dcIdTitle.getLength() == 1
                && dcIdTitle.item(0).getNodeType() == Document.ELEMENT_NODE) {
            dcIdTitle.item(0).setNodeValue(FedoraUtils.ensurePID(newPid));
        }
    }
}
