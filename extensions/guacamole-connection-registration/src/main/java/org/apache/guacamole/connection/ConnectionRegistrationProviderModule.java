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

import com.google.inject.AbstractModule;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.environment.LocalEnvironment;
import org.apache.guacamole.net.auth.AuthenticationProvider;

/**
 * A module that manages ConnectionRegistration-specific Guice injections.
 */
public class ConnectionRegistrationProviderModule extends AbstractModule {
    
    /**
     * Guacamole server environment.
     */
    private final Environment environment;

    /**
     * A reference to the ConnectionRestirctionProvider on behalf of which
     * this module has configured injection.
     */
    private final AuthenticationProvider authProvider;
    
    /**
     * Create a new instance of this Guice module, configuring association for the local
     * environment and the calling authentication provider.
     * 
     * @param authProvider
     *     The authentication provider that created this injector.
     * 
     * @throws GuacamoleException 
     *     If an error occurs retrieving the Guacamole environment.
     */
    public ConnectionRegistrationProviderModule(
            AuthenticationProvider authProvider) throws GuacamoleException {

        // Get local environment
        this.environment = LocalEnvironment.getInstance();

        // Store associated auth provider
        this.authProvider = authProvider;

    }
    
    @Override
    protected void configure() {

        // Bind core implementations of guacamole-ext classes
        bind(AuthenticationProvider.class).toInstance(authProvider);
        bind(Environment.class).toInstance(environment);
     
        // Bind ConnectionRegistration-specific classes
        bind(ConnectionRegistrationResource.class);
        
    }
    
}
