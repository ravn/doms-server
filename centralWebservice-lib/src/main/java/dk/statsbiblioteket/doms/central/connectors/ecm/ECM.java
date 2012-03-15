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
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;

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
public class ECM extends Connector {

    private WebResource restApi;

    public ECM(Credentials creds, String ecmLocation)
            throws MalformedURLException {
        super(creds, ecmLocation);
        restApi = client.resource(location);
        restApi.addFilter(new HTTPBasicAuthFilter(creds.getUsername(),creds.getPassword()));
    }

    public String createNewObject(String templatePid, List<String> oldIdentifiers, String comment) throws
                                                                                                   BackendMethodFailedException,
                                                                                                   BackendInvalidCredsException,
                                                                                                   BackendInvalidResourceException {
        try {
            WebResource temp = restApi
                    .path("/clone/")
                    .queryParam("logMessage", comment)
                    .path(URLEncoder.encode(templatePid, "UTF-8"));
            if (oldIdentifiers != null) {
                for (String oldIdentifier : oldIdentifiers) {
                    temp = temp.queryParam("oldID", oldIdentifier);
                }
            }
            String clonePID = temp
                    .post(String.class);
            return clonePID;
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getClientResponseStatus()
                    .equals(ClientResponse.Status.UNAUTHORIZED)) {
                throw new BackendInvalidCredsException(
                        "Invalid Credentials Supplied", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        }
    }

    public String createBundle(String pid, String angle)
            throws BackendMethodFailedException,
                   BackendInvalidCredsException,
                   BackendInvalidResourceException {
        try {
            String bundle = restApi
                    .path("getViewObjectsForObject/")
                    .path(URLEncoder.encode(pid, "UTF-8"))
                    .path("/forAngle/")
                    .path(angle)
                    .queryParam("bundle", "true")
                    .get(String.class);
            return bundle;
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getClientResponseStatus()
                    .equals(ClientResponse.Status.UNAUTHORIZED)) {
                throw new BackendInvalidCredsException(
                        "Invalid Credentials Supplied", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        }

    }

    public List<String> getEntryContentModelsForObject(String pid, String angle)
            throws BackendInvalidCredsException,
                   BackendMethodFailedException,
                   BackendInvalidResourceException {
        try {
            PidList list = restApi
                    .path("getEntryContentModelsForObject/")
                    .path(URLEncoder.encode(pid, "UTF-8"))
                    .path("/forAngle/")
                    .path(angle)
                    .get(PidList.class);
            return list;

        } catch (UniformInterfaceException e) {
            if (e.getResponse().getClientResponseStatus()
                    .equals(ClientResponse.Status.UNAUTHORIZED)) {
                throw new BackendInvalidCredsException(
                        "Invalid Credentials Supplied", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        }


    }

}
