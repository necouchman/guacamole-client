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
package org.apache.guacamole.auth.sync.user;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.sync.connection.ActiveConnectionSyncDirectory;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a UserContext that delegates all functions to another
 * context, except the ActiveConnectionDirectory, which is stored in a
 * Redis server.
 */
public class ActiveConnectionSyncUserContext extends DelegatingUserContext {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveConnectionSyncUserContext.class);
    
    private final ActiveConnectionSyncDirectory activeConnectionDirectory;
    
    /**
     * Create a new UserContext that delegates functions to an underlying
     * context.
     * 
     * @param context 
     *     The context to delegate call to, unless otherwise overridden.
     * 
     * @throws GuacamoleException
     */
    public ActiveConnectionSyncUserContext(UserContext context) throws GuacamoleException {
        super(context);
        this.activeConnectionDirectory = new ActiveConnectionSyncDirectory(super.getActiveConnectionDirectory());
    }
    
    @Override
    public Directory<ActiveConnection> getActiveConnectionDirectory()
            throws GuacamoleException {
        return activeConnectionDirectory;
    }

    public void shutdown() {
        activeConnectionDirectory.shutdown();
    }
    
}
