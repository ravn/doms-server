package dk.statsbiblioteket.doms.central.connectors.bitstorage;

import dk.statsbiblioteket.doms.bitstorage.highlevel.*;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.Connector;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.webservices.Credentials;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 25, 2010
 * Time: 1:50:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bitstorage extends Connector {

    private HighlevelBitstorageSoapWebservice service;
    private static final QName QNAME = new QName(
            "http://highlevel.bitstorage.doms.statsbiblioteket.dk/",
            "HighlevelBitstorageSoapWebserviceService");

    public Bitstorage(Credentials creds, String location)
            throws MalformedURLException {
        super(creds, location);
        URL wsdlLocation = new URL(location);
        service = new HighlevelBitstorageSoapWebserviceService(wsdlLocation,
                                                               QNAME).getHighlevelBitstorageSoapWebservicePort();
        ((BindingProvider)service).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,username);
        ((BindingProvider)service).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,password);
    }

    public void uploadFileToObjectFromPermanentURLWithCharacterisation(String pid,
                                                                       String filename,
                                                                       String permanentURL,
                                                                       String md5String,
                                                                       Characterisation characterisation)
            throws BackendMethodFailedException,
                   BackendInvalidCredsException
    {
        try {
            service.uploadFileToObjectFromPermanentURLWithCharacterisation(pid,
                                                                           filename,
                                                                           permanentURL,
                                                                           md5String,
                                                                           0,
                                                                           characterisation);
        } catch (HighlevelSoapException e) {
            throw new BackendMethodFailedException("The uploadFileToObjectFromPermanentURLWithCharacterisation method failed to execute",e);
        }
    }
}
