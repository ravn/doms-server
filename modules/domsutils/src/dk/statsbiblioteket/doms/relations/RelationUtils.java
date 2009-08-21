package dk.statsbiblioteket.doms.relations;

import dk.statsbiblioteket.doms.DomsUserToken;
import dk.statsbiblioteket.doms.exceptions.ObjectNotFoundException;
import dk.statsbiblioteket.doms.fedora.FedoraUtils;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jrdf.graph.Literal;
import org.jrdf.graph.URIReference;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RelationUtils {

    private static Log log = LogFactory.getLog(RelationUtils.class);

    public static boolean isMainObject(String pid, DomsUserToken userToken)
            throws Exception {
        return false;
        //TODO: implement this
    }

    public static DigitalObject[] getAllCollections(DomsUserToken userToken)
            throws Exception {

        try {
            final Set<DigitalObject> objects = new HashSet<DigitalObject>();

            String query = "select $object $title\n" + "from <#ri>\n"
                    + "where $object <info:fedora/fedora-system:def/model#hasModel> <info:fedora/doms:ContentModel_Collection>\n"
                    + "minus($object <info:fedora/fedora-system:def/model#state> <info:fedora/fedora-system:def/model#Deleted>)\n"
                    + "and $object <http://purl.org/dc/elements/1.1/title> $title";
            objects.addAll(query(query, userToken));

            log.trace(
                    "Exiting DomsResourceIndex.getAllCollections"
                            + " return referencingObjects = " + objects
                            .toString());

            return objects.toArray(
                    new DigitalObject[objects.size()]);
        } catch (Exception e) {
            log.error(
                    "ResourceIndex.allMainObjects(): Caught " + "exception: ",
                    e);
            throw new Exception("Query failed due to internal error.");
        }


    }

    private static DigitalObject[] getAllMainObjects_private(String viewName,
                                                             DomsUserToken userToken)
            throws Exception {

        try {
            final Set<DigitalObject> objects = new HashSet<DigitalObject>();

            String query = "select $object $title \n" + "from <#ri> \n"
                    + "where\n"
                    + "$object <info:fedora/fedora-system:def/model#hasModel> $cm \n"
                    + "minus $object <info:fedora/fedora-system:def/model#state> <info:fedora/fedora-system:def/model#Deleted>\n"
                    + "and $cm <http://doms.statsbiblioteket.dk/relations/default/0/1/#isMainForNamedView>"
                    + viewName + " \n"
                    + "and $object <http://purl.org/dc/elements/1.1/title> $title\n";

            objects.addAll(query(query, userToken));

            log.trace(
                    "Exiting DomsResourceIndex.allReferencingTheseObjects;"
                            + " return referencingObjects = " + objects
                            .toString());

            return objects.toArray(
                    new DigitalObject[objects.size()]);
        } catch (Exception e) {
            log.error(
                    "ResourceIndex.allMainObjects(): Caught " + "exception: ",
                    e);
            throw new Exception("Query failed due to internal error.");
        }


    }

    public static DigitalObject[] getAllMainObjects(String viewName,
                                                    DomsUserToken userToken)
            throws Exception {
        return getAllMainObjects_private("'" + viewName + "'", userToken);
    }

    public static DigitalObject[] allMainObjects(DomsUserToken client)
            throws Exception {
        return getAllMainObjects_private("$viewname", client);
    }

    public static DigitalObject[] allReferencingTheseObjects(String[] objects,
                                                             DomsUserToken domsclient)
            throws Exception {

        log.trace(
                "Entering DomsResourceIndex.allReferencingTheseObjects"
                        + " with\nobjects = " + Arrays.toString(objects));

        try {
            final Set<DigitalObject> referencingObjects
                    = new HashSet<DigitalObject>();

            for (String object : objects) {
                object = FedoraUtils.ensureURI(object);
                String query = "select $object $title\n" + "from <#ri>\n"
                        + "where $object $relates <" + object + ">\n"
                        + " minus($object <info:fedora/fedora-system:def/model#state>"
                        + " <info:fedora/fedora-system:def/model#Deleted>)\n and "
                        + "$object <http://purl.org/dc/elements/1.1/title> $title";

                referencingObjects.addAll(query(query, domsclient));
            }

            log.trace(
                    "Exiting DomsResourceIndex.allReferencingTheseObjects;"
                            + " return referencingObjects = "
                            + referencingObjects.toString());

            return referencingObjects.toArray(
                    new DigitalObject[referencingObjects.size()]);
        } catch (Exception e) {
            log.error(
                    "ResourceIndex.allReferencingTheseObjects(): Caught "
                            + "exception: ", e);
            throw new Exception("Query failed due to internal error.");
        }
    }

    public static DigitalObject[] allTemplatesInCollectionForContentModel(
            String collection, String contentModel, DomsUserToken domsclient)
            throws Exception {

        log.trace(
                "Entering DomsResourceIndex."
                        + "allTemplatesInCollectionForContentModel with\n"
                        + "collection = " + collection + "\ncontentModel = "
                        + contentModel);
        collection = FedoraUtils.ensureURI(collection);
        contentModel = FedoraUtils.ensureURI(contentModel);

        String query = "select $object $title\n" + "from <#ri>\n" + "where\n"
                + "  (\n" + "    trans(\n"
                + "      $subtype <http://doms.statsbiblioteket.dk/relations/default/0/1/#extendsModel> <"
                + contentModel + "> and\n"
                + "      $subtype  <http://doms.statsbiblioteket.dk/relations/default/0/1/#extendsModel> $temp\n"
                + "    )\n"
                + "    or $subtype <http://doms.statsbiblioteket.dk/relations/default/0/1/#extendsModel> <"
                + contentModel + ">\n" + "    or $subtype <mulgara:is> <"
                + contentModel + ">\n" + "  )\n"
                + "  and $object <http://doms.statsbiblioteket.dk/relations/default/0/1/#isTemplateFor> $subtype\n "
                + "  and $object <http://doms.statsbiblioteket.dk/relations/default/0/1/#isPartOfCollection> <"
                + collection + ">\n"
                + "  minus($object <info:fedora/fedora-system:def/model#state> <info:fedora/fedora-system:def/model#Deleted>)\n"
                + "  and $object <http://purl.org/dc/elements/1.1/title> $title \n";

        try {

            Set<DigitalObject> templatesFound = query(query, domsclient);

            log.trace(
                    "Exiting DomsResourceIndex."
                            + "allTemplatesInCollectionForContentModel; returning "
                            + "templatesFound = " + templatesFound.toString());

            return templatesFound.toArray(
                    new DigitalObject[templatesFound.size()]);
        } catch (Exception e) {
            log.error(
                    "ResourceIndex.allTemplatesInCollectionForContentModel():"
                            + " Caught exception: ", e);
            throw new Exception("Query failed due to internal error.");
        }
    }

    /**
     * Syntactic sugar method for getting the state of an Fedora object
     *
     * @param pid The pid of the object to examine
     * @return The state as a string. Could be "A", "I" or "D".
     *
     * @throws Exception if the state could not be retrived for some reason.
     */
    public static String getState(String pid, DomsUserToken domsclient)
            throws Exception {

        FedoraUtils.ensurePID(pid);

        try {

            ObjectFields objectFields = getObjectFields(
                    pid, new String[]{"state", "pid"}, domsclient);
            final String state = objectFields.getState();
            return state;
        } catch (ObjectNotFoundException onfe) {
            log.warn(
                    "DomsResourceIndex.getState(): Object not found. pid = "
                            + pid + "  exception: " + onfe);
            throw new Exception("Could not find an object with pid = " + pid);
        } catch (ServiceException se) {
            log.warn("DomsResourceIndex.getState(): Caught exception: " + se);
            throw new Exception("Query failed due to internal error.");
        } catch (IOException ioe) {
            log.warn("DomsResourceIndex.getState(): Caught exception: " + ioe);
            throw new Exception("Query failed due to internal error.");
        }
    }

    /**
     * Send given query to the Fedora Client, the objects returned as a result
     * of the query will be converted to <code>DigitalObject</code> instances
     * and returned to the caller.
     *
     * @param query  Query string to send to Fedora.
     * @param client Fedora client to send the query to.
     * @return A set of <code>DigitalObject</code> instances created on the
     *         result of the query.
     *
     * @throws org.apache.axis.types.URI.MalformedURIException
     *                         if the conversion from Fedora objects to
     *                         <code>DigitalObject</code> failed.
     * @throws IOException     if Fedora failed handling the query.
     * @throws TrippiException if iteration of the query result failed.
     */
    public static Set<DigitalObject> query(String query, DomsUserToken client)
            throws URI.MalformedURIException, IOException, TrippiException {

        log.trace("Entering DomsResourceIndex.query with query = \n" + query);

        final Set<DigitalObject> objectsFound = new HashSet<DigitalObject>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("lang", "itql");
        map.put("query", query);

        final TupleIterator tupleIterator = FedoraUtils.getFedoraClient(client)
                .getTuples(map);

        while (tupleIterator.hasNext()) {
            final Map tuple = tupleIterator.next();
            final Literal title = (Literal) tuple.get("title");
            final URIReference subject = (URIReference) tuple.get("object");
            objectsFound.add(
                    new DigitalObject(
                            title.toString(), new org.apache.axis.types.URI(
                                    subject.getURI().toString())));
        }
        log.trace(
                "Exiting DomsResourceIndex.query with objectsFound = \n"
                        + objectsFound.toString());

        return objectsFound;
    }

    /**
     * Get the indicated fields of the indicated object from the repository.
     *
     * @param pid       The pid of the object to examine
     * @param fields    The list of fields to get. Strings from these lists: <ul>
     *                  <li>Key fields:<i>"pid", "label", "state", "ownerId",
     *                  "cDate", "mDate", "dcmDate"</i> </li> <li>Dublin core
     *                  fields: <i>title, creator, subject, description, publisher,
     *                  contributor, date, format, identifier, source, language,
     *                  relation, coverage, rights</i></li> </ul> fields must
     *                  ALWAYS contain "pid".
     * @param userToken The client for the fedora server
     * @return An object having the selected fields intialized.
     *
     * @throws IOException                    If something went wrong in talking
     *                                        to the server
     * @throws javax.xml.rpc.ServiceException If something went wrong in talking
     *                                        to the server
     * @throws dk.statsbiblioteket.doms.exceptions.ObjectNotFoundException
     *                                        If the pid was not found in the
     *                                        repository
     */
    private static ObjectFields getObjectFields(String pid, String[] fields,
                                                DomsUserToken userToken)
            throws ServiceException, ObjectNotFoundException, IOException {

        pid = FedoraUtils.ensurePID(pid);

        final FieldSearchQuery query = new FieldSearchQuery();
        final Condition[] conditions = new Condition[1];
        conditions[0] = new Condition();
        conditions[0].setProperty("pid");
        conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
        conditions[0].setValue(pid);
        query.setConditions(conditions);
        final FieldSearchResult result = FedoraUtils.getAPIA(userToken)
                .findObjects(
                        fields, new NonNegativeInteger("1"), query);

        final ObjectFields[] resultList = result.getResultList();
        if (resultList == null || resultList.length == 0) {
            throw new ObjectNotFoundException(
                    "Object not found in " + "repository.");
        }
        return resultList[0];
    }

    public static DigitalObject[] allObjectsForContentModel(String contentModel,
                                                            DomsUserToken userToken)
            throws Exception {

        log.trace(
                "Entering DomsResourceIndex."
                        + "allObjectsForContentModel with\n" + "pid = '"
                        + contentModel + "'");
        contentModel = FedoraUtils.ensureURI(contentModel);

        String query = "select $object $title\n" + "from <#ri>\n" + "where\n"
                + "  (\n" + "    trans(\n"
                + "      $subtype <http://doms.statsbiblioteket.dk/relations/default/0/1/#extendsModel> <"
                + contentModel + "> and\n"
                + "      $subtype  <http://doms.statsbiblioteket.dk/relations/default/0/1/#extendsModel> $temp\n"
                + "    )\n"
                + "    or $subtype <http://doms.statsbiblioteket.dk/relations/default/0/1/#extendsModel> <"
                + contentModel + ">\n" + "    or $subtype <mulgara:is> <"
                + contentModel + ">\n" + "  )\n"
                + "  and $object <info:fedora/fedora-system:def/model#hasModel> $subtype\n "
                + "  minus($object <http://doms.statsbiblioteket.dk/relations/default/0/1/#isTemplateFor> $any)\n "
                + "  minus($object <info:fedora/fedora-system:def/model#state> <info:fedora/fedora-system:def/model#Deleted>)\n"
                + "  and $object <http://purl.org/dc/elements/1.1/title> $title \n";

        try {

            Set<DigitalObject> templatesFound = query(query, userToken);

            log.trace(
                    "Exiting DomsResourceIndex."
                            + "allTemplatesInCollectionForContentModel; returning "
                            + "templatesFound = " + templatesFound.toString());

            return templatesFound.toArray(
                    new DigitalObject[templatesFound.size()]);
        } catch (Exception e) {
            log.error(
                    "ResourceIndex.allTemplatesInCollectionForContentModel():"
                            + " Caught exception: ", e);
            throw new Exception("Query failed due to internal error.");
        }
    }
}
