package dk.statsbiblioteket.doms.central.connectors.ecm;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.Connector;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.webservices.Credentials;

import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 25, 2010
 * Time: 1:50:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECM extends Connector {

    private WebResource restApi;

    public ECM(Credentials creds, String ecmLocation)
            throws MalformedURLException {
        super(creds, ecmLocation);
        Client client = Client.create();
        restApi = client.resource(location);
    }

    public String createNewObject(String templatePid) throws
                                                      BackendMethodFailedException,
                                                      BackendInvalidCredsException {
        try {
            String clonePID = restApi
                    .path("/clone/")
                    .path(URLEncoder.encode(templatePid, "UTF-8"))
                    .header("Authorization", credsAsBase64())
                    .post(String.class);
            return clonePID;
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus()
                == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException(
                        "Invalid Credentials Supplied", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        }


    }


}
