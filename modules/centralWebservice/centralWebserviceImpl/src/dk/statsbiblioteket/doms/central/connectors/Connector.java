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


package dk.statsbiblioteket.doms.central.connectors;

import dk.statsbiblioteket.doms.webservices.Base64;
import dk.statsbiblioteket.doms.webservices.Credentials;

import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 27, 2010
 * Time: 1:07:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Connector {

    private Credentials creds;
    protected String location;

    protected Connector(Credentials creds, String location) throws
                                                                           MalformedURLException {
        this.creds = creds;

        this.location = location;
    }

    protected String credsAsBase64(){
        String preBase64 = creds.getUsername() + ":" + creds.getPassword();
        String base64 = Base64.encodeBytes(preBase64.getBytes());
        return "Basic "+base64;
    }



}
