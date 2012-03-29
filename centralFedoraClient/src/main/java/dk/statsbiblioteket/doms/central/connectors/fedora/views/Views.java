package dk.statsbiblioteket.doms.central.connectors.fedora.views;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/29/12
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Views {

    /**
     * Get the data objects which are marked (in their content models) as entries
     * for the given angle
     *
     * @param viewAngle       The given view angle
     * @return a lists of the data objects pids
     */
    public List<String> getEntryCMsForAngle(
            String viewAngle) throws BackendInvalidCredsException, BackendMethodFailedException;

    /**
     * Get all data objects subscribing to a given content model, and with the
     * given status
     *
     * @param cmpid           the content model
     * @param status          the given status, A, I or D
     * @return a list of data objects

     * @throws dk.statsbiblioteket.doms.central.connectors.fedora.templates.ObjectIsWrongTypeException    if cmpid is not a content model
     */
    public List<String> getObjectsForContentModel(
            String cmpid,
            String status) throws BackendInvalidCredsException, BackendMethodFailedException;

    /**
     * Get all entry data objects for the given angle, but only entry objects
     * with the given state
     *
     * @param viewAngle       the viewangle
     * @param state           the required state
     * @return a list of dataobjects
     */
    public List<String> getEntriesForAngle(String viewAngle,
                                      String state) throws BackendInvalidCredsException, BackendMethodFailedException,
                                                           BackendInvalidResourceException;
    /**
     * Get a list of the objects in the view of a given object
     *
     * @param objpid          the object whose view we examine
     * @param viewAngle       The view angle
     * @return the list of the pids in the view of objpid
     */
    public List<String> getViewObjectsListForObject(
            String objpid,
            String viewAngle) throws BackendInvalidCredsException, BackendMethodFailedException,
                                     BackendInvalidResourceException;

    /**
     * Get a bundle of the xml dump of the objects in the view of objpid. The
     * objects will be bundled under the supertag
     * dobundle:digitalObjectBundle, where dobundle is defined in Constants
     *
     * @param objpid          the object whose view we examine
     * @param viewAngle       The view angle
     * @return The objects bundled under the supertag
     * @see dk.statsbiblioteket.doms.central.connectors.fedora.utils.Constants#NAMESPACE_DIGITAL_OBJECT_BUNDLE
     */
    public Document getViewObjectBundleForObject(
            String objpid,
            String viewAngle) throws BackendInvalidCredsException, BackendMethodFailedException,
                                     BackendInvalidResourceException;


    public List<String> getEntryContentModelsForObjectForViewAngle(String pid,
                                                              String angle) throws BackendInvalidCredsException,
                                                                                   BackendMethodFailedException;
}
