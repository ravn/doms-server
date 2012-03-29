package dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.FedoraRelation;

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



        /**
     * Runs any query, that produces one column of results, and return each line as a string
     * @param query The query to execute
     * @return an empty list
     */
    public List<String> genericQuery(String query)
            throws BackendInvalidCredsException, BackendMethodFailedException;
}
