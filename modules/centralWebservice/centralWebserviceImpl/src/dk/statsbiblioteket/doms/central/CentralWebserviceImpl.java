/*
 * $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 * The DOMS project.
 * Copyright (C) 2007-2010  The State and University Library
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


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
import javax.jws.WebService;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import java.lang.String;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 18, 2010
 * Time: 2:01:51 PM
 * To change this template use File | Settings | File Templates.
 */
@WebService(endpointInterface = "dk.statsbiblioteket.doms.centralWebservice.CentralWebservice")
public class CentralWebserviceImpl implements CentralWebservice {


    @Resource
    WebServiceContext context;

    private static Log log = LogFactory.getLog(
            CentralWebserviceImpl.class);
    private String ecmLocation;
    private String fedoraLocation;
    private String bitstorageLocation;


    public CentralWebserviceImpl() {
        bitstorageLocation = ConfigCollection.getProperties().getProperty(
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
            @WebParam(name = "pids", targetNamespace = "")
            List<java.lang.String> pids)
            throws InvalidCredentialsException, MethodFailedException {
        List<String> activated = new ArrayList<String>();
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            for (String pid : pids) {
                fedora.modifyObjectState(pid, fedora.STATE_ACTIVE);
                activated.add(pid);
            }
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            //rollback
            markInProgressObject(activated);
            throw new MethodFailedException("Method failed to execute",
                                            "Method failed to execute",
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            markInProgressObject(activated);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            markInProgressObject(activated);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);
        }
        catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            markInProgressObject(activated);
            throw new MethodFailedException("Server error", "Server error", e);
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void markInProgressObject(
            @WebParam(name = "pids", targetNamespace = "")
            List<java.lang.String> pids)
            throws MethodFailedException, InvalidCredentialsException {
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            for (String pid : pids) {
                fedora.modifyObjectState(pid, fedora.STATE_INACTIVE);
            }
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
            String existingObject = getFileObjectWithURL(permanentURL);
            if (existingObject != null){
                log.warn("Attempt to add a permament url that already exists"
                         + "in DOMS");
                throw new MethodFailedException("This permanent url has already "
                                                + "been added to the object '"+
                existingObject+"'","This permanent url has already "
                                                + "been added to the object '"+
                existingObject+"'");
            }
            Characterisation emptycharac = new Characterisation();
            emptycharac.setValidationStatus("valid");
            emptycharac.setBestFormat(formatURI);
            emptycharac.getFormatURIs().add(formatURI);
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
            @WebParam(name = "URL", targetNamespace = "") String url)
            throws MethodFailedException, InvalidCredentialsException {
        try {
            Credentials creds = getCredentials();
            Fedora fedora = new Fedora(creds,
                                       fedoraLocation);
            List<String> objects = fedora.listObjectsWithThisLabel(url);

            if (objects != null && !objects.isEmpty()){
                return objects.get(0);
            } else {
                return null;
            }

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
            log.warn("Attempted call at Central without credentials");
            creds = new Credentials("", "");
        }
        return creds;

    }

}
