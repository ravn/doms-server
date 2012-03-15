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


package dk.statsbiblioteket.doms.central.connectors.ecm;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.Connector;
import dk.statsbiblioteket.doms.ecm.repository.CachingConnector;
import dk.statsbiblioteket.doms.ecm.repository.FedoraConnector;
import dk.statsbiblioteket.doms.ecm.repository.FedoraUserToken;
import dk.statsbiblioteket.doms.ecm.repository.PidGenerator;
import dk.statsbiblioteket.doms.ecm.repository.exceptions.*;
import dk.statsbiblioteket.doms.ecm.repository.utils.DocumentUtils;
import dk.statsbiblioteket.doms.ecm.services.templates.TemplateSubsystem;
import dk.statsbiblioteket.doms.ecm.services.view.ViewSubsystem;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 25, 2010
 * Time: 1:50:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECM  {


    private FedoraConnector fedoraConnector;
    private boolean initialised;
    private ViewSubsystem view;
    private TemplateSubsystem temps;
    private PidGenerator pidGenerator;

    public ECM(Credentials creds, String fedoraserverurl, String fedoraconnectorclassstring,
               String pidgeneratorclassString)
            throws MalformedURLException, InitialisationException {


        view = new ViewSubsystem();
        temps = new TemplateSubsystem();

        try {
            Class<?> pidgeneratorClass = Class.forName(pidgeneratorclassString);
            if (PidGenerator.class.isAssignableFrom(pidgeneratorClass)) {
                try {
                    pidGenerator = (PidGenerator) pidgeneratorClass.newInstance();
                } catch (InstantiationException e) {
                    throw new InitialisationException("Initialise failed", e);
                } catch (IllegalAccessException e) {
                    throw new InitialisationException("Initialise failed", e);
                }
            } else {//Class not implementing the correct interface
                throw new InitialisationException("Initialise failed");
            }
        } catch (ClassNotFoundException e) {
            throw new InitialisationException("Initialise failed", e);
        }

        if (initialised){
            return;
        }
        try {
            Class<?> fedoraconnectorclass = Class.forName(fedoraconnectorclassstring);
            if (FedoraConnector.class.isAssignableFrom(fedoraconnectorclass)) {
                try {
                    fedoraConnector = (FedoraConnector) fedoraconnectorclass.newInstance();
                    fedoraConnector = new CachingConnector(fedoraConnector);
                    FedoraUserToken token =
                            new FedoraUserToken(fedoraserverurl, creds.getUsername(),creds.getPassword());
                    fedoraConnector.initialise(token);
                } catch (InstantiationException e) {//TODO
                    throw new InitialisationException("Initialise failed", e);
                } catch (IllegalAccessException e) {//TODO
                    throw new InitialisationException("Initialise failed", e);
                }
            }
        } catch (ClassNotFoundException e) {//TODO
            throw new InitialisationException("Initialise failed", e);
        }
        initialised = true;
    }



    public String createNewObject(String templatePid, List<String> oldIdentifiers, String comment) throws
                                                                                                   BackendMethodFailedException,
                                                                                                   BackendInvalidCredsException,
                                                                                                   BackendInvalidResourceException {
        try {
            return temps.cloneTemplate(templatePid, oldIdentifiers, comment, fedoraConnector, pidGenerator);
        } catch (PIDGeneratorException e) {
            throw new BackendMethodFailedException("Server error", e);
        } catch (InvalidCredentialsException e) {
            throw new BackendInvalidCredsException("Invalid credentials",e);
        } catch (ObjectNotFoundException e) {
            throw new BackendInvalidResourceException("Object not found",e);
        } catch (FedoraIllegalContentException e) {
            throw new BackendMethodFailedException("Server error", e);
        } catch (ObjectIsWrongTypeException e) {
            throw new BackendMethodFailedException("Server error", e);
        } catch (FedoraConnectionException e) {
            throw new BackendMethodFailedException("Server error", e);
        }
    }

    public String createBundle(String pid, String angle)
            throws BackendMethodFailedException,
                   BackendInvalidCredsException,
                   BackendInvalidResourceException {
        try {

            //Return a string, as the two different return formats
            //confuse java
            Document dobundle = view.getViewObjectBundleForObject(
                    pid,
                    angle,
                    fedoraConnector);
            return DocumentUtils.documentToString(dobundle);
        } catch (InvalidCredentialsException e) {
            throw new BackendInvalidCredsException("Invalid credentials",e);
        } catch (ObjectNotFoundException e) {
            throw new BackendInvalidResourceException("Object not found",e);
        } catch (FedoraIllegalContentException e) {
            throw new BackendMethodFailedException("Server error", e);
        } catch (FedoraConnectionException e) {
            throw new BackendMethodFailedException("Server error", e);
        } catch (TransformerException e) {
            throw new BackendMethodFailedException("Server error", e);
        }

    }



}
