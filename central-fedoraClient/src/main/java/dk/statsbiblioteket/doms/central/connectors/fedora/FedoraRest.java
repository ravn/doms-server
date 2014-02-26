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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.Connector;
import dk.statsbiblioteket.doms.central.connectors.fedora.generated.DatastreamType;
import dk.statsbiblioteket.doms.central.connectors.fedora.generated.ObjectDatastreams;
import dk.statsbiblioteket.doms.central.connectors.fedora.generated.ObjectFieldsType;
import dk.statsbiblioteket.doms.central.connectors.fedora.generated.ResultType;
import dk.statsbiblioteket.doms.central.connectors.fedora.generated.Validation;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.DatastreamProfile;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.FedoraRelation;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectProfile;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectType;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.SearchResult;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.Constants;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.DateUtils;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: abr Date: Aug 25, 2010 Time: 1:50:31 PM To change this template use File | Settings
 * |
 * File Templates.
 */
public class FedoraRest extends Connector implements Fedora {

    private static final String AS_OF_DATE_TIME = "asOfDateTime";
    private static Log log = LogFactory.getLog(FedoraRest.class);
    private WebResource restApi;
    private String port;


    public FedoraRest(Credentials creds, String location) throws MalformedURLException {
        super(creds, location);

        restApi = client.resource(location + "/objects");
        restApi.addFilter(new HTTPBasicAuthFilter(creds.getUsername(), creds.getPassword()));
        port = calculateFedoraPort(location);
    }

    private String calculateFedoraPort(String location) {
        String portString = location.substring(location.lastIndexOf(':') + 1);
        portString = portString.substring(0, portString.indexOf('/'));
        return portString;
    }

    @Override
    public boolean exists(String pid, Long asOfDateTime) throws
                                                         BackendInvalidCredsException,
                                                         BackendMethodFailedException {
        try {
            ObjectProfile profile = getObjectProfile(pid, asOfDateTime);
        } catch (BackendInvalidResourceException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isDataObject(String pid, Long asOfDateTime) throws
                                                               BackendInvalidCredsException,
                                                               BackendMethodFailedException {
        try {
            ObjectProfile profile = getObjectProfile(pid, asOfDateTime);
            return profile.getType().equals(ObjectType.DATA_OBJECT);
        } catch (BackendInvalidResourceException e) {
            return false;
        }
    }

    @Override
    public boolean isTemplate(String pid, Long asOfDateTime) throws
                                                             BackendInvalidCredsException,
                                                             BackendMethodFailedException {
        try {
            ObjectProfile profile = getObjectProfile(pid, asOfDateTime);
            return profile.getType().equals(ObjectType.TEMPLATE);
        } catch (BackendInvalidResourceException e) {
            return false;
        }

    }

    @Override
    public boolean isContentModel(String pid, Long asOfDateTime) throws
                                                                 BackendInvalidCredsException,
                                                                 BackendMethodFailedException {
        try {
            ObjectProfile profile = getObjectProfile(pid, asOfDateTime);
            return profile.getType().equals(ObjectType.CONTENT_MODEL);
        } catch (BackendInvalidResourceException e) {
            return false;
        }

    }

    @Override
    public String getObjectXml(String pid, Long asOfTime) throws
                                                          BackendMethodFailedException,
                                                          BackendInvalidCredsException,
                                                          BackendInvalidResourceException {

        try {
            //Get basic fedora profile

            //Get the object xml

            //Strip the old versions

            //Search for managed datastreams with format text/xml

            //retrieve and insert the content

            String xml = getRaxXml(pid);
            ObjectXml objectXml = new ObjectXml(pid,xml, this,asOfTime);

            return objectXml.getCleaned();
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource not found", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        } catch (TransformerException e) {
            throw new BackendMethodFailedException("Failed to transform object to output format", e);
        }
    }

    protected String getRaxXml(String pid) throws UnsupportedEncodingException {
        return restApi.path("/")
                                    .path(URLEncoder.encode(pid, "UTF-8"))
                                    .path("/objectXML")
                                    .type(MediaType.TEXT_XML_TYPE)
                                    .get(String.class);
    }

    private String StringOrNull(Long time) {
        if (time != null && time > 0) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return formatter.format(new Date(time));
        }
        return "";
    }

    @Override
    public String ingestDocument(Document document, String logmessage) throws
                                                                       BackendMethodFailedException,
                                                                       BackendInvalidCredsException {
        String payload;
        try {

            payload = DOM.domToString(document);
        } catch (TransformerException e) {
            throw new BackendMethodFailedException("Supplied document not valid", e);
        }
        try {

            String pid = restApi.path("/")
                                .path(URLEncoder.encode("new", "UTF-8"))
                                .type(MediaType.TEXT_XML_TYPE)
                                .post(String.class, payload);
            return pid;
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException(
                        "Invalid Credentials Supplied when ingesting document: \n'" + payload + "'", e);
            } else {
                throw new BackendMethodFailedException("Server error when ingesting document: \n'" + payload + "'", e);
            }
        }
    }

