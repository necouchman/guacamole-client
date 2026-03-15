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

package org.apache.guacamole.directory.multiuser;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.directory.multiuser.user.MultiUserContext;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AuthenticationProvider implementation which provides the ability to connect
 * to existing active connections and configure active connection behavior.
 */
public class MultiUserConnectionDirectoryProvider extends AbstractAuthenticationProvider {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiUserConnectionDirectoryProvider.class);
    
    /**
     * Injector which will manage the object graph of this authentication
     * provider.
     */
    private final Injector injector;
    
    /**
     * Create a new instance of the Multi-User Connection directory provider,
     * setting up the Guice injector for dependency management.
     * 
     * @throws GuacamoleException
     *     If an error occurs configuring the Guice injector.
     */
    public MultiUserConnectionDirectoryProvider() throws GuacamoleException {

        // Set up Guice injector.
        injector = Guice.createInjector(
            new MultiUserConnectionDirectoryProviderModule(this)
        );
        
    }
    
    @Override
    public String getIdentifier() {
        return "multiuser";
    }

    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {

        if (context instanceof MultiUserContext)
            return context;
        
        return new MultiUserContext(context);

    }

}
