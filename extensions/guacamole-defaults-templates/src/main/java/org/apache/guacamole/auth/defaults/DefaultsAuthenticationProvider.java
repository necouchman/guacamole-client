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

package org.apache.guacamole.auth.defaults;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.net.auth.UserContextFactory;

/**
 * AuthenticationProvider implementation which provides functionality that
 * allows for connection parameters to be either assigned default values and/or
 * provide values that forcibly override those configured in each connection.
 */
public class DefaultsAuthenticationProvider extends AbstractAuthenticationProvider {
    
    /**
     * Injector which will manage the object graph of this authentication
     * provider.
     */
    private final Injector injector;
    
    /**
     * Factory for creating instances of the relevant Defaults-specific
     * UserContext implementation.
     */
    private final UserContextFactory userContextFactory;

    /**
     * Creates a new DefaultsAuthenticationProvider that provides default
     * settings for connections.
     *
     * @throws GuacamoleException
     *     If a required property is missing, or an error occurs while parsing
     *     a property.
     */
    public DefaultsAuthenticationProvider() throws GuacamoleException {

        // Set up Guice injector.
        injector = Guice.createInjector(
            new DefaultsAuthenticationProviderModule(this)
        );
        
        this.userContextFactory = injector.getInstance(UserContextFactory.class);

    }
    
    @Override
    public String getIdentifier() {
        return "defaults";
    }

    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        return userContextFactory.create(context);
        
    }

}