    @Override
    public ObjectProfile getObjectProfile(String pid, Long asOfTime) throws
                                                                     BackendMethodFailedException,
                                                                     BackendInvalidCredsException,
                                                                     BackendInvalidResourceException {
        try {
            //Get basic fedora profile
            dk.statsbiblioteket.doms.central.connectors.fedora.generated.ObjectProfile profile = restApi.path("/")
                                                                                                        .path(
                                                                                                                URLEncoder
                                                                                                                        .encode(
                                                                                                                                pid,
                                                                                                                                "UTF-8"))
                                                                                                        .queryParam(
                                                                                                                "format",
                                                                                                                "text/xml")
                                                                                                        .get(dk.statsbiblioteket.doms.central.connectors.fedora.generated.ObjectProfile.class);
            ObjectProfile prof = new ObjectProfile();
            prof.setObjectCreatedDate(profile.getObjCreateDate().toGregorianCalendar().getTime());
            prof.setObjectLastModifiedDate(profile.getObjLastModDate().toGregorianCalendar().getTime());
            prof.setLabel(profile.getObjLabel());
            prof.setOwnerID(profile.getObjOwnerId());
            prof.setState(profile.getObjState());
            prof.setPid(profile.getPid());
            List<String> contentmodels = new ArrayList<String>();
            for (String s : profile.getObjModels().getModel()) {
                if (s.startsWith("info:fedora/")) {
                    s = s.substring("info:fedora/".length());
                }
                contentmodels.add(s);
            }
            prof.setContentModels(contentmodels);

            //Get relations
            List<FedoraRelation> relations = getNamedRelations(pid, null, asOfTime);
            prof.setRelations(relations);

            //get Datastream list
            ObjectDatastreams datastreams = restApi.path("/")
                                                   .path(URLEncoder.encode(pid, "UTF-8"))
                                                   .path("/datastreams")
                                                   .queryParam("format", "text/xml")
                                                   .get(ObjectDatastreams.class);
            List<DatastreamProfile> pdatastreams = new ArrayList<DatastreamProfile>();
            for (DatastreamType datastreamType : datastreams.getDatastream()) {
                pdatastreams.add(getDatastreamProfile(pid, datastreamType.getDsid(), asOfTime));
            }
            prof.setDatastreams(pdatastreams);

            //decode type
            prof.setType(ObjectType.DATA_OBJECT);
            if (prof.getContentModels().contains("fedora-system:ContentModel-3.0")) {
                prof.setType(ObjectType.CONTENT_MODEL);
            }
            if (prof.getContentModels().contains("doms:ContentModel_File")) {
                prof.setType(ObjectType.FILE);
            }
            if (prof.getContentModels().contains("doms:ContentModel_Collection")) {
                prof.setType(ObjectType.COLLECTION);
            }

            for (FedoraRelation fedoraRelation : prof.getRelations()) {
                String predicate = fedoraRelation.getPredicate();
                if (Constants.TEMPLATE_REL.equals(predicate)) {
                    prof.setType(ObjectType.TEMPLATE);
                    break;
                }
            }


            return prof;


        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }

    }

