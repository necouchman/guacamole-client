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

package org.apache.guacamole.auth.active.user;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.active.connection.ExtendedActiveConnectionDirectory;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.UserContext;

/**
 * An UserContext that delegates most functionality to another context, providing
 * only some additional ActiveConnection support.
 */
public class ActiveConnectionUserContext extends DelegatingUserContext {
    
    private final ExtendedActiveConnectionDirectory activeConnectionDirectory;
    
    public ActiveConnectionUserContext(UserContext userContext)
            throws GuacamoleException {
        super(userContext);
        this.activeConnectionDirectory =
                new ExtendedActiveConnectionDirectory(super.getActiveConnectionDirectory());
        this.activeConnectionDirectory.getRootConnectionGroup().getConnectionGroupIdentifiers().add(this.activeConnectionDirectory.getRootConnectionGroup().getIdentifier());
    }
    
    @Override
    public Directory<ActiveConnection> getActiveConnectionDirectory()
            throws GuacamoleException {
        
        return activeConnectionDirectory;
        
    }
    
}
