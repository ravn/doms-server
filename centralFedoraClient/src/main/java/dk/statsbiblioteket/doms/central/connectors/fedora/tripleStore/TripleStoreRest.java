package dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.Connector;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.FedoraUtil;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.Names;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.FedoraRelation;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/15/12
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TripleStoreRest  extends Connector implements TripleStore{



    private WebResource restApi;

    private static Log log = LogFactory.getLog(
            TripleStoreRest.class);


    public TripleStoreRest(Credentials creds, String location)
            throws MalformedURLException {
        super(creds, location);
        restApi = client.resource(location + "/risearch")
                .queryParam("type", "tuples")
                .queryParam("lang", "iTQL")
                .queryParam("format", "CSV")
                .queryParam("flush", "true")
                .queryParam("stream", "on");
        restApi.addFilter(new HTTPBasicAuthFilter(creds.getUsername(),creds.getPassword()));
    }


    @Override
    public List<FedoraRelation> getInverseRelations(String pid, String predicate)
            throws BackendMethodFailedException, BackendInvalidCredsException, BackendInvalidResourceException {
        try {


            String subject = pid;
            if (!subject.startsWith("info:fedora/")) {
                subject = "info:fedora/" + subject;
            }
            String predicateOrig = predicate;
            String queryStart = "select $object";
            if (predicate == null || predicate.isEmpty()){
                queryStart+= " $predicate";
                predicate = "$predicate";
                predicateOrig = null;
            } else {
                predicate = "<"+predicate+">";
            }

            String query = queryStart+"\n"
                           + "from <#ri>\n"
                           + "where $object "+predicate+" <" + subject + ">\n"
                           + "and ($object <fedora-model:state> <fedora-model:Active>\n" +
                           "or $object <fedora-model:state> <fedora-model:Inactive>)";
            String objects = restApi
                    .queryParam("query", query)
                    .post(String.class);
            String[] lines = objects.split("\n");
            List<FedoraRelation> relations = new ArrayList<FedoraRelation>();

            for (String line : lines) {
                if (line.startsWith("\"")) {
                    continue;
                }
                if (line.startsWith("info:fedora/")) {
                    line = line.substring("info:fedora/".length());
                }
                String[] components = line.split(",");

                if (predicate.equals("$predicate")){
                    relations.add(new FedoraRelation(components[0],components[1],pid));
                } else {
                    relations.add(new FedoraRelation(components[0],predicateOrig,pid));
                }


            }
            return relations;
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus()
                == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException(
                        "Invalid Credentials Supplied",
                        e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource not found", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        }
    }


    @Override
    public List<String> findObjectFromDCIdentifier(String string)
            throws BackendInvalidCredsException, BackendMethodFailedException {

        String query = "select $object\n"
                       + "from <#ri>\n"
                       + "where $object <dc:identifier> '" + string.replaceAll(Pattern.quote("'"), Matcher.quoteReplacement("\\'")) + "' "
                       + "and ($object <fedora-model:state> <fedora-model:Active>\n" +
                       "or $object <fedora-model:state> <fedora-model:Inactive>)";
        return genericQuery(query);
    }

    @Override
    public void flushTriples() throws BackendInvalidCredsException, BackendMethodFailedException {
        findObjectFromDCIdentifier("doms:ContentModel_DOMS");
    }


    @Override
    public List<String> getObjectsInCollection(String collectionPid, String contentModel)
            throws BackendInvalidCredsException, BackendMethodFailedException {

        if (!collectionPid.startsWith("info:fedora/")){
            collectionPid = "info:fedora/"+collectionPid.trim();
        }
        if (!contentModel.startsWith("info:fedora/")){
            contentModel = "info:fedora/"+contentModel.trim();
        }
        String query = "select $object\n"
                       + "from <#ri>\n"
                       + "where $object <"+ Names.COLLECTION_RELATION+"> <"+collectionPid+">\n"
                       + "and $object <fedora-model:hasModel> <"+contentModel+">\n"
                       + "and ($object <fedora-model:state> <fedora-model:Active>\n" +
                       "or $object <fedora-model:state> <fedora-model:Inactive>)";
        return genericQuery(query);

    }

    public List<String> listObjectsWithThisLabel(String label)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException {
        //TODO sanitize label

        String query = "select $object\n"
                       + "from <#ri>\n"
                       + "where $object <fedora-model:label> '" + label + "'";
        return genericQuery(query);
    }



    /**
     * Runs any query, that produces one column of results, and return each line as a string
     * @param query The query to execute
     * @return an empty list
     */
    public List<String> genericQuery(String query)
            throws BackendInvalidCredsException, BackendMethodFailedException {
        try {
            String objects = restApi
                    .queryParam("query", query)
                    .post(String.class);
            String[] lines = objects.split("\n");
            List<String> foundobjects = new ArrayList<String>();
            for (String line : lines) {
                if (line.startsWith("\"")) {
                    continue;
                }
                if (line.startsWith("info:fedora/")) {
                    line = line.substring("info:fedora/".length());
                }
                foundobjects.add(line);
            }
            return foundobjects;
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus()
                == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException(
                        "Invalid Credentials Supplied",
                        e);
            }  else {
                throw new BackendMethodFailedException("Server error", e);
            }
        }



    }

}