    public DatastreamProfile getDatastreamProfile(String pid, String dsid, Long asOfTime) throws
                                                                                          BackendMethodFailedException,
                                                                                          BackendInvalidCredsException,
                                                                                          BackendInvalidResourceException {
        try {

            dk.statsbiblioteket.doms.central.connectors.fedora.generated.DatastreamProfile fdatastream = restApi.path(
                    "/")
                                                                                                                .path(
                                                                                                                        URLEncoder
                                                                                                                                .encode(
                                                                                                                                        pid,
                                                                                                                                        "UTF-8"))
                                                                                                                .path("/datastreams/")
                                                                                                                .path(dsid)
                                                                                                                .queryParam(
                                                                                                                        AS_OF_DATE_TIME,
                                                                                                                        StringOrNull(
                                                                                                                                asOfTime))
                                                                                                                .queryParam(
                                                                                                                        "format",
                                                                                                                        "text/xml")
                                                                                                                .get(dk.statsbiblioteket.doms.central.connectors.fedora.generated.DatastreamProfile.class);
            DatastreamProfile profile = new DatastreamProfile();
            profile.setID(fdatastream.getDsID());
            profile.setLabel(fdatastream.getDsLabel());
            profile.setState(fdatastream.getDsState());

            profile.setChecksum(fdatastream.getDsChecksum());
            profile.setChecksumType(fdatastream.getDsChecksumType());


            profile.setCreated(fdatastream.getDsCreateDate().toGregorianCalendar().getTime().getTime());
            profile.setFormatURI(fdatastream.getDsFormatURI());
            profile.setMimeType(fdatastream.getDsMIME());

            String type = fdatastream.getDsControlGroup();
            if (type.equals("X")) {
                profile.setInternal(true);
            } else if (type.equals("E") || type.equals("R")) {
                profile.setInternal(false);
                profile.setUrl(fdatastream.getDsLocation());
            }
            return profile;
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }

    }

    @Override
    public void modifyObjectState(String pid, String state, String comment) throws
                                                                            BackendMethodFailedException,
                                                                            BackendInvalidCredsException,
                                                                            BackendInvalidResourceException {
        try {
            if (comment == null || comment.isEmpty()) {
                comment = "No message supplied";
            }
            restApi.path("/")
                   .path(URLEncoder.encode(pid, "UTF-8"))
                   .queryParam("state", state)
                   .queryParam("logMessage", comment)
                   .put();
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }
    }

    @Override
    public void modifyDatastreamByValue(String pid, String datastream, ChecksumType checksumType, String checksum,
                                        byte[] contents, List<String> alternativeIdentifiers, String comment) throws
                                                                                                              BackendMethodFailedException,
                                                                                                              BackendInvalidCredsException,
                                                                                                              BackendInvalidResourceException {
        try {
            updateExistingDatastreamByValue(
                    pid, datastream, checksumType, checksum, contents, alternativeIdentifiers, comment, null, null);
        } catch (BackendInvalidResourceException e) {
            //perhaps the datastream did not exist
            createDatastreamByValue(pid, datastream, checksumType, checksum, contents, alternativeIdentifiers,null, comment);
        }
    }

    @Override
    public void modifyDatastreamByValue(String pid, String datastream, ChecksumType checksumType, String checksum,
                                        byte[] contents, List<String> alternativeIdentifiers, String comment,
                                        Long lastModifiedDate) throws
                                                               BackendMethodFailedException,
                                                               BackendInvalidCredsException,
                                                               BackendInvalidResourceException,
                                                               ConcurrentModificationException {
        try {
            updateExistingDatastreamByValue(
                    pid,
                    datastream,
                    checksumType,
                    checksum,
                    contents,
                    alternativeIdentifiers,
                    comment,
                    lastModifiedDate, null);
        } catch (BackendInvalidResourceException e) {
            //perhaps the datastream did not exist
            createDatastreamByValue(pid, datastream, checksumType, checksum, contents, alternativeIdentifiers, null,comment);
        }
    }

