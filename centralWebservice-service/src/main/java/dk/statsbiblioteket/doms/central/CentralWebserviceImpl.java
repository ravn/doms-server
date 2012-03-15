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


import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.authchecker.AuthChecker;

import dk.statsbiblioteket.doms.central.connectors.ecm.ECM;
import dk.statsbiblioteket.doms.central.connectors.fedora.*;
import dk.statsbiblioteket.doms.central.connectors.updatetracker.UpdateTracker;
import dk.statsbiblioteket.doms.central.connectors.updatetracker.UpdateTrackerRecord;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.lang.String;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 18, 2010
 * Time: 2:01:51 PM
 * To change this template use File | Settings | File Templates.
 */
@WebService(endpointInterface = "dk.statsbiblioteket.doms.central.CentralWebservice")
public class CentralWebserviceImpl implements CentralWebservice {

    @Resource
    WebServiceContext context;

    private static Log log = LogFactory.getLog(
            CentralWebserviceImpl.class);
    private static Lock lock = new Lock();

    private String fedoraLocation;
    private String updateTrackerLocation;
    private String authCheckerLocation;
    private String pidgeneratorclassString;
    private String fedoraconnectorclassstring;


    public CentralWebserviceImpl() {
        pidgeneratorclassString = ConfigCollection.getProperties()
                .getProperty("dk.statsbiblioteket.doms.ecm.pidGenerator.client");
        fedoraconnectorclassstring =
                ConfigCollection.getProperties().getProperty("dk.statsbiblioteket.doms.ecm.fedora.connector");
        fedoraLocation = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.fedoraLocation");
        updateTrackerLocation = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.updateTrackerLocation");
        authCheckerLocation = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.authCheckerLocation");

    }


