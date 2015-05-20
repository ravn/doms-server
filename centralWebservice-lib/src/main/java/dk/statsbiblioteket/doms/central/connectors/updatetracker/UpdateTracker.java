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

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.Connector;
import dk.statsbiblioteket.doms.updatetracker.improved.UpdateTrackerClient;
import dk.statsbiblioteket.doms.updatetracker.improved.webservice.UpdateTrackerTimerServlet;
import dk.statsbiblioteket.doms.updatetracker.webservice.InvalidCredentialsException;
import dk.statsbiblioteket.doms.updatetracker.webservice.MethodFailedException;
import dk.statsbiblioteket.doms.updatetracker.webservice.RecordDescription;
import dk.statsbiblioteket.sbutil.webservices.authentication.Credentials;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: abr Date: Sep 21, 2010 Time: 4:41:10 PM To change this template use File | Settings |
 * File Templates.
 */
public class UpdateTracker extends Connector {
    private UpdateTrackerClient service;

    public UpdateTracker(Credentials creds,
                         String location)
            throws
            MalformedURLException {
        super(creds, location);
        service = new UpdateTrackerClient(UpdateTrackerTimerServlet.updateTracker);
    }

    public List<UpdateTrackerRecord> listObjectsChangedSince(String collectionPid,
                                                             String viewAngle,
                                                             long date,
                                                             String state,
                                                             int offset,
                                                             int limit)
            throws
            BackendMethodFailedException,
            BackendInvalidCredsException {

        List<UpdateTrackerRecord> list = new ArrayList<UpdateTrackerRecord>();


        try {
            List<RecordDescription>
                    changed =
                    service.listObjectsChangedSince(collectionPid, viewAngle, date, state, offset, limit);
            for (RecordDescription recordDescription : changed) {
                UpdateTrackerRecord rec = new UpdateTrackerRecord();
                rec.setCollectionPid(recordDescription.getCollectionPid());
                rec.setEntryContentModelPid(recordDescription.getEntryCMPid());
                rec.setDate(new Date(recordDescription.getLastChangedTime()));
                rec.setRecordDate(new Date(recordDescription.getRecordTime()));
                rec.setPid(recordDescription.getPid());
                rec.setState(recordDescription.getState());
                rec.setViewAngle(viewAngle);
                list.add(rec);
            }
        } catch (InvalidCredentialsException e) {
            throw new BackendInvalidCredsException("Invalid credentials for update tracker", e);
        } catch (MethodFailedException e) {
            throw new BackendMethodFailedException("Update tracker failed", e);
        }

        return list;

    }

    public long getLatestModification(String collectionPid,
                                      String viewAngle,
                                      String state)
            throws
            BackendMethodFailedException,
            BackendInvalidCredsException {

        try {
            return service.getLatestModificationTime(collectionPid, viewAngle, state);
        } catch (InvalidCredentialsException e) {
            throw new BackendInvalidCredsException("Invalid credentials for update tracker", e);
        } catch (MethodFailedException e) {
            throw new BackendMethodFailedException("Update tracker failed", e);
        }
    }
}
