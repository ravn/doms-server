package dk.statsbiblioteket.doms.central.connectors.fedora.views;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.inheritance.ContentModelInheritance;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.FedoraRelation;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStore;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.Constants;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.FedoraUtil;
import dk.statsbiblioteket.util.DocumentUtils;
import dk.statsbiblioteket.util.xml.DOM;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: abr Date: 3/29/12 Time: 3:14 PM To change this template use File | Settings | File
 * Templates.
 */
public class ViewsImpl implements Views {


    private static final Log LOG = LogFactory.getLog(ViewsImpl.class);

    TripleStore ts;
    private ContentModelInheritance inheritance;
    private Fedora fedora;


    public ViewsImpl(TripleStore ts,
                     ContentModelInheritance inheritance,
                     Fedora fedora) {
        this.ts = ts;
        this.inheritance = inheritance;
        this.fedora = fedora;
    }

    /**
     * Simple utility method for removing illegal characters in the viewAngle name
     *
     * @param viewAngle the viewAngle to sanitize
     *
     * @return the sanitized viewAngle
     */
    private String sanitizeLiteral(String viewAngle) {
        viewAngle = viewAngle.replaceAll("'", "");
        viewAngle = viewAngle.replaceAll("<", "");
        return viewAngle;
    }


    /**
     * Simple utility method for removing illegal characters in a pid.
     *
     * @param pid the pid to sanitize
     *
     * @return the sanitized pid
     */
    private String sanitizePid(String pid) {
        return pid;
    }


    /**
     * Get a list of the objects in the view of a given object
     *
     * @param objpid    the object whose view we examine
     * @param viewAngle The view angle
     * @param asOfTime
     *
     * @return the list of the pids in the view of objpid
     */
    public List<String> getViewObjectsListForObject(String objpid,
                                                    String viewAngle,
                                                    Long asOfTime)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException,
            BackendInvalidResourceException {

        LOG.trace("Entering getViewObjectsListForObject with params '" +
                  objpid + "' and '" + viewAngle + "'" + " and timestamp='" + asOfTime + "'");
/*

        if (!fedoraConnector.exists(objpid)){
            throw new ObjectNotFoundException("The data object '" + objpid +
                                              "' does not exist");
        }
        if (!fedoraConnector.isDataObject(objpid)){
            throw new ObjectNotFoundException("The data object '" + objpid +
                                              "' is not a data object");
        }
*/

        List<String> includedPids = new ArrayList<String>();

        appendPids(viewAngle, includedPids, objpid, asOfTime);

        return includedPids;
    }

    /**
     * Get a bundle of the xml dump of the objects in the view of objpid. The objects will be bundled under the supertag
     * dobundle:digitalObjectBundle, where dobundle is defined in Constants
     *
     * @param objpid    the object whose view we examine
     * @param viewAngle The view angle
     * @param asOfTime
     *
     * @return The objects bundled under the supertag
     * @see Constants#NAMESPACE_DIGITAL_OBJECT_BUNDLE
     */
    public Document getViewObjectBundleForObject(String objpid,
                                                 String viewAngle,
                                                 Long asOfTime)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException,
            BackendInvalidResourceException {


        List<String> pidlist = getViewObjectsListForObject(objpid, viewAngle, asOfTime);

        Document doc = DocumentUtils.DOCUMENT_BUILDER.newDocument();

        //There is no document element per default, so we make one
        doc.appendChild(doc.createElementNS(Constants.NAMESPACE_DIGITAL_OBJECT_BUNDLE, "dobundle:digitalObjectBundle"));

        //And we get the new document element back
        Element docelement = doc.getDocumentElement();

        for (String pid : pidlist) {
            //Get the object as a document
            Document objectdoc = DOM.stringToDOM(fedora.getObjectXml(pid, asOfTime), true);

            //add it to the bundle we are creating
            Element objectdocelement = objectdoc.getDocumentElement();
            Node importobjectdocelement = doc.importNode(objectdocelement, true);
            docelement.appendChild(importobjectdocelement);
        }

        //return the bundle
        return doc;
    }


    private void appendPids(String viewname,
                            List<String> includedPids,
                            String pid,
                            Long asOfTime)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException,
            BackendInvalidResourceException {

        LOG.trace("Entering appendPids with params " + viewname + " and " + pid + " and timestamp " + asOfTime);
        pid = sanitizePid(pid);
        viewname = sanitizeLiteral(viewname);

        // Check if PIDs is there
        // This is the reason why we need to thread the list through the
        // recursion. Without it we would end in cycles
        pid = FedoraUtil.ensurePID(pid);
        if (includedPids.contains(pid)) {
            return;
        }
        includedPids.add(pid);
        LOG.trace("Pid '" + pid + "' added to includedPids");

        // Find relations to follow
        // Get content model
        CompoundView cm = CompoundView.getView(pid, fedora, asOfTime);
        View view = cm.getView().get(viewname);
        if (view == null) {
            LOG.debug("View null, returning");
            return;
        }

        // Outgoing relations
        List<String> properties = view.getProperties();
        for (String property : properties) {
            // Find relations
            List<FedoraRelation> relations;

            relations = fedora.
                                      getNamedRelations(pid, property, asOfTime);


            // Recursively add
            for (FedoraRelation relation : relations) {
                String newpid = relation.getObject();
                appendPids(viewname, includedPids, newpid, asOfTime);

            }

        }

        // Incoming relations
        List<String> inverseProperties = view.getInverseProperties();
        for (String inverseProperty : inverseProperties) {
            String
                    query =
                    "select $object\n"
                    + "from <#ri>\n"
                    + "where $object <"
                    + inverseProperty
                    + "> <"
                    + FedoraUtil.ensureURI(pid)
                    + ">";
            // Find relations


            List<String> objects = ts.genericQuery(query);
            // Recursively add
            for (String newpid : objects) {
                appendPids(viewname, includedPids, newpid, asOfTime);
            }


        }
    }
}
