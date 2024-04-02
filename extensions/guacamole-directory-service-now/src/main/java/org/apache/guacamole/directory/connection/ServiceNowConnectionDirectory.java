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

package org.apache.guacamole.directory.connection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DelegatingDirectory;
import org.apache.guacamole.net.auth.Directory;

/**
 * A connection directory that collects Service Now connections.
 */
public class ServiceNowConnectionDirectory extends DelegatingDirectory<Connection> {
    
    /**
     * The default root connection group identifier.
     */
    public static final String DEFAULT_ROOT_CONNECTION_GROUP = "root";
    
    /**
     * The connections to store for Service Now.
     */
    private final Map<String, Connection> serviceNowConnections =
            new ConcurrentHashMap<>();
    
    /**
     * 
     * @param directory
     */
    public ServiceNowConnectionDirectory(Directory<Connection> directory) {
        
        super(directory);
        
    }
    
    /**
     * Add a new, in-memory-only, Service Now connection. This avoids adding
     * anything more permanent to the underlying directory to which other
     * functionality is delegated.
     * 
     * @param connection 
     *     The connection to add to the in-memory connection store.
     */
    public void addServiceNow(Connection connection) {
        serviceNowConnections.put(connection.getIdentifier(), connection);
    }
    
    @Override
    public Set<String> getIdentifiers() throws GuacamoleException {
        
        // Make a copy of the delegated identifiers.
        Set<String> allIdentifiers = new HashSet<>(super.getIdentifiers());
        
        // Add our local identifiers.
        allIdentifiers.addAll(serviceNowConnections.keySet());
        
        // Return them all.
        return allIdentifiers;
    }
    
    @Override
    public Collection<Connection> getAll(Collection<String> identifiers) 
            throws GuacamoleException {
        
        // First, get the delegated connections.
        Collection<Connection> allConnections = new HashSet<>(super.getAll(identifiers));
        
        // Next, look the identifiers and see if we have any local ones.
        for (String identifier : identifiers) {
            if (serviceNowConnections.containsKey(identifier))
                allConnections.add(serviceNowConnections.get(identifier));
        }
        
        // Return the full set.
        return allConnections;
    }
    
    @Override
    public Connection get(String identifier) throws GuacamoleException {
        
        // Check local connections, first.
        if (serviceNowConnections.containsKey(identifier))
            return serviceNowConnections.get(identifier);
        
        // Else, check upstream
        return super.get(identifier);
        
    }
    
}
