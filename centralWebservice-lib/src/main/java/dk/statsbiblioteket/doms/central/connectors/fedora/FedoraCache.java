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

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;
import dk.statsbiblioteket.util.caching.TimeSensitiveCache;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 25, 2010
 * Time: 11:52:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class FedoraCache implements Fedora {


    private final Fedora fedora;

    /**
     * This is the blob of user specific caches. Note that this is itself a cache
     * so it will be garbage collected
     */
    private static TimeSensitiveCache<Credentials, Caches> userspecificCaches;

    private Caches myCaches;


    public FedoraCache(Credentials creds, Fedora fedora)
            throws MalformedURLException {
        synchronized (FedoraCache.class) {
            if (userspecificCaches == null) {
                String lifetime = ConfigCollection.getProperties().getProperty(
                        "dk.statsbiblioteket.doms.central.connectors.fedora.usercache.lifetime",
                        "" + 1000 * 60 * 10);
                String size = ConfigCollection.getProperties().getProperty(
                        "dk.statsbiblioteket.doms.central.connectors.fedora.usercache.size",
                        "" + 20);
                userspecificCaches = new TimeSensitiveCache<Credentials, Caches>(
                        Long.parseLong(lifetime),
                        true,
                        Integer.parseInt(size));
            }

            this.fedora = fedora;
            myCaches = userspecificCaches.get(creds);
            if (myCaches == null) {
                myCaches = new Caches();
                userspecificCaches.put(creds, myCaches);
            }

        }

    }

    @Override
    public ObjectProfile getObjectProfile(String pid)
            throws BackendMethodFailedException, BackendInvalidCredsException, BackendInvalidResourceException {
        return fedora.getObjectProfile(pid);
    }

    public void modifyObjectState(String pid, String state, String comment) throws
                                                                            BackendMethodFailedException,
                                                                            BackendInvalidCredsException,
                                                                            BackendInvalidResourceException {
        fedora.modifyObjectState(pid, state, comment);
    }

    public void modifyDatastreamByValue(String pid,
                                        String datastream,
                                        String contents, String comment) throws
                                                                         BackendMethodFailedException,
                                                                         BackendInvalidCredsException,
                                                                         BackendInvalidResourceException {
        fedora.modifyDatastreamByValue(pid, datastream, contents, comment);
    }

    public String getXMLDatastreamContents(String pid, String datastream) throws
                                                                          BackendMethodFailedException,
                                                                          BackendInvalidCredsException,
                                                                          BackendInvalidResourceException {
        String content = myCaches.getDatastreamContents(pid, datastream);
        if (content == null) {
            content = fedora.getXMLDatastreamContents(pid, datastream);
            myCaches.putDatastreamContents(pid, datastream, content);
        }
        return content;
    }

    public void addRelation(String pid,
                            String subject,
                            String property,
                            String object, boolean literal, String comment) throws
                                                           BackendMethodFailedException,
                                                           BackendInvalidCredsException,
                                                           BackendInvalidResourceException {
        fedora.addRelation(pid, subject, property, object, literal, comment);
    }

    @Override
    public List<FedoraRelation> getNamedRelations(String pid, String name)
            throws BackendMethodFailedException, BackendInvalidCredsException, BackendInvalidResourceException {
        return fedora.getNamedRelations(pid, name);
    }

    @Override
    public void deleteRelation(String pid, String subject, String predicate, String object, boolean literal,
                               String comment)
            throws BackendMethodFailedException, BackendInvalidCredsException, BackendInvalidResourceException {
        fedora.deleteRelation(pid, subject, predicate, object, literal, comment);
    }

    @Override
    public List<String> getObjectsInCollection(String collectionPid, String contentModel)
            throws BackendInvalidCredsException, BackendMethodFailedException{
        return fedora.getObjectsInCollection(collectionPid,contentModel);
    }

    public List<String> listObjectsWithThisLabel(String label) throws
                                                               BackendInvalidCredsException,
                                                               BackendMethodFailedException
                                                               {
        return fedora.listObjectsWithThisLabel(label);
    }

    public void modifyObjectLabel(String pid, String name, String comment) throws
                                                                           BackendMethodFailedException,
                                                                           BackendInvalidCredsException,
                                                                           BackendInvalidResourceException {
        fedora.modifyObjectLabel(pid, name, comment);
    }

    @Override
    public List<String> findObjectFromDCIdentifier(String string)
            throws BackendInvalidCredsException, BackendMethodFailedException{
        return fedora.findObjectFromDCIdentifier(string);
    }

    @Override
    public void flushTripples() throws BackendInvalidCredsException, BackendMethodFailedException {
        fedora.flushTripples();
    }

    @Override
    public List<SearchResult> fieldsearch(String query, int offset, int pageLength)
            throws BackendInvalidCredsException, BackendMethodFailedException{
        return fedora.fieldsearch(query,offset,pageLength);
    }

    @Override
    public List<FedoraRelation> getInverseRelations(String pid, String predicate)
            throws BackendMethodFailedException, BackendInvalidCredsException, BackendInvalidResourceException {
        return fedora.getInverseRelations(pid,predicate);
    }
}
