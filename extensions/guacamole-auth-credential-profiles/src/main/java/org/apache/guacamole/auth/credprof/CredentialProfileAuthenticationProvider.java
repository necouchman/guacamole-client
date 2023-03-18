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

package org.apache.guacamole.auth.credprof;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.credprof.user.CredentialProfileUserContext;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;

/**
 * This is an authentication provider that allows users to enter and store
 * credentials temporarily and re-use them within connections for the life
 * of their session. Once a user session has timed out or been logged out
 * the credentials will be lost and must be re-entered.
 */
public class CredentialProfileAuthenticationProvider extends AbstractAuthenticationProvider {

    /**
     * Injector which will manage the object graph of this authentication
     * provider.
     */
    private final Injector injector;

    /**
     * Creates a new Credential Profile AuthenticationProvider that
     * stores credentials for users for the duration of their Guacamole
     * session.
     *
     * @throws GuacamoleException
     *     If a required property is missing, or an error occurs while parsing
     *     a property.
     */
    public CredentialProfileAuthenticationProvider() throws GuacamoleException {

        // Set up Guice injector.
        injector = Guice.createInjector(new CredentialProfileAuthenticationProviderModule(this));

    }

    @Override
    public String getIdentifier() {
        return "credprof";
    }


    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        if (context instanceof CredentialProfileUserContext)
            return context;
        
        return new CredentialProfileUserContext(context);
        
    }
    
    @Override
    public UserContext redecorate(UserContext decorated, UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        return new CredentialProfileUserContext(context);
        
    }

}