    @Override
    public void modifyDatastreamByValue(String pid, String datastream, ChecksumType checksumType, String checksum,
                                        byte[] contents, List<String> alternativeIdentifiers, String mimeType, String comment,
                                        Long lastModifiedDate) throws
            BackendMethodFailedException,
            BackendInvalidCredsException,
            BackendInvalidResourceException,
            ConcurrentModificationException {
        try {
            updateExistingDatastreamByValue(
                    pid,
                    datastream,
                    checksumType,
                    checksum,
                    contents,
                    alternativeIdentifiers,
                    comment,
                    lastModifiedDate, mimeType);
        } catch (BackendInvalidResourceException e) {
            //perhaps the datastream did not exist
            createDatastreamByValue(pid, datastream, checksumType, checksum, contents, alternativeIdentifiers,mimeType, comment);
        }
    }

    private void createDatastreamByValue(String pid, String datastream, ChecksumType checksumType, String checksum,
                                         byte[] contents, List<String> alternativeIdentifiers,String mimeType, String comment) throws
                                                                                                               BackendMethodFailedException,
                                                                                                               BackendInvalidCredsException,
                                                                                                               BackendInvalidResourceException {
        try {
            if (mimeType == null){
                mimeType = "text/xml";
            }
            WebResource resource = getModifyDatastreamWebResource(
                    pid, datastream, checksumType, checksum, alternativeIdentifiers, comment, null, mimeType);

            resource.queryParam("controlGroup", "M").post(new ByteArrayInputStream(contents));
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {

            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }


    }

    private WebResource getModifyDatastreamWebResource(String pid, String datastream, ChecksumType checksumType,
                                                       String checksum, List<String> alternativeIdentifiers,
                                                       String comment, Long lastModifiedDate, String mimeType) throws
                                                                                              UnsupportedEncodingException {
        if (comment == null || comment.isEmpty()) {
            comment = "No message supplied";
        }

        WebResource resource = restApi.path("/")
                                      .path(URLEncoder.encode(pid, "UTF-8"))
                                      .path("/datastreams/")
                                      .path(URLEncoder.encode(datastream, "UTF-8"))
                                      .queryParam("logMessage", comment);


        if (alternativeIdentifiers != null) {
            for (String alternativeIdentifier : alternativeIdentifiers) {
                resource = resource.queryParam("altIDs", alternativeIdentifier);
            }
        }
        if (checksumType != null) {
            resource = resource.queryParam("checksumType", checksumType.toString());

        }
        if (checksum != null) {
            resource = resource.queryParam("checksum", checksum);

        }
        if (lastModifiedDate != null) {
            resource = resource.queryParam("lastModifiedDate", StringOrNull(lastModifiedDate));
        }
        if (mimeType != null) {
            resource = resource.queryParam("mimeType", mimeType);
        } /*else {
            resource = resource.queryParam("mimeType", "text/xml");
        }*/
        return resource;
    }

    private void updateExistingDatastreamByValue(String pid, String datastream, ChecksumType checksumType,
                                                 String checksum, byte[] contents, List<String> alternativeIdentifiers,
                                                 String comment, Long lastModifiedDate, String mimeType) throws
                                                                                        BackendMethodFailedException,
                                                                                        BackendInvalidCredsException,
                                                                                        BackendInvalidResourceException,
                                                                                        ConcurrentModificationException {
        try {
            WebResource resource = getModifyDatastreamWebResource(
                    pid, datastream, checksumType, checksum, alternativeIdentifiers, comment, lastModifiedDate, mimeType);
            resource.header(HttpHeaders.CONTENT_TYPE, null).put(new ByteArrayInputStream(contents));
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.CONFLICT.getStatusCode()) {
                throw new ConcurrentModificationException("Datastream has changed between reading and writing.");
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }

    }

    @Override
    public void deleteObject(String pid, String comment) throws
                                                         BackendMethodFailedException,
                                                         BackendInvalidCredsException,
                                                         BackendInvalidResourceException {
        try {
            restApi.path("/").path(URLEncoder.encode(pid, "UTF-8")).queryParam("logMessage", comment).delete();
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }

    }

    @Override
    public void deleteDatastream(String pid, String datastream, String comment) throws
                                                                                BackendMethodFailedException,
                                                                                BackendInvalidCredsException,
                                                                                BackendInvalidResourceException {
        try {
            restApi.path("/")
                   .path(URLEncoder.encode(pid, "UTF-8"))
                   .path("/datastreams/")
                   .path(URLEncoder.encode(datastream, "UTF-8"))
                   .queryParam("logMessage", comment)
                   .delete();
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }
    }

    @Override
    public String getXMLDatastreamContents(String pid, String datastream, Long asOfTime) throws
                                                                                         BackendMethodFailedException,
                                                                                         BackendInvalidCredsException,
                                                                                         BackendInvalidResourceException {
        try {

            String contents = restApi.path("/")
                                     .path(URLEncoder.encode(pid, "UTF-8"))
                                     .path("/datastreams/")
                                     .path(URLEncoder.encode(datastream, "UTF-8"))
                                     .path("/content")
                                     .queryParam(AS_OF_DATE_TIME, StringOrNull(asOfTime))
                                     .get(String.class);
            return contents;
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }
    }

    @Override
    public void addRelation(String pid, String subject, String predicate, String object, boolean literal,
                            String comment) throws
                                            BackendMethodFailedException,
                                            BackendInvalidCredsException,
                                            BackendInvalidResourceException {
        try {
            if (comment == null || comment.isEmpty()) {
                comment = "No message supplied";
            }  //TODO, fedora should take this logmessage

            if (!literal) {
                if (!object.startsWith("info:fedora/")) {
                    object = "info:fedora/" + object;
                }
            }

            URI predURI = new URI(predicate);
            if (!predURI.isAbsolute()) {
                predicate = "info:fedora/" + predicate;
            }
            if (subject == null || subject.isEmpty()) {
                subject = "info:fedora/" + pid;
            }

            restApi.path("/")
                   .path(pid)
                   .path("/relationships/new")
                   .queryParam("subject", subject)
                   .queryParam("predicate", predicate)
                   .queryParam("object", object)
                   .queryParam("isLiteral", "" + literal)
                   .post();

            if (predicate.equals("http://doms.statsbiblioteket.dk/relations/default/0/1/#hasLicense")) {
                //this is a license relation, update the policy datastream
                if (object.startsWith("info:fedora/")) {
                    object = object.substring("info:fedora/".length());
                }
                restApi.path("/")
                       .path(URLEncoder.encode(pid, "UTF-8"))
                       .path("/datastreams/POLICY")
                       .queryParam(
                               "dsLocation", "http://localhost:" + port +
                                             "/fedora/objects/" + object + "/datastreams/LICENSE/content")
                       .queryParam("mimeType", "application/rdf+xml")
                       .queryParam("ignoreContent", "true")
                       .put();
            }
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }

        } catch (URISyntaxException e) {
            throw new BackendMethodFailedException("Failed to parse predicate as an URI", e);
        }
    }

