package dk.statsbiblioteket.doms.central.connectors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import dk.statsbiblioteket.doms.central.connectors.builderpattern.AuthException;
import dk.statsbiblioteket.doms.central.connectors.builderpattern.Datastream;
import dk.statsbiblioteket.doms.central.connectors.builderpattern.ImprovedFedora;
import dk.statsbiblioteket.doms.central.connectors.builderpattern.NotFoundException;
import dk.statsbiblioteket.doms.central.connectors.builderpattern.ObjectStructure;
import dk.statsbiblioteket.doms.central.connectors.builderpattern.Relation;
import dk.statsbiblioteket.doms.central.connectors.builderpattern.SimpleObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImprovedFedoraImpl implements ImprovedFedora {

    private static final String AS_OF_DATE_TIME = "asOfDateTime";
    private static final String LOG_MESSAGE = "logMessage";
    private static final String LABEL = "label";
    private static final String OWNER = "owner";
    private static final String STATE = "state";
    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    private static Log log = LogFactory.getLog(ImprovedFedoraImpl.class);


    public final static Client client = Client.create();
    private WebResource restApi;

    protected String location;

    public ImprovedFedoraImpl(String username, String password, String location) {
        this.location = location;

        restApi = client.resource(location + "/objects");
        restApi.addFilter(new HTTPBasicAuthFilter(username, password));

    }

    @Override
    public List<String> getObjectsInCollection(String collectionPid, String contentModelPid) throws AuthException {
        return null;
    }

    @Override
    public List<String> findPidFromDCIdentifier(String identifier) throws AuthException {
        return null;
    }

    @Override
    public List<String> findPidFromObjectLabel(String label) throws AuthException {
        return null;
    }

    @Override
    public SimpleObject cloneTemplate(String templatepid, List<String> oldIDs, String logMessage) throws
                                                                                                  AuthException,
                                                                                                  NotFoundException {
        return null;
    }

    @Override
    public SimpleObject newObject(List<String> oldIDs, List<String> collections, String logMessage) throws
                                                                                                    AuthException {
        return null;
    }

    @Override
    public ObjectStructure getObjectStructure(String pid, Long asOfTime) throws AuthException, NotFoundException {
        return null;
    }

    @Override
    public void modifyObject(SimpleObject object, Long lastModifiedDate, String logMessage) throws
                                                                                            AuthException,
                                                                                            NotFoundException {
        try {
            WebResource webmethod = restApi.path("/").path(enc(object));
            if (object.getLabel() != null) {
                webmethod = webmethod.queryParam(LABEL, object.getLabel());
            }
            if (object.getOwner() != null) {
                webmethod = webmethod.queryParam(OWNER, object.getOwner());
            }
            if (object.getState() != null) {
                webmethod = webmethod.queryParam(STATE, object.getState().getShortForm());
            }
            webmethod.queryParam(LOG_MESSAGE, logMessage).put();
        } catch (UniformInterfaceException e) {
            handleException(e);
        }

    }


    @Override
    public void deleteObject(String pid, Long lastModifiedDate, String logMessage) throws
                                                                                   AuthException,
                                                                                   NotFoundException {
        try {
            WebResource webmethod = restApi.path("/").path(enc(pid));
            if (lastModifiedDate != null) {
                webmethod = webmethod.queryParam(LAST_MODIFIED_DATE, format(lastModifiedDate));
            }
            webmethod.queryParam(LOG_MESSAGE, logMessage).delete();
        } catch (UniformInterfaceException e) {
            handleException(e);
        }
    }

    @Override
    public void setRelsInt(String pid, Map<String, Set<Relation>> relations, Long lastModifiedDate,
                           String logMessage) throws AuthException, NotFoundException {

    }

    @Override
    public Map<String, Set<Relation>> getRelsInt(String pid, Long asOfTime) throws AuthException, NotFoundException {
        return null;
    }

    @Override
    public void setRelsExt(String pid, Set<Relation> relations, Long lastModifiedDate, String logMessage) throws
                                                                                                          AuthException,
                                                                                                          NotFoundException {

    }

    @Override
    public Set<Relation> getRelsExt(String pid, Long asOfTime) throws AuthException, NotFoundException {
        return null;
    }

    @Override
    public Map<String, Set<Relation>> getInverseRelations(String pid, String predicate) throws
                                                                                        AuthException,
                                                                                        NotFoundException {
        return null;
    }

    @Override
    public Datastream getDatastream(String pid, String datastreamID, Long asOfTime) throws
                                                                                    AuthException,
                                                                                    NotFoundException {
        return null;
    }

    @Override
    public void modifyDatastream(String pid, Datastream datastream, Long lastModifiedDate, String logMessage) throws
                                                                                                              AuthException,
                                                                                                              NotFoundException {

    }

    @Override
    public void deleteDatastream(String pid, String datastreamID, Long lastModifiedDate, String logMessage) throws
                                                                                                            AuthException,
                                                                                                            NotFoundException {
        try {

            WebResource webmethod = restApi.path("/").path(enc(pid)).path("/").path(enc(datastreamID));
            if (lastModifiedDate != null) {
                webmethod = webmethod.queryParam(LAST_MODIFIED_DATE, format(lastModifiedDate));
            }
            webmethod.queryParam(LOG_MESSAGE, logMessage).delete();
        } catch (UniformInterfaceException e) {
            handleException(e);
        }

    }


    @Override
    public InputStream getDatastreamContents(String pid, String datastreamId, Long asOfTime) throws AuthException,
                                                                                                    NotFoundException {
        try {
            WebResource webResource = restApi.path("/")
                                             .path(enc(pid))
                                             .path("/datastreams/")
                                             .path(enc(datastreamId))
                                             .path("/content");
            if (asOfTime != null) {
                webResource = webResource.queryParam(AS_OF_DATE_TIME, format(asOfTime));
            }
            return webResource.get(InputStream.class);
        } catch (UniformInterfaceException e) {
            handleException(e);
            return null;
        }

    }

    private String format(Long lastModifiedDate) {
        //TODO
        return null;
    }


    private void handleException(UniformInterfaceException e) throws AuthException, NotFoundException {
        if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
            throw new AuthException("Invalid Credentials Supplied", e);
        } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
            throw new NotFoundException("Resource not found", e);
        } else {
            throw new RuntimeException("Server error", e);
        }
    }

    private String enc(String pid) {
        try {
            return URLEncoder.encode(pid, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not known....", e);
        }
    }


    private String enc(SimpleObject object) {
        return enc(object.getPid());
    }

}
