package dk.statsbiblioteket.doms.view;

import dk.statsbiblioteket.doms.DomsUserToken;
import dk.statsbiblioteket.doms.contentmodels.CompoundContentModel;
import dk.statsbiblioteket.doms.contentmodels.ContentModelUtils;
import dk.statsbiblioteket.doms.contentmodels.View;
import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.fedora.FedoraUtils;
import dk.statsbiblioteket.doms.namespace.NamespaceConstants;
import dk.statsbiblioteket.util.qa.QAInfo;
import fedora.server.types.gen.RelationshipTuple;
import org.jrdf.graph.URIReference;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.IN_DEVELOPMENT,
        author = "kfc",
        reviewers = {""})
/** Utility methods for working with DOMS views. */
public class ViewUtils {

    /* TODO: Needs javadoc */
    public static List<String> getMainViewNames(String mainpid,
                                                DomsUserToken userToken) {
        List<String> mainviewnames = new ArrayList<String>();
        CompoundContentModel cm = ContentModelUtils
                .getCompoundContentModel(mainpid, userToken);
        Map<String, View> views = cm.getView();
        Set<String> viewNames = views.keySet();
        for (String viewname : viewNames) {
            View view = views.get(viewname);
            if (view.isMain()) {
                mainviewnames.add(viewname);
            }
        }
        return mainviewnames;
    }

    /* TODO: Needs javadoc */
    public static List<String> getViewObjects(String mainpid,
                                              DomsUserToken userToken)
            throws XPathExpressionException {
        // Recursively bundle documents
        List<String> pids = new ArrayList<String>();
        for (String mainviewname : getMainViewNames(mainpid, userToken)) {
            appendPids(mainviewname, pids, mainpid, userToken);
        }
        return pids;
    }

    /* TODO: Needs javadoc */
    public static List<String> getViewObjects(String mainpid, String viewname,
                                              DomsUserToken userToken)
            throws XPathExpressionException {
        List<String> pids = new ArrayList<String>();
        appendPids(viewname, pids, mainpid, userToken);
        return pids;
    }

    /**
     * Get a bundle of documents that are in the same view. The bundle is
     * trivially bundled under a common super-tag
     * <code>
     * &lt;dobundle:digitalObjectBundle xmlns:dobundle="http://doms.statsbiblioteket.dk/types/digitalobjectbundle/0/1/#"&gt;
     * </code>
     * and then all the FoxML objects are simply appended under this tag.
     * The objects are added in a depth-first traversal of the view.
     *
     * @param pid      The PID of the object to start in.
     * @param viewname The name of the View to follow.
     * @return A document containing the bundle.
     *
     * @throws FedoraConnectionException on trouble communicating with Fedora.
     * @throws dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException
     *                                   if contents from Fedora is not
     *                                   correct (i.e. illegal XML or inconsistent content model or view
     *                                   description, or view refers to non-existing objects).
     */
    public static Document getViewBundle(String pid, String viewname,
                                         DomsUserToken userToken) {
        // Recursively bundle documents
        List<String> pids = new ArrayList<String>();
        appendPids(viewname, pids, pid, userToken);

        // Init result document
        Document result = FedoraUtils.DOCUMENT_BUILDER.newDocument();
        Element root = result.createElementNS(
                NamespaceConstants.NAMESPACE_DIGITAL_OBJECT_BUNDLE,
                "dobundle:digitalObjectBundle");
        result.appendChild(root);

        // Add documents
        for (String p : pids) {
            Document doc = FedoraUtils.getObjectXml(p, userToken);
            result.getDocumentElement().appendChild(
                    result.importNode(doc.getDocumentElement(), true));
        }

        return result;
    }

    private static void appendPids(String viewname, List<String> includedPids,
                                   String pid, DomsUserToken userToken) {

        // Check if PIDs is there
        pid = FedoraUtils.ensurePID(pid);
        if (includedPids.contains(pid)) {
            return;
        }
        includedPids.add(pid);

        // Find relations to follow
        // Get content model
        CompoundContentModel cm = ContentModelUtils
                .getCompoundContentModel(pid);
        View view = cm.getView().get(viewname);
        if (view == null) {
            return;
        }

        // Outgoing relations
        List<String> properties = view.getProperties();
        for (String property : properties) {
            // Find relations
            RelationshipTuple[] tuples;
            try {
                tuples = FedoraUtils.getAPIM(userToken)
                        .getRelationships(pid, property);
            } catch (RemoteException e) {
                throw new FedoraConnectionException(
                        "Error getting relation '" + property + "' from '" + pid
                                + "' from Fedora", e);
            }

            // Recursively add
            if (tuples != null) {
                for (RelationshipTuple tuple : tuples) {
                    String newpid = tuple.getObject();
                    appendPids(
                            viewname, includedPids, newpid, userToken);
                }
            }
        }

        // Incoming relations
        List<String> inverseProperties = view.getProperties();
        for (String inverseProperty : inverseProperties) {
            // Find relations
            Map<String, String> query = new HashMap<String, String>();
            query.put("lang", "itql");
            query.put(
                    "query",
                    "select $subject\n" + "from <#ri>\n" + "where $subject <"
                            + inverseProperty + "> <"
                            + FedoraUtils.ensureURI(pid) + ">");
            TupleIterator queryResult;
            try {
                queryResult = FedoraUtils.getFedoraClient(userToken)
                        .getTuples(query);
            } catch (IOException e) {
                throw new FedoraConnectionException(
                        "Error getting relation '" + inverseProperty + "' to '"
                                + pid + "' from Fedora", e);
            }

            // Recursively add
            try {
                while (queryResult.hasNext()) {
                    Map tuple;
                    tuple = queryResult.next();
                    org.jrdf.graph.Node n = (org.jrdf.graph.Node) tuple
                            .get("subject");
                    if ((n instanceof URIReference)) {
                        String newpid = ((URIReference) n).getURI().toString();
                        appendPids(
                                viewname, includedPids, newpid, userToken);
                    }
                }
            } catch (TrippiException e) {
                throw new FedoraConnectionException(
                        "Error getting relation '" + inverseProperty + "' to '"
                                + pid + "' from Fedora", e);
            }
        }
    }
}