    @Override
    public void addRelations(String pid, String subject, String predicate, List<String> objects, boolean literal,
                             String comment) throws
                                             BackendMethodFailedException,
                                             BackendInvalidCredsException,
                                             BackendInvalidResourceException {
        if (comment == null || comment.isEmpty()) {
            comment = "No message supplied";
        }
        if (literal) {//cant handle literal yet
            for (String object : objects) {
                addRelation(pid, subject, predicate, object, literal, comment);
            }
        }
        if (objects.size() == 1) {//more efficient if only adding one relation
            addRelation(pid, subject, predicate, objects.get(0), literal, comment);
        }


        try {
            XPathSelector xpath = DOM.createXPathSelector(
                    "rdf", Constants.NAMESPACE_RDF);


            String datastream;
            if (subject == null || subject.isEmpty() || subject.equals(pid) || subject.equals("info:fedora/" + pid)) {
                subject = "info:fedora/" + pid;
                datastream = "RELS-EXT";
            } else {
                datastream = "RELS-INT";
            }

            String rels = getXMLDatastreamContents(pid, datastream, null);
            Document relsDoc = DOM.stringToDOM(rels, true);


            Node rdfDescriptionNode = xpath.selectNode(
                    relsDoc, "/rdf:RDF/rdf:Description[@rdf:about='" + subject + "']");

            URI predURI = new URI(predicate);
            if (!predURI.isAbsolute()) {
                predicate = "info:fedora/" + predicate;
            }

            String[] splits = predicate.split("#");


            for (String object : objects) {
                if (!object.startsWith("info:fedora/")) {
                    object = "info:fedora/" + object;
                }

                Element relationsShipElement = relsDoc.createElementNS(splits[0] + "#", splits[1]);
                relationsShipElement.setAttributeNS(Constants.NAMESPACE_RDF, "rdf:resource", object);
                rdfDescriptionNode.appendChild(relationsShipElement);
            }

            modifyDatastreamByValue(
                    pid, datastream, ChecksumType.MD5, null, DOM.domToString(relsDoc).getBytes("UTF-8"), null, "application/rdf+xml", comment, null);
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("Failed to transform RELS-EXT", e);
        } catch (TransformerException e) {
            throw new BackendMethodFailedException("Failed to transform RELS-EXT", e);

        } catch (URISyntaxException e) {
            throw new BackendMethodFailedException("Failed to parse predicate as an URI", e);
        }

    }

