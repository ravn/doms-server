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

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Sep 21, 2010
 * Time: 4:41:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateTracker extends Connector{


    public UpdateTracker(Credentials creds, String location)
            throws MalformedURLException {
        super(creds, location);
    }

    public List<UpdateTrackerRecord> listObjectsChangedSince(String collectionPid,
                                                             String entryContentModel,
                                                             String viewAngle,
                                                             long date)
            throws
            BackendMethodFailedException,
            BackendInvalidCredsException,
            BackendInvalidResourceException {
        List<UpdateTrackerRecord> list
                = new ArrayList<UpdateTrackerRecord>();
        UpdateTrackerRecord rec = new UpdateTrackerRecord();
        rec.setCollectionPid("doms:RadioTV_Collection");
        rec.setEntryContentModelPid("doms:ContentModel_Program");
        rec.setViewAngle("SummaVisible");
        rec.setDate(new Date(0));
        list.add(rec);
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

}
