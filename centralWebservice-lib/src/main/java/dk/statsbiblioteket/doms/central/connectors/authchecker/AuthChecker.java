package dk.statsbiblioteket.doms.central.connectors.authchecker;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import dk.statsbiblioteket.doms.authchecker.user.User;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.Connector;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: abr Date: 9/19/11 Time: 4:06 PM To change this template use File | Settings | File
 * Templates.
 */
public class AuthChecker extends Connector {
    private WebResource restApi;

    public AuthChecker(String authCheckerLocation)
            throws
            MalformedURLException {
        super(null, authCheckerLocation);
        restApi = client.resource(authCheckerLocation);
    }

    public User createTempUser(String username,
                               List<String> roles)
            throws
            BackendMethodFailedException,
            BackendInvalidCredsException,
            BackendInvalidResourceException {


        try {
            User user;

            WebResource
                    tmp =
                    restApi.path("/createTempUser/").path(URLEncoder.encode(username, "UTF-8")).path("/withTheseRoles");
            for (String role : roles) {
                if (role.contains("@")) {
                    String[] components = role.split("@");
                    tmp = tmp.queryParam(components[0], components[1]);
                } else {
                    tmp = tmp.queryParam("role", role);
                }
            }

            user = tmp.accept(MediaType.TEXT_XML).post(User.class);
            return user;

        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource not found", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        }

    }

    public User createTempAdminUser(String username,
                                    List<String> roles)
            throws
            BackendMethodFailedException,
            BackendInvalidCredsException,
            BackendInvalidResourceException {
        try {
            User user;

            WebResource
                    tmp =
                    restApi.path("/createAdminUser/")
                           .path(URLEncoder.encode(username, "UTF-8"))
                           .path("/WithTheseRoles");
            for (String role : roles) {
                if (role.contains("@")) {  //TODO dangerous logic
                    String[] components = role.split("@");
                    tmp = tmp.queryParam(components[0], components[1]);
                } else {
                    tmp = tmp.queryParam("role", role);
                }
            }

            user = tmp.accept(MediaType.TEXT_XML).post(User.class);
            return user;

        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource not found", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        }
    }
}
