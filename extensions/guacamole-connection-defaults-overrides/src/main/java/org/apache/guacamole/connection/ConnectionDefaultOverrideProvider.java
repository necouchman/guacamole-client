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

package org.apache.guacamole.connection;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.connection.user.ConnectionDefaultOverrideUserContext;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;

/**
 * An AuthenticaitonProvider implementation that reads default and/or override
 * settings from a configuration file and injects those settings in to connection
 * configurations from other authentication extensions.
 */
public class ConnectionDefaultOverrideProvider extends AbstractAuthenticationProvider {
    
    @Override
    public String getIdentifier() {
        return "connection-defaults-overrides";
    }
    
    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        return new ConnectionDefaultOverrideUserContext(context.self(), context);
    }
    
}