    @Override
    public List<FedoraRelation> getNamedRelations(String pid, String name, Long asOfTime) throws
                                                                                          BackendMethodFailedException,
                                                                                          BackendInvalidCredsException,
                                                                                          BackendInvalidResourceException {
        try {
            //TODO use asOfTime here, when fedora supports it

            String subject = pid;
            if (!subject.startsWith("info:fedora/")) {
                subject = "info:fedora/" + subject;
            }
            WebResource temp = restApi.path("/")
                                      .path(pid)
                                      .path("/relationships/")
                                      .queryParam("subject", subject)
                                      .queryParam("format", "n-triples");
            if (name != null) {
                temp = temp.queryParam("predicate", name);
            }
            String relationString = temp.get(String.class);


            String[] lines = relationString.split("\n");
            List<FedoraRelation> relations = new ArrayList<FedoraRelation>();
            for (String line : lines) {
                String[] elements = line.split(" ");
                if (elements.length > 2) {
                    FedoraRelation rel = new FedoraRelation(
                            cleanInfo(elements[0]), clean(elements[1]), cleanInfo(elements[2]));
                    if (elements[2].startsWith("<info:fedora/")) {
                        rel.setLiteral(false);
                    } else {
                        rel.setLiteral(true);
                    }
                    relations.add(rel);
                }
            }
            return relations;
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }
    }

    private String clean(String element) {
        if (element.startsWith("<") && element.endsWith(">")) {
            element = element.substring(1, element.length() - 1);
        }
        return element;
        //To change body of created methods use File | Settings | File Templates.
    }

