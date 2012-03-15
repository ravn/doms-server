package dk.statsbiblioteket.doms.central.connectors.fedora;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/15/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TripleStore {


    List<String> getObjectsInCollection(String collectionPid, String contentModel) throws
                                                                                   BackendInvalidCredsException,
                                                                                   BackendMethodFailedException;


    List<String> listObjectsWithThisLabel(String label)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException;



    List<String> findObjectFromDCIdentifier(String string)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException;


    void flushTripples()
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException;


    List<FedoraRelation> getInverseRelations(String pid, String predicate) throws BackendMethodFailedException,
                                                                                  BackendInvalidCredsException,
                                                                                  BackendInvalidResourceException;

}
