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
package org.apache.guacamole.directory.multiuser.connection;

import java.util.Collection;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.DelegatingDirectory;
import org.apache.guacamole.net.auth.Directory;

/**
 * An directory that delegates much of the functionality to an underlying
 * directory, providing some convenience mechanisms for retrieving active
 * connections to enable multi-user connections.
 */
public class MultiUserActiveConnectionDirectory 
        extends DelegatingDirectory<ActiveConnection> {
    
    /**
     * Create a new multi-user active connection directory, wrapping the provided
     * active connection directory, delegating functionality to the wrapped
     * directory and providing additional functionality for multi-user
     * connections.
     * 
     * @param activeConnectionDirectory 
     *     The active connection directory to wrap.
     */
    public MultiUserActiveConnectionDirectory(Directory<ActiveConnection> activeConnectionDirectory) {
        super(activeConnectionDirectory);
    }
    
    /**
     * Return true if the identifier provided exists in the connection directory,
     * otherwise false.
     * 
     * @param identifier
     *     The identifier to look for in the connection directory.
     * 
     * @return 
     *     True if the provided identifier exists in the directory, otherwise
     *     false.
     * 
     * @throws GuacamoleException
     *     If an error occurs retrieving the object, or if the user does not
     *     have permissions to the object.
     */
    public boolean hasConnection(String identifier) throws GuacamoleException {
        Collection<ActiveConnection> activeConnections = getAll(getIdentifiers());
        for (ActiveConnection activeConnection : activeConnections) {
            if (activeConnection.getConnectionIdentifier().equals(identifier))
                return true;
        }
        return false;
    }
    
    /**
     * Return the identifier of the active connection that matches the provided
     * connection identifier, if one exists, otherwise return null.
     * 
     * @param identifier
     *     The identifier of the connection to retrieve.
     * 
     * @return 
     *     The active connection identifier matching the connection identifier,
     *     or null if none exists.
     * 
     * @throws GuacamoleException
     *     If an error occurs retrieving the object, or if the user does not
     *     have permissions to the object.
     */
    public String getActiveConnectionIdentifier(String identifier) throws GuacamoleException {
        Collection<ActiveConnection> activeConnections = getAll(getIdentifiers());
        for (ActiveConnection activeConnection : activeConnections) {
            if (activeConnection.getConnectionIdentifier().equals(identifier))
                return activeConnection.getIdentifier();
        }
        return null;
    }
    
    /**
     * Return the ActiveConnection for the given connection identifier, or null
     * if no such active connection exists.
     * 
     * @param identifier
     *     The Connection identifier for which to retrieve the ActiveConnection
     *     object.
     * 
     * @return
     *     The ActiveConnection object that is associated with the given
     *     Connection identifier.
     * 
     * @throws GuacamoleException
     *     If an error occurs retrieving the object, or if the user does not
     *     have permissions to the object.
     */
    public ActiveConnection getActiveConnection(String identifier) throws GuacamoleException {
        Collection<ActiveConnection> activeConnections = getAll(getIdentifiers());
        for (ActiveConnection activeConnection : activeConnections) {
            if (activeConnection.getConnectionIdentifier().equals(identifier))
                return activeConnection;
        }
        return null;
    }
    
}
