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
package org.apache.guacamole.auth.sync;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.sync.user.ActiveConnectionSyncUserContext;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * An "Authentication" provider with the purpose of synchronizing ActiveConnectio
 * objects across multiple nodes.
 */
public class ActiveConnectionSyncProvider extends AbstractAuthenticationProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveConnectionSyncProvider.class);
    
    private ActiveConnectionSyncUserContext userContext;
    
    /**
     * Injector which will manage the object graph of this authentication
     * provider.
     */
    private final Injector injector;
    
    public ActiveConnectionSyncProvider() throws GuacamoleException {
        // Set up Guice injector.
        injector = Guice.createInjector(
            new ActiveConnectionSyncProviderModule(this)
        );
    }
    
    @Override
    public String getIdentifier() {
        return "connsync";
    }
    
    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        LOGGER.debug(">>>SYNC<<< Decorating UserContext: {}", context.getClass().toString());
        
        if (context instanceof ActiveConnectionSyncUserContext)
            return context;
        
        this.userContext = new ActiveConnectionSyncUserContext(context);
        return userContext;
        
    }
    
    @Override
    public UserContext redecorate(UserContext decorated, UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        LOGGER.debug(">>>SYNC<<< Redecorating User Context: {}", context.getClass().toString());
        
        this.userContext = new ActiveConnectionSyncUserContext(context);
        return userContext;
        
    }
    
    @Override
    public void shutdown() {
        userContext.shutdown();
    }
    
}
