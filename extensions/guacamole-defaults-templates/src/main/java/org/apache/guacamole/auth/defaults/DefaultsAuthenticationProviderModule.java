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

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.defaults.conf.ConfigurationService;
import org.apache.guacamole.auth.defaults.connection.DefaultsConnection;
import org.apache.guacamole.auth.defaults.connection.DefaultsConnectionFactory;
import org.apache.guacamole.auth.defaults.user.DefaultsUserContext;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.environment.LocalEnvironment;
import org.apache.guacamole.net.auth.AuthenticationProvider;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.net.auth.UserContextFactory;

/**
 * A Guice module which provides Defaults-specific implementations.
 */
public class DefaultsAuthenticationProviderModule extends AbstractModule {
    
    /**
     * Guacamole server environment.
     */
    private final Environment environment;

    /**
     * A reference to the DefaultsAuthenticationProvider on behalf of which this
     * module has configured injection.
     */
    private final AuthenticationProvider authProvider;
    
    /**
     * Create a new instance of this Guice module, referencing the specified
     * authentication provider, in order to provide injections for this
     * extension.
     * 
     * @param authProvider
     *     The authentication provider to which this instance is linked.
     * 
     * @throws GuacamoleException 
     *     If the local environment cannot be retrieved.
     */
    public DefaultsAuthenticationProviderModule(AuthenticationProvider authProvider)
            throws GuacamoleException {

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
        
        // Bind Defaults-specific services
        bind(ConfigurationService.class);

        // Install the builder for the DefaultsUserContext
        install(new FactoryModuleBuilder()
                .implement(UserContext.class, DefaultsUserContext.class)
                .build(UserContextFactory.class)
        );
        
        // Install the builder for the DefaultsConnection
        install(new FactoryModuleBuilder()
                .implement(DefaultsConnection.class, DefaultsConnection.class)
                .build(DefaultsConnectionFactory.class)
        );
        


    }
    
}
