package dk.statsbiblioteket.doms.namespace;

import dk.statsbiblioteket.util.qa.QAInfo;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "kfc",
        reviewers = {"jrg"})
/** Utilities for working in the DOMS namespace. */
public class NamespaceUtils {
    /**
     * Helper method for doing an XPath query using DOMS namespaces.
     * {@see NameSpaceConstants#DOMS_NAMESPACE_CONTEXT}.
     *
     * @param node            The node to start XPath query on.
     * @param xpathExpression The XPath expression, using default DOMS
     *                        namespace prefixes.
     * @return The result, as a node list.
     *
     * @throws XPathExpressionException On trouble parsing or evaluating the
     *                                  expression.
     */
    public static NodeList xpathQuery(Node node, String xpathExpression)
            throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(NamespaceConstants.DOMS_NAMESPACE_CONTEXT);

        return (NodeList) xPath
                .evaluate(xpathExpression, node, XPathConstants.NODESET);
    }
}
