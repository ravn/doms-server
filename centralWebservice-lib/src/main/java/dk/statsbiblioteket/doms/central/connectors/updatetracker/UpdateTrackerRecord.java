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

import java.util.Date;

/**
 * A record from the update tracker.
 */
public class UpdateTrackerRecord {

    String collectionPid;

    /**
     * Do not use this field
     * @deprecated
     */
    @Deprecated()
    String entryContentModelPid;

    String pid;

    String viewAngle;

    Date date;

    String state;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCollectionPid() {
        return collectionPid;
    }

    public void setCollectionPid(String collectionPid) {
        this.collectionPid = collectionPid;
    }

    /**
     * Do not use this field
     *
     * @deprecated
     */
    @Deprecated()
    public String getEntryContentModelPid() {
        return entryContentModelPid;
    }

    /**
     * Do not use this field
     *
     * @deprecated
     */
    @Deprecated()
    public void setEntryContentModelPid(String entryContentModelPid) {
        this.entryContentModelPid = entryContentModelPid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getViewAngle() {
        return viewAngle;
    }

    public void setViewAngle(String viewAngle) {
        this.viewAngle = viewAngle;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