    @Override
    public String newObject(@WebParam(name = "pid", targetNamespace = "") String pid,
                            @WebParam(name = "oldID", targetNamespace = "") List<String> oldID,
                            @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        try {
            log.trace(
                    "Entering newObject with params pid=" + pid + " and oldIDs="
                    + oldID.toString());
            Credentials creds = getCredentials();
            ECM ecm = new ECM(creds,fedoraLocation,fedoraconnectorclassstring,pidgeneratorclassString);
            return ecm.createNewObject(pid, oldID, comment);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);

        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }
    }

    @Override
    public ObjectProfile getObjectProfile(@WebParam(name = "pid", targetNamespace = "") String pid)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        try {
            log.trace("Entering getObjectProfile with params pid='" + pid+"'");
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            dk.statsbiblioteket.doms.central.connectors.fedora.ObjectProfile fprofile = fedora.getObjectProfile(pid);
            ObjectProfile wprofile = new ObjectProfile();
            wprofile.setTitle(fprofile.getLabel());
            wprofile.setPid(fprofile.getPid());
            wprofile.setState(fprofile.getState());
            wprofile.setCreatedDate(fprofile.getObjectCreatedDate().getTime());
            wprofile.setModifiedDate(fprofile.getObjectLastModifiedDate().getTime());
            wprofile.getContentmodels().addAll(fprofile.getContentModels());
            switch (fprofile.getType()){
                case CONTENT_MODEL:
                    wprofile.setType("ContentModel");
                    break;
                case DATA_OBJECT:
                    wprofile.setType("DataObject");
                    break;
                case TEMPLATE:
                    wprofile.setType("TemplateObject");
                    break;
                case COLLECTION:
                    wprofile.setType("CollectionObject");
                    break;
                case FILE:
                    wprofile.setType("FileObject");
                    break;
            }

            //Datastreams
            List<DatastreamProfile> datastreams = wprofile.getDatastreams();
            for (dk.statsbiblioteket.doms.central.connectors.fedora.DatastreamProfile datastreamProfile : fprofile
                    .getDatastreams()) {
                DatastreamProfile wdprofile = new DatastreamProfile();
                wdprofile.setId(datastreamProfile.getID());
                wdprofile.setLabel(datastreamProfile.getLabel());
                wdprofile.setChecksum(new Checksum());
                wdprofile.getChecksum().setType(datastreamProfile.getChecksumType());
                wdprofile.getChecksum().setValue(datastreamProfile.getChecksum());
                wdprofile.setMimeType(datastreamProfile.getMimeType());
                wdprofile.setFormatUri(datastreamProfile.getFormatURI());
                wdprofile.setInternal(datastreamProfile.isInternal());
                if (!wdprofile.isInternal()){
                    wdprofile.setUrl(datastreamProfile.getUrl());
                }
                datastreams.add(wdprofile);
            }

            //Relations
            List<Relation> wrelations = wprofile.getRelations();
            wrelations.addAll(convertRelations(fprofile.getRelations()));

            return wprofile;



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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);

        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    @Override
    public void setObjectLabel(@WebParam(name = "pid", targetNamespace = "") String pid,
                               @WebParam(name = "name", targetNamespace = "") String name,
                               @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        try {
            log.trace("Entering setObjectLabel with params pid=" + pid
                      + " and name=" + name);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            fedora.modifyObjectLabel(pid, name, comment);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);

        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }


    }


    @Override
    public void deleteObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids,
                             @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        try {
            log.trace("Entering deleteObject with params pid=" + pids);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            for (String pid : pids) {
                fedora.modifyObjectState(pid, fedora.STATE_DELETED, comment);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }

    }


    @Override
    public void markPublishedObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids,
                                    @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        List<String> activated = new ArrayList<String>();
        try {
            log.trace("Entering markPublishedObject with params pids=" + pids);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            for (String pid : pids) {
                fedora.modifyObjectState(pid, fedora.STATE_ACTIVE, comment);
                activated.add(pid);
            }
        } catch (BackendMethodFailedException e) {
            log.warn("Failed to execute method", e);
            //rollback
            comment = comment + ": Publishing failed, marking back to InProgress";
            markInProgressObject(activated, comment);
            throw new MethodFailedException("Method failed to execute: "+e.getMessage(),
                                            "Method failed to execute: "+e.getMessage(),
                                            e);
        } catch (BackendInvalidCredsException e) {
            log.debug("User supplied invalid credentials", e);
            comment = comment + ": Publishing failed, marking back to InProgress";
            markInProgressObject(activated, comment);
            throw new InvalidCredentialsException("Invalid Credentials Supplied",
                                                  "Invalid Credentials Supplied",
                                                  e);
        } catch (MalformedURLException e) {
            log.error("caught problemException", e);
            comment = comment + ": Publishing failed, marking back to InProgress";
            markInProgressObject(activated, comment);
            throw new MethodFailedException("Webservice Config invalid",
                                            "Webservice Config invalid",
                                            e);

        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);

            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            comment = comment + ": Publishing failed, marking back to InProgress";
            markInProgressObject(activated, comment);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }


    }


    @Override
    public void markInProgressObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids,
                                     @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        try {
            log.trace("Entering markInProgressObject with params pids=" + pids);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            for (String pid : pids) {
                fedora.modifyObjectState(pid, fedora.STATE_INACTIVE, comment);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);

        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }

    }


    @Override
    public void modifyDatastream(@WebParam(name = "pid", targetNamespace = "") String pid,
                                 @WebParam(name = "datastream", targetNamespace = "") String datastream,
                                 @WebParam(name = "contents", targetNamespace = "") String contents,
                                 @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        try {
            log.trace("Entering modifyDatastream with params pid=" + pid
                      + " and datastream=" + datastream + " and contents="
                      + contents);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            fedora.modifyDatastreamByValue(pid, datastream, contents, comment);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);

        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }

    }


    public String getDatastreamContents(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "datastream", targetNamespace = "")
            String datastream)
            throws MethodFailedException, InvalidCredentialsException, InvalidResourceException {
        try {
            log.trace("Entering getDatastreamContents with params pid=" + pid
                      + " and datastream=" + datastream);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);

        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    @Override
    public void addFileFromPermanentURL(@WebParam(name = "pid", targetNamespace = "") String pid,
                                        @WebParam(name = "filename", targetNamespace = "") String filename,
                                        @WebParam(name = "md5sum", targetNamespace = "") String md5Sum,
                                        @WebParam(name = "permanentURL", targetNamespace = "") String permanentURL,
                                        @WebParam(name = "formatURI", targetNamespace = "") String formatURI,
                                        @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();


        try {
            log.trace("Entering addFileFromPermamentURL with params pid=" + pid
                      + " and filename=" + filename + " and md5sum=" + md5Sum
                      + " and permanentURL=" + permanentURL + " and formatURI="
                      + formatURI);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,fedoraLocation);


            String existingObject = getFileObjectWithURL(permanentURL);
            if (existingObject != null) {
                log.warn("Attempt to add a permament url that already exists"
                         + "in DOMS");
                throw new MethodFailedException(
                        "This permanent url has already "
                        + "been added to the object '" +
                        existingObject + "'",
                        "This permanent url has already "
                        + "been added to the object '" +
                        existingObject + "'");
            }
            fedora.addExternalDatastream(pid,"CONTENTS",filename,permanentURL,formatURI,"application/octet-stream",comment);
            setObjectLabel(pid,permanentURL,comment);

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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);

        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }

    }


    public String getFileObjectWithURL(
            @WebParam(name = "URL", targetNamespace = "") String url)
            throws MethodFailedException, InvalidCredentialsException, InvalidResourceException {
        try {
            log.trace("Entering getFileObjectWithURL with param url=" + url);
            Credentials creds = getCredentials();
            TripleStore tripleStore = new TripleStoreRest(creds,fedoraLocation);
            List<String> objects = tripleStore.listObjectsWithThisLabel(url);

            if (objects != null && !objects.isEmpty()) {
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

    @Override
    public void addRelation(@WebParam(name = "pid", targetNamespace = "") String pid,
                            @WebParam(name = "relation", targetNamespace = "") Relation relation,
                            @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        try {
            log.trace("Entering addRelation with params pid=" + pid
                      + " and subject=" + relation.getSubject() + " and predicate="
                      + relation.getPredicate() + " and object=" + relation.getObject());
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            fedora.addRelation(pid, relation.subject, relation.predicate, relation.object, relation.literal, comment);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }

    }

    @Override
    public List<Relation> getRelations(@WebParam(name = "pid", targetNamespace = "") String pid)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {

        log.trace("Entering getRelations with params pid='" + pid + "'");
        return getNamedRelations(pid, null);
    }

    @Override
    public List<Relation> getNamedRelations(@WebParam(name = "pid", targetNamespace = "") String pid,
                                            @WebParam(name = "predicate", targetNamespace = "") String predicate)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        try {
            log.trace("Entering getNamedRelations with params pid='" + pid + "'");
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            List<FedoraRelation> fedorarels = fedora.getNamedRelations(pid, predicate);
            return convertRelations(fedorarels);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }

    }

    @Override
    public List<Relation> getInverseRelations(@WebParam(name = "pid", targetNamespace = "") String pid)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        try {
            log.trace("Entering getInverseRelations with params pid='" + pid + "'");
            Credentials creds = getCredentials();
            TripleStore tripleStore = new TripleStoreRest(creds,fedoraLocation);
            List<FedoraRelation> fedorarels = tripleStore.getInverseRelations(pid, null);
            return convertRelations(fedorarels);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    @Override
    public List<Relation> getInverseRelationsWithPredicate(@WebParam(name = "pid", targetNamespace = "") String pid,
                                                           @WebParam(name = "predicate", targetNamespace = "")
                                                           String predicate)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        try {
            log.trace("Entering getInverseRelations with params pid='" + pid + "'");
            Credentials creds = getCredentials();
            TripleStore tripleStore = new TripleStoreRest(creds,fedoraLocation);
            List<FedoraRelation> fedorarels = tripleStore.getInverseRelations(pid, predicate);
            return convertRelations(fedorarels);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }


    @Override
    public void deleteRelation(@WebParam(name = "pid", targetNamespace = "") String pid,
                               @WebParam(name = "relation", targetNamespace = "") Relation relation,
                               @WebParam(name = "comment", targetNamespace = "") String comment)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        long token = lock.getReadAndWritePerm();
        try {
            log.trace("Entering deleteRelation with params pid=" + pid
                      + " and subject=" + relation.subject + " and predicate="
                      + relation.predicate + " and object=" + relation.object);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            fedora.deleteRelation(pid, relation.subject, relation.predicate, relation.object, relation.literal, comment);
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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        } finally {
            lock.releaseReadAndWritePerm(token);
        }

    }


    public ViewBundle getViewBundle(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "name", targetNamespace = "")
            String viewAngle)
            throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        log.trace("Entering getViewBundle with params pid=" + pid
                  + " and viewAngle=" + viewAngle);
        /*
        * Pseudo kode here
        * We need to figure two things out
        * the bundle
        * the type
        * ECM generates the bundle
        * The type is the entry content model of the origin pid
        * */

        Credentials creds = getCredentials();
        try {
            ECM ecm = new ECM(creds,fedoraLocation,fedoraconnectorclassstring,pidgeneratorclassString);
/*
            List<String> types = ecm.getEntryContentModelsForObject(pid,
                                                                    viewAngle);
            if (types.isEmpty()) {
                throw new BackendInvalidResourceException("Pid '"+pid+"'is not an entry object for angle '"+viewAngle+"'");
            }
*/
            String bundleContentsString = ecm.createBundle(pid, viewAngle);

            ViewBundle viewBundle = new ViewBundle();
            viewBundle.setId(pid);
            viewBundle.setContents(bundleContentsString);
            return viewBundle;

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
        } catch (BackendInvalidResourceException e) {
            log.debug("Invalid resource requested", e);
            throw new InvalidResourceException("Invalid Resource Requested",
                                               "Invalid Resource Requested",
                                               e);
        } catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }
    }

    public List<RecordDescription> getIDsModified(
            @WebParam(name = "since", targetNamespace = "") long since,
            @WebParam(name = "collectionPid", targetNamespace = "")
            String collectionPid,
            @WebParam(name = "viewAngle", targetNamespace = "")
            String viewAngle,
            @WebParam(name = "state", targetNamespace = "") String state,
            @WebParam(name = "offset", targetNamespace = "") Integer offset,
            @WebParam(name = "limit", targetNamespace = "") Integer limit)
            throws InvalidCredentialsException, MethodFailedException {
        try {
            logEntering("getIDsModified",
                        since + "",
                        collectionPid,
                        viewAngle,
                        state,
                        offset + "",
                        limit + "");
            Credentials creds = getCredentials();
            UpdateTracker tracker = new UpdateTracker(creds,
                                                      updateTrackerLocation);
            if (state == null || state.isEmpty()) {
                state = "Published";
            }
            List<UpdateTrackerRecord> modifieds
                    = tracker.listObjectsChangedSince(
                    collectionPid,
                    viewAngle,
                    since,
                    state,
                    offset,
                    limit);
            return transform(modifieds);
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


    public long getLatestModified(
            @WebParam(name = "collectionPid", targetNamespace = "")
            String collectionPid,
            @WebParam(name = "viewAngle", targetNamespace = "")
            String viewAngle,
            @WebParam(name = "state", targetNamespace = "")
            String state)
            throws InvalidCredentialsException, MethodFailedException {
        try {
            logEntering("getLatestModified", collectionPid, viewAngle, state);
            Credentials creds = getCredentials();
            UpdateTracker tracker = new UpdateTracker(creds,
                                                      updateTrackerLocation);
            return tracker.getLatestModification(collectionPid,
                                                 viewAngle,
                                                 state);
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

    @Override
    public List<String> findObjectFromDCIdentifier(@WebParam(name = "string", targetNamespace = "") String string)
            throws InvalidCredentialsException, MethodFailedException {
        try {
            log.trace("Entering findObjectFromDCIdentifier with param string=" + string);
            Credentials creds = getCredentials();
            TripleStore tripleStore = new TripleStoreRest(creds,fedoraLocation);
            List<String> objects = tripleStore.findObjectFromDCIdentifier(string);


            return objects;
/*
            if (objects != null && !objects.isEmpty()) {
                return objects.get(0);
            } else {
                return null;
            }
*/

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

    @Override
    public List<SearchResult> findObjects(@WebParam(name = "query", targetNamespace = "") String query,
                                          @WebParam(name = "offset", targetNamespace = "") int offset,
                                          @WebParam(name = "pageSize", targetNamespace = "") int pageSize)
            throws InvalidCredentialsException, MethodFailedException {
        try {
            log.trace("Entering findObjectsr with param query=" + query + ", offset="+offset+", pageSize="+pageSize);
            Credentials creds = getCredentials();
            Fedora fedora = FedoraFactory.newInstance(creds,
                                                      fedoraLocation);
            List<dk.statsbiblioteket.doms.central.connectors.fedora.SearchResult> fresults =
                    fedora.fieldsearch(query, offset, pageSize);
            List<SearchResult> wresults = new ArrayList<SearchResult>();
            for (dk.statsbiblioteket.doms.central.connectors.fedora.SearchResult fresult : fresults) {
                SearchResult wresult = new SearchResult();
                wresult.setPid(fresult.getPid());
                wresult.setTitle(fresult.getLabel());
                wresult.setState(fresult.getState());
                wresult.setCreatedDate(fresult.getcDate());
                wresult.setModifiedDate(fresult.getmDate());
                wresults.add(wresult);
            }

            return wresults;

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
        }  catch (Exception e) {
            log.warn("Caught Unknown Exception", e);
            throw new MethodFailedException("Server error", "Server error", e);
        }

    }


    @Override
    public void lockForWriting() throws InvalidCredentialsException, MethodFailedException {
        Credentials creds = getCredentials();

        lock.lockForWriting(); //DO the lock


        try { //Execute a command to flush the unflushed triple changes.
            TripleStore tripleStore = new TripleStoreRest(creds,fedoraLocation);
            tripleStore.flushTripples();
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

    @Override
    public void unlockForWriting() throws InvalidCredentialsException, MethodFailedException {
        lock.unlockForWriting();
    }

    @Override
    public User createTempAdminUser(@WebParam(name = "username", targetNamespace = "") String username,
                                    @WebParam(name = "roles", targetNamespace = "") List<String> roles)
            throws InvalidCredentialsException, MethodFailedException {
        try {
            Credentials creds = getCredentials();//TODO perhaps we should check something here, against context.xml?
            AuthChecker auth = new AuthChecker(authCheckerLocation);
            dk.statsbiblioteket.doms.authchecker.user.User auser = auth.createTempAdminUser(username, roles);
            User user = new User();
            user.setUsername(auser.getUsername());
            user.setPassword(auser.getPassword());
            return user;
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

    @Override
    public List<String> getObjectsInCollection(
            @WebParam(name = "collectionPid", targetNamespace = "") String collectionPid,
            @WebParam(name = "contentModelPid", targetNamespace = "") String contentModelPid)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        try {
            log.trace("Entering getObjectsInCollection with param collectionPid=" + collectionPid + " and contentModelPid="+contentModelPid);
            Credentials creds = getCredentials();
            TripleStore tripleStore = new TripleStoreRest(creds,fedoraLocation);
            List<String> objects = tripleStore.getObjectsInCollection(collectionPid,contentModelPid);
            return objects;
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

    private List<RecordDescription> transform(List<UpdateTrackerRecord> input) {
        List<RecordDescription> output = new ArrayList<RecordDescription>();
        for (UpdateTrackerRecord updateTrackerRecord : input) {
            output.add(transform(updateTrackerRecord));
        }
        return output;
    }

    private RecordDescription transform(UpdateTrackerRecord updateTrackerRecord) {
        RecordDescription a = new RecordDescription();
        a.setCollectionPid(updateTrackerRecord.getCollectionPid());
        a.setEntryContentModelPid(updateTrackerRecord.getEntryContentModelPid());
        a.setPid(updateTrackerRecord.getPid());
        a.setDate(updateTrackerRecord.getDate().getTime());
        return a;
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

    private void logEntering(String method, String... params) {
        if (log.isTraceEnabled()) {
            String command = method + "(";
            for (String param : params) {
                command = command + " " + param + ",";
            }
            command = command.substring(0, command.length() - 1) + ")";
            log.trace("Entering " + command);
        }
    }

    private static List<Relation> convertRelations(List<FedoraRelation> fedorarels) {
        List<Relation> outrealtions = new ArrayList<Relation>();
        for (FedoraRelation fedorarel : fedorarels) {
            Relation outrel = new Relation();
            outrel.setSubject(fedorarel.getSubject());
            outrel.setPredicate(fedorarel.getPredicate());
            outrel.setObject(fedorarel.getObject());
            outrel.setLiteral(fedorarel.isLiteral());
            outrealtions.add(outrel);
        }
        return outrealtions;
    }


}
