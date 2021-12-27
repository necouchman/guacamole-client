/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.guacamole.auth.sync.connection;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.credentials.UserCredentials;
import org.apache.guacamole.protocol.GuacamoleClientInformation;

/**
 *
 * @author nick
 */
public class SynchronizedActiveConnection implements Serializable, ActiveConnection {

    private static final long serialVersionUID = 1L;

    private final String connectionIdentifier;
    
    private final String sharingProfileIdentifier;
    
    private final Date startDate;
    
    private final String remoteHost;
    
    private final String username;
    
    private final transient ActiveConnection activeConnection;
    
    private final boolean isConnectable;
    
    private final String identifier;
    
    public SynchronizedActiveConnection(ActiveConnection activeConnection) {
        this.activeConnection = activeConnection;
        this.username = activeConnection.getUsername();
        this.remoteHost = activeConnection.getRemoteHost();
        this.connectionIdentifier = activeConnection.getConnectionIdentifier();
        this.startDate = activeConnection.getStartDate();
        this.sharingProfileIdentifier = activeConnection.getSharingProfileIdentifier();
        this.isConnectable = activeConnection.isConnectable();
        this.identifier = activeConnection.getIdentifier();
    }
    
    @Override
    public String getConnectionIdentifier() {
        return connectionIdentifier;
    }

    @Override
    public void setConnectionIdentifier(String connnectionIdentifier) {
        activeConnection.setConnectionIdentifier(connectionIdentifier);
    }

    @Override
    public String getSharingProfileIdentifier() {
        return sharingProfileIdentifier;
    }

    @Override
    public void setSharingProfileIdentifier(String sharingProfileIdentifier) {
        activeConnection.setSharingProfileIdentifier(sharingProfileIdentifier);
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        activeConnection.setStartDate(startDate);
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public void setRemoteHost(String remoteHost) {
        activeConnection.setRemoteHost(remoteHost);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        activeConnection.setUsername(username);
    }

    @Override
    public GuacamoleTunnel getTunnel() {
        return activeConnection.getTunnel();
    }

    @Override
    public void setTunnel(GuacamoleTunnel tunnel) {
        activeConnection.setTunnel(tunnel);
    }

    @Override
    public boolean isConnectable() {
        return isConnectable;
    }

    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info, Map<String, String> tokens) throws GuacamoleException {
        return activeConnection.connect(info, tokens);
    }

    @Override
    public int getActiveConnections() {
        return activeConnection.getActiveConnections();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        activeConnection.setIdentifier(identifier);
    }
    
    @Override
    public UserCredentials getSharingCredentials(String identifier) throws GuacamoleException {
        return activeConnection.getSharingCredentials(identifier);
    }
    
    public ActiveConnection getUndecorated() {
        return activeConnection;
    }
    
}