    private String cleanInfo(String element) {
        element = clean(element);

        if (element.startsWith("info:fedora/")) {
            element = element.substring("info:fedora/".length());
        }

        return element;
        //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public void deleteRelation(String pid, String subject, String predicate, String object, boolean literal,
                               String comment) throws
                                               BackendMethodFailedException,
                                               BackendInvalidCredsException,
                                               BackendInvalidResourceException {
        try {
            if (comment == null || comment.isEmpty()) {
                comment = "No message supplied";
            } //TODO, fedora should take this logmessage

            if (!literal) {
                if (!object.startsWith("info:fedora/")) {
                    object = "info:fedora/" + object;
                }
            }
            URI predURI = new URI(predicate);
            if (!predURI.isAbsolute()) {
                predicate = "info:fedora/" + predicate;
            }

            restApi.path("/")
                   .path(pid)
                   .path("/relationships/")
                   .queryParam("predicate", predicate)
                   .queryParam("object", object)
                   .queryParam("isLiteral", "" + literal)
                   .delete();

        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }

        } catch (URISyntaxException e) {
            throw new BackendMethodFailedException("Failed to parse predicate as an URI", e);
        }
    }

    @Override
    public void modifyObjectLabel(String pid, String name, String comment) throws
                                                                           BackendMethodFailedException,
                                                                           BackendInvalidCredsException,
                                                                           BackendInvalidResourceException {
        try {
            if (comment == null || comment.isEmpty()) {
                comment = "No message supplied";
            }

            restApi.path("/")
                   .path(URLEncoder.encode(pid, "UTF-8"))
                   .queryParam("label", name)
                   .queryParam("logMessage", comment)
                   .put();
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource not found", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        }
    }

    @Override
    public List<SearchResult> fieldsearch(String query, int offset, int pageLength) throws
                                                                                    BackendMethodFailedException,
                                                                                    BackendInvalidCredsException {
        try {

            ResultType searchResult = restApi.queryParam("terms", query)
                                             .queryParam("maxResults", pageLength + "")
                                             .queryParam("resultFormat", "xml")
                                             .queryParam("pid", "true")
                                             .queryParam("label", "true")
                                             .queryParam("state", "true")
                                             .queryParam("cDate", "true")
                                             .queryParam("mDate", "true")
                                             .get(ResultType.class);

            if (offset > 0) {

                for (int i = 1; i <= offset; i++) {
                    String token = searchResult.getListSession().getToken();
                    searchResult = restApi.queryParam("query", query)
                                          .queryParam("sessionToken", token)
                                          .queryParam("resultFormat", "xml")
                                          .get(ResultType.class);
                }
            }
            List<SearchResult> outputResults = new ArrayList<SearchResult>(
                    searchResult.getResultList().getObjectFields().size());
            for (ObjectFieldsType objectFieldsType : searchResult.getResultList().getObjectFields()) {

                outputResults.add(
                        new SearchResult(
                                objectFieldsType.getPid(),
                                objectFieldsType.getLabel(),
                                objectFieldsType.getState(),
                                DateUtils.parseDateStrict(objectFieldsType.getCDate()).getTime(),
                                DateUtils.parseDateStrict(objectFieldsType.getMDate()).getTime()));
            }
            return outputResults;

        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        } catch (ParseException e) {
            throw new BackendMethodFailedException("Failed to parse date from search result", e);
        }
    }

    @Override
    public void addExternalDatastream(String pid, String datastream, String label, String url, String formatURI,
                                      String mimeType, String checksumType, String checksum, String comment) throws
                                                                                                             BackendMethodFailedException,
                                                                                                             BackendInvalidCredsException,
                                                                                                             BackendInvalidResourceException {
        try {
            if (comment == null || comment.isEmpty()) {
                comment = "No message supplied";
            }

            /*
           @PathParam(RestParam.PID) String pid,
                                 @PathParam(RestParam.DSID) String dsID,
                                 @QueryParam(RestParam.CONTROL_GROUP) @DefaultValue("X") String controlGroup,
                                 @QueryParam(RestParam.DS_LOCATION) String dsLocation,
                                 @QueryParam(RestParam.ALT_IDS) List<String> altIDs,
                                 @QueryParam(RestParam.DS_LABEL) String dsLabel,
                                 @QueryParam(RestParam.VERSIONABLE) @DefaultValue("true") Boolean versionable,
                                 @QueryParam(RestParam.DS_STATE) @DefaultValue("A") String dsState,
                                 @QueryParam(RestParam.FORMAT_URI) String formatURI,
                                 @QueryParam(RestParam.CHECKSUM_TYPE) String checksumType,
                                 @QueryParam(RestParam.CHECKSUM) String checksum,
                                 @QueryParam(RestParam.MIME_TYPE) String mimeType,
                                 @QueryParam(RestParam.LOG_MESSAGE) String logMessage
            */
            WebResource resource = restApi.path("/")
                                          .path(URLEncoder.encode(pid, "UTF-8"))
                                          .path("/datastreams/")
                                          .path(URLEncoder.encode(datastream, "UTF-8"))
                                          .queryParam("controlGroup", "R")
                                          .queryParam("dsLocation", url)
                                          .queryParam("dsLabel", label)
                                          .queryParam("formatURI", formatURI)
                                          .queryParam("mimeType", mimeType)
                                          .queryParam("logMessage", comment);
            if (checksumType != null) {
                resource = resource.queryParam("checksumType", checksumType);
            }
            if (checksum != null) {
                resource = resource.queryParam("checksum", checksum);
            }
            resource.post();
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied: pid '" + pid + "'", e);
            } else if (e.getResponse().getStatus() == ClientResponse.Status.NOT_FOUND.getStatusCode()) {
                throw new BackendInvalidResourceException("Resource '" + pid + "'not found", e);
            } else {
                throw new BackendMethodFailedException("Server error for '" + pid + "'", e);
            }
        }

    }

    @Override
    public Validation validate(String pid) throws BackendMethodFailedException, BackendInvalidCredsException, BackendInvalidResourceException {
        try {
            WebResource format = restApi.path("/")
                    .path(
                            URLEncoder
                                    .encode(
                                            pid,
                                            "UTF-8"))
                    .path("/validate")
                    .queryParam(
                            "format",
                            "text/xml");
            return format
                    .get(Validation.class);
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        }
    }

    @Override
    public String newEmptyObject(String pid, List<String> oldIDs, List<String> collections, String logMessage) throws
                                                                                                               BackendMethodFailedException,
                                                                                                               BackendInvalidCredsException {

        try {

            InputStream emptyObjectStream = Thread.currentThread()
                                                  .getContextClassLoader()
                                                  .getResourceAsStream("EmptyObject.xml");
            Document emptyObject = DOM.streamToDOM(emptyObjectStream, true);


            XPathSelector xpath = DOM.createXPathSelector(
                    "foxml",
                    Constants.NAMESPACE_FOXML,
                    "rdf",
                    Constants.NAMESPACE_RDF,
                    "d",
                    Constants.NAMESPACE_RELATIONS,
                    "dc",
                    Constants.NAMESPACE_DC,
                    "oai_dc",
                    Constants.NAMESPACE_OAIDC);
            //Set pid
            Node pidNode = xpath.selectNode(emptyObject, "/foxml:digitalObject/@PID");
            pidNode.setNodeValue(pid);

            Node rdfNode = xpath.selectNode(
                    emptyObject,
                    "/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion/foxml:xmlContent/rdf:RDF/rdf:Description/@rdf:about");
            rdfNode.setNodeValue("info:fedora/" + pid);

            //add Old Identifiers to DC
            Node dcIdentifierNode = xpath.selectNode(
                    emptyObject,
                    "/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion/foxml:xmlContent/oai_dc:dc/dc:identifier");
            dcIdentifierNode.setTextContent(pid);
            Node parent = dcIdentifierNode.getParentNode();
            for (String oldID : oldIDs) {
                Node clone = dcIdentifierNode.cloneNode(true);
                clone.setTextContent(oldID);
                parent.appendChild(clone);
            }

            Node collectionRelationNode = xpath.selectNode(
                    emptyObject,
                    "/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion/foxml:xmlContent/rdf:RDF/rdf:Description/d:isPartOfCollection");

            parent = collectionRelationNode.getParentNode();
            //remove the placeholder relationNode
            parent.removeChild(collectionRelationNode);

            for (String collection : collections) {
                Node clone = collectionRelationNode.cloneNode(true);
                clone.getAttributes().getNamedItem("rdf:resource").setNodeValue("info:fedora/" + collection);
                parent.appendChild(clone);
            }

            String createdPid = restApi.path("/")
                                       .path(URLEncoder.encode(pid, "UTF-8"))
                                       .queryParam("state", "I")
                                       .type(MediaType.TEXT_XML_TYPE)
                                       .post(String.class, DOM.domToString(emptyObject));
            return createdPid;
        } catch (UnsupportedEncodingException e) {
            throw new BackendMethodFailedException("UTF-8 not known....", e);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == ClientResponse.Status.UNAUTHORIZED.getStatusCode()) {
                throw new BackendInvalidCredsException("Invalid Credentials Supplied", e);
            } else {
                throw new BackendMethodFailedException("Server error", e);
            }
        } catch (TransformerException e) {
            throw new BackendMethodFailedException("Failed to convert DC back to string", e);
        }
    }
}