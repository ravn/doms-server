package dk.statsbiblioteket.doms.central;

import dk.statsbiblioteket.doms.centralWebservice.*;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.bitstorage.Bitstorage;
import dk.statsbiblioteket.doms.central.connectors.ecm.ECM;
import dk.statsbiblioteket.doms.webservices.Credentials;
import dk.statsbiblioteket.doms.webservices.ConfigCollection;
import dk.statsbiblioteket.doms.bitstorage.highlevel.Characterisation;

import javax.jws.WebParam;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import java.lang.String;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 18, 2010
 * Time: 2:01:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class CentralWebserviceImpl implements CentralWebservice {


    @Resource
    WebServiceContext context;

    private static Log log = LogFactory.getLog(
            CentralWebserviceImpl.class);
    private String bitstorageWSDL;
    private String ecmLocation;
    private String fedoraLocation;
    private String bitstorageLocation;


    public CentralWebserviceImpl() {
        bitstorageWSDL = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.bitstorageWSDL");
        ecmLocation = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.ecmLocation");
        fedoraLocation = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.fedoraLocation");
    }

    public String newObject(
            @WebParam(name = "pid", targetNamespace = "") String pid)
            throws MethodFailedException, InvalidCredentialsException {

        try {
            Credentials creds = getCredentials();
            ECM ecm = new ECM(creds, ecmLocation);
            return ecm.createNewObject(pid);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    public void deleteObject(
            @WebParam(name = "pid", targetNamespace = "") String pid)
            throws MethodFailedException, InvalidCredentialsException {
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            fedora.modifyObjectState(pid, fedora.STATE_DELETED);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    public void markPublishedObject(
            @WebParam(name = "pid", targetNamespace = "") String pid)
            throws MethodFailedException, InvalidCredentialsException {

        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            fedora.modifyObjectState(pid, fedora.STATE_ACTIVE);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void markInProgressObject(
            @WebParam(name = "pid", targetNamespace = "") String pid)
            throws MethodFailedException, InvalidCredentialsException {
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            fedora.modifyObjectState(pid, fedora.STATE_INACTIVE);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    public void modifyDatastream(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "datastream", targetNamespace = "")
            String datastream,
            @WebParam(name = "contents", targetNamespace = "")
            String contents)
            throws MethodFailedException, InvalidCredentialsException {
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            fedora.modifyDatastreamByValue(pid, datastream, contents);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    public String getDatastreamContents(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "datastream", targetNamespace = "")
            String datastream)
            throws MethodFailedException, InvalidCredentialsException {
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            return fedora.getXMLDatastreamContents(pid, datastream);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    public void addFileFromPermanentURL(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "filename", targetNamespace = "") String filename,
            @WebParam(name = "md5sum", targetNamespace = "") String md5Sum,
            @WebParam(name = "permanentURL", targetNamespace = "")
            String permanentURL,
            @WebParam(name = "formatURI", targetNamespace = "")
            String formatURI)
            throws InvalidCredentialsException, MethodFailedException {
        try {
            Credentials creds = getCredentials();
            Bitstorage bs = new Bitstorage(creds, bitstorageLocation);
            Characterisation emptycharac = new Characterisation();
            emptycharac.setValidationStatus("valid");
            emptycharac.setValidationStatus(formatURI);
            bs.uploadFileToObjectFromPermanentURLWithCharacterisation(pid,
                                                                      filename,
                                                                      permanentURL,
                                                                      md5Sum,
                                                                      emptycharac);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }


    public String getFileObjectWithURL(
            @WebParam(name = "URL", targetNamespace = "") String url) {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addRelation(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "subject", targetNamespace = "")
            String subject,
            @WebParam(name = "predicate", targetNamespace = "")
            String predicate,
            @WebParam(name = "object", targetNamespace = "")
            String object)
            throws InvalidCredentialsException, MethodFailedException {
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            fedora.addRelation(pid, subject, predicate, object);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    private Credentials getCredentials() {
        HttpServletRequest request = (HttpServletRequest) context
                .getMessageContext()
                .get(MessageContext.SERVLET_REQUEST);
        Credentials creds = (Credentials) request.getAttribute("Credentials");
        if (creds == null) {
            log.warn("Attempted call at Bitstorage without credentials");
            creds = new Credentials("", "");
        }
        return creds;

    }

}
