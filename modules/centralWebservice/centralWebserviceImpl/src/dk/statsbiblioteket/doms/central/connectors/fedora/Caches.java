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

package dk.statsbiblioteket.doms.central.connectors.fedora;

import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import dk.statsbiblioteket.util.caching.TimeSensitiveCache;


/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 25, 2010
 * Time: 11:53:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class Caches {




    private TimeSensitiveCache<String, String> datastreamContents;

    public Caches() {
        String timeToLive = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.connectors.fedora.caches.datastreamContents.lifetime",
                "" + 600000);

        String size = ConfigCollection.getProperties().getProperty(
                "dk.statsbiblioteket.doms.central.connectors.fedora.caches.datastreamContents.size",
                "" + 25);
        datastreamContents = new TimeSensitiveCache<String, String>(
                Long.parseLong(timeToLive),
                true,
                Integer.parseInt(size));
    }

    private String mergeStrings(String... strings) {
        String result = "";
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (i == 0) {
                result = string;
            } else {
                result = result + "/" + string;
            }

        }
        return result;
    }


    private boolean pidProtection(String pid) {
        pid = pid.replaceAll("info:fedora/", "");
        if (pid.startsWith("doms:")) {
            return true;
        }
        return false;
    }

    public String getDatastreamContents(String pid, String datastream) {
        if (pidProtection(pid)){
            return datastreamContents.get(mergeStrings(pid,datastream));
        }
        return null;
    }

    public void putDatastreamContents(String pid, String datastream, String value) {
        if (pidProtection(pid)){
            datastreamContents.put(mergeStrings(pid,datastream), value);
        }
    }

    public void removeDatastreamContents(String pid, String datastream) {
        datastreamContents.remove(mergeStrings(pid,datastream));
    }
}
