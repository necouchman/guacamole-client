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

package org.apache.guacamole.auth.ban;

import com.google.inject.AbstractModule;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.environment.LocalEnvironment;
import org.apache.guacamole.net.auth.AuthenticationProvider;

/**
 * A module that provides injection services for the BanningAuthenticationProvider.
 */
public class BanningAuthenticationProviderModule extends AbstractModule {
    
    /**
     * The environment in which the server runs.
     */
    private final Environment environment;
    
    /**
     * The AuthenticationProvider that is invoking this module.
     */
    private final AuthenticationProvider authProvider;
    
    /**
     * Create a new BanningAuthenticationProviderModule to provide the required
     * injections for the Banning Authentication provider.
     * 
     * @param authProvider
     *     The AuthenticationProvider instance invoking this module.
     * 
     * @throws GuacamoleException 
     *     If an exception occurs while retrieving the server environment.
     */
    public BanningAuthenticationProviderModule(AuthenticationProvider authProvider) throws GuacamoleException {
        
        // The environment in which the server is running.
        this.environment = LocalEnvironment.getInstance();
        
        // The authentication provider that called this module.
        this.authProvider = authProvider;
        
    }
    
    @Override
    protected void configure() {

        // Bind core implementations of guacamole-ext classes
        bind(AuthenticationProvider.class).toInstance(authProvider);
        bind(Environment.class).toInstance(environment);
        
        bind(BanningAuthenticationListener.class);
        
    }
    
}
