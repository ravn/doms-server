package dk.statsbiblioteket.doms.central.connectors;

import dk.statsbiblioteket.doms.webservices.Base64;
import dk.statsbiblioteket.doms.webservices.Credentials;

import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 27, 2010
 * Time: 1:07:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Connector {

    protected String username;
    protected String password;
    protected String location;

    protected Connector(Credentials creds, String location) throws
                                                                           MalformedURLException {
        this.username = username;
        this.password = password;
        this.location = location;
    }

    protected String credsAsBase64(){
        String preBase64 = username + ":" + password;
        String base64 = Base64.encodeBytes(preBase64.getBytes());
        return "Basic "+base64;
    }



}
