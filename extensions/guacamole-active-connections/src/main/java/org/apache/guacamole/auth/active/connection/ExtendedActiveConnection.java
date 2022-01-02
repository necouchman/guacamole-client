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

package org.apache.guacamole.auth.active.connection;

import java.util.Date;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.credentials.UserCredentials;

/**
 * An ActiveConnection implementation that extends the functionality,
 * implementing some additional ActiveConnection support.
 */
public class ExtendedActiveConnection implements ActiveConnection {
    
    private final ActiveConnection undecorated;
    
    public ExtendedActiveConnection(ActiveConnection undecorated) {
        this.undecorated = undecorated;
    }
    
    public ActiveConnection getUndecorated() {
        return undecorated;
    }

    @Override
    public String getConnectionIdentifier() {
        return undecorated.getConnectionIdentifier();
    }

    @Override
    public void setConnectionIdentifier(String connectionIdentifier) {
        undecorated.setConnectionIdentifier(connectionIdentifier);
    }

    @Override
    public String getSharingProfileIdentifier() {
        return undecorated.getSharingProfileIdentifier();
    }

    @Override
    public void setSharingProfileIdentifier(String sharingProfileIdentifier) {
        undecorated.setSharingProfileIdentifier(sharingProfileIdentifier);
    }

    @Override
    public Date getStartDate() {
        return undecorated.getStartDate();
    }

    @Override
    public void setStartDate(Date startDate) {
        undecorated.setStartDate(startDate);
    }

    @Override
    public String getRemoteHost() {
        return undecorated.getRemoteHost();
    }

    @Override
    public void setRemoteHost(String remoteHost) {
        undecorated.setRemoteHost(remoteHost);
    }

    @Override
    public String getUsername() {
        return undecorated.getUsername();
    }

    @Override
    public void setUsername(String username) {
        undecorated.setUsername(username);
    }

    @Override
    public GuacamoleTunnel getTunnel() {
        return undecorated.getTunnel();
    }

    @Override
    public void setTunnel(GuacamoleTunnel tunnel) {
        undecorated.setTunnel(tunnel);
    }

    @Override
    public boolean isConnectable() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return undecorated.getIdentifier();
    }

    @Override
    public void setIdentifier(String identifier) {
        undecorated.setIdentifier(identifier);
    }

    @Override
    public UserCredentials getSharingCredentials(String identifier) throws GuacamoleException {
        return undecorated.getSharingCredentials(identifier);
    }
    
}
