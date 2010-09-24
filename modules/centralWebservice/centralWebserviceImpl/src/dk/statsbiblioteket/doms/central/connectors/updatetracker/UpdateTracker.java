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

package dk.statsbiblioteket.doms.central.connectors.updatetracker;

import dk.statsbiblioteket.doms.central.connectors.Connector;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.webservices.Credentials;
import dk.statsbiblioteket.doms.bitstorage.highlevel.HighlevelBitstorageSoapWebserviceService;
import dk.statsbiblioteket.doms.bitstorage.highlevel.HighlevelBitstorageSoapWebservice;
import dk.statsbiblioteket.doms.updatetracker.webservice.*;

import javax.xml.ws.BindingProvider;
import javax.xml.namespace.QName;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.String;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Sep 21, 2010
 * Time: 4:41:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateTracker extends Connector{
    private UpdateTrackerWebservice service;
    private QName QNAME = new QName("http://updatetracker.doms.statsbiblioteket.dk/", "UpdateTrackerWebserviceService");


    public UpdateTracker(Credentials creds, String location)
            throws MalformedURLException {
        super(creds, location);
        URL wsdlLocation = new URL(location);
        service = new UpdateTrackerWebserviceService(wsdlLocation,
                                                     QNAME).getUpdateTrackerWebservicePort();
        ((BindingProvider)service).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,creds.getUsername());
        ((BindingProvider)service).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,creds.getPassword());


    }

    public List<UpdateTrackerRecord> listObjectsChangedSince(String collectionPid,
                                                             String entryContentModel,
                                                             String viewAngle,
                                                             long date)
            throws
            BackendMethodFailedException,
            BackendInvalidCredsException {

        List<UpdateTrackerRecord> list
                = new ArrayList<UpdateTrackerRecord>();

        try {
            List<PidDatePidPid> changed = service.listObjectsChangedSince(
                    collectionPid,
                    entryContentModel,
                    long2Gregorian(date));
            for (PidDatePidPid pidDatePidPid : changed) {
                UpdateTrackerRecord rec = new UpdateTrackerRecord();
                rec.setCollectionPid(pidDatePidPid.getCollectionPid());
                rec.setEntryContentModelPid(pidDatePidPid.getEntryCMPid());
                rec.setDate(pidDatePidPid.getLastChangedTime().toGregorianCalendar().getTime());
                list.add(rec);
            }
        } catch (InvalidCredentialsException e) {
            throw new BackendInvalidCredsException("Invalid credentials for update tracker",e);
        } catch (MethodFailedException e) {
            throw new BackendMethodFailedException("Update tracker failed",e);
        }

        return list;

    }

    public long getLatestModification(String collectionPid,
                                      String entryContentModel,
                                      String viewAngle)
            throws
            BackendMethodFailedException,
            BackendInvalidCredsException,
            BackendInvalidResourceException {
        return 0;
    }

    public static XMLGregorianCalendar long2Gregorian(long date)
            throws BackendMethodFailedException {
        DatatypeFactory dataTypeFactory;
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new BackendMethodFailedException("Failed to convert dates...",e);
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date);
        return dataTypeFactory.newXMLGregorianCalendar(gc);
    }

}
