package dk.statsbiblioteket.doms.central.connectors.builderpattern;


import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ImprovedFedora {



      /*------------SEARCHES----------------*/


    /**
     * Get the pids of objects of a specific type (content model) in a specific collection
     * @param collectionPid the collection. NotNull
     * @param contentModelPid the content model pid. If null, returns all types
     * @return the pids of the objects
     * @throws AuthException    if the authorization failed
     */
    List<String> getObjectsInCollection(String collectionPid, String contentModelPid) throws
                                                                                      AuthException;


    /**
     * Find the pids of objects with the given DC identifier in their DC datastream
     * @param identifier the identifier. NotNull
     * @return a list of pids of objects
     * @throws AuthException    if the authorization failed
     */
    List<String> findPidFromDCIdentifier(String identifier) throws
                                                        AuthException;

    /**
     * Find the pids of objects with the given object label
     * @param label the label. NotNull
     * @return a list of pids of objects
     * @throws AuthException    if the authorization failed
     */
    List<String> findPidFromObjectLabel(String label) throws AuthException;


    /*------------OBJECTS----------------*/

    /**
     * Create a new object by cloning a template
     * @param templatepid the pid of the template
     * @param oldIDs the old identifiers to add to the object. Can be null
     * @param logMessage the log message for the operation
     * @return the new object
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid did not exist, or does not refernece a template object
     */
    public SimpleObject cloneTemplate(String templatepid, List<String> oldIDs, String logMessage) throws
                                                                                                  AuthException,
                                                                                                  NotFoundException;

    /**
     * Create a new empty object
     * @param oldIDs the old identifiers to add to the object. Can be null
     * @param collections the collections for the new objects. Can be null
     * @param logMessage the log message for the operation
     * @return the new object
     * @throws AuthException    if the authorization failed
     */
    public SimpleObject newObject(List<String> oldIDs, List<String> collections, String logMessage) throws
                                                                                                    AuthException;

    /**
     * Get the full object, ie the simple object and the datastreams and internal and external relations.
     * @param pid the pid of the object
     * @param asOfTime the timestamp, or null for newest
     * @return the full object
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid did not exist
     */
    public ObjectStructure getObjectStructure(String pid, Long asOfTime) throws
                                                                     AuthException,
                                                                     NotFoundException;

    /**
     * Modify the object properties
     * @param object the modified object
     * @param lastModifiedDate if the object have been modified after this timestamp, fail the operation. This can be
     *                         used
     *                         to ensure concurrent access does not destroy the object
     * @param logMessage       the log message
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid did not exist
     */
    void modifyObject(SimpleObject object, Long lastModifiedDate, String logMessage) throws
                                                                                     AuthException,
                                                                                     NotFoundException;

    /**
     * Delete the object from DOMS
     * @param pid the pid of the pid of the objectt
     * @param lastModifiedDate if the object have been modified after this timestamp, fail the operation. This can be
     *                         used
     *                         to ensure concurrent access does not destroy the object
     * @param logMessage       the log message
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid did not exist
     */
    void deleteObject(String pid, Long lastModifiedDate, String logMessage) throws
                                                                            AuthException,
                                                                            NotFoundException;


    /*------------RELATIONS----------------*/


    /**
     * set the internal relations of an object. The internal relations are relations from a datastream in this object to whatever
     * @param pid the pid of the object
     * @param relations the internal relations to set
     * @param lastModifiedDate if the object have been modified after this timestamp, fail the operation. This can be
     *                         used
     *                         to ensure concurrent access does not destroy the object
     * @param logMessage       the log message
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid did not exist
     */
    void setRelsInt(String pid, Map<String, Set<Relation>> relations, Long lastModifiedDate, String logMessage) throws
                                                                                                                AuthException,
                                                                                                                NotFoundException;

    /**
     * Get the internal relations of an object. The internal relations are relations from a datastream in this object to whatever
     * @param pid the pid of the object
     * @param asOfTime the timestamp, or null for newest
     *
     * @return the internal relations as an immutable map, where the keys are datastream IDs
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid or RELS-INT datastream did not exist
     */
    Map<String, Set<Relation>> getRelsInt(String pid, Long asOfTime) throws
                                                                     AuthException,

                                                                     NotFoundException;


    /**
     * Set the external relations of an object. The external relations are relations from this object to whatever.
     *
     * @param pid              the pid of the object
     * @param relations        the relations to set
     * @param lastModifiedDate if the object have been modified after this timestamp, fail the operation. This can be
     *                         used
     *                         to ensure concurrent access does not destroy the object
     * @param logMessage       the log message
     *
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid  did not exist
     */
    void setRelsExt(String pid, Set<Relation> relations, Long lastModifiedDate, String logMessage) throws
                                                                                                   AuthException,
                                                                                                   NotFoundException;

    /**
     * Get the external relations of the object
     *
     * @param pid      the pid of the object
     * @param asOfTime the timestamp, or null for newest
     *
     * @return the relations as an immutable set
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid or RELS-EXT datastream did not exist
     */
    Set<Relation> getRelsExt(String pid, Long asOfTime) throws
                                                        AuthException,
                                                        NotFoundException;

    /**
     * Get the inverse relations, ie. the relations TO this object.
     *
     * @param pid       the pid of the object
     * @param predicate the predicate of the inverse relations. If null, get for all predicates
     *
     * @return a Map of pid,Set of Relation. Each key is an pid of an object
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid did not exist
     */
    Map<String, Set<Relation>> getInverseRelations(String pid, String predicate) throws
                                                                                 AuthException,
                                                                                 NotFoundException;


    /*------------DATASTREAMS----------------*/

    /**
     * @param pid          the pid of the object
     * @param datastreamID the id of the datastream in the object
     * @param asOfTime     the timestamp, or null for newest
     *
     * @return the datastream as an object
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid or datastream did not exist
     */
    Datastream getDatastream(String pid, String datastreamID, Long asOfTime) throws
                                                                             AuthException,
                                                                             NotFoundException;


    /**
     * modify or create a datastream in the object
     *
     * @param pid              the pid of the object
     * @param datastream       the datastream to add
     * @param lastModifiedDate if the object have been modified after this timestamp, fail the operation. This can be
     *                         used
     *                         to ensure concurrent access does not destroy the object
     * @param logMessage       the log message
     *
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid did not exist
     */
    void modifyDatastream(String pid, Datastream datastream, Long lastModifiedDate, String logMessage) throws
                                                                                                       AuthException,
                                                                                                       NotFoundException;

    /**
     * Delete the datastream
     *
     * @param pid              the pid of the object
     * @param datastreamID     the id of the datastream in the object
     * @param lastModifiedDate if the object have been modified after this timestamp, fail the operation. This can be
     *                         used
     *                         to ensure concurrent access does not destroy the object
     * @param logMessage       the log message
     *
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid or datastream did not exist
     */
    void deleteDatastream(String pid, String datastreamID, Long lastModifiedDate, String logMessage) throws
                                                                                                     AuthException,
                                                                                                     NotFoundException;

    /**
     * Get the datastream contents
     *
     * @param pid          the pid of the object
     * @param datastreamId the id of the datastream in the object
     * @param asOfTime     the timestamp, or null for newest
     *
     * @return the contents as an inputstream
     * @throws AuthException    if the authorization failed
     * @throws NotFoundException if the pid or datastream did not exist
     */
    InputStream getDatastreamContents(String pid, String datastreamId, Long asOfTime) throws
                                                                                      AuthException,
                                                                                      NotFoundException;
}
