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

package org.apache.guacamole.auth.approval;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.approval.user.ApprovalUserContext;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;

/**
 * An authentication provider that manages access to logins and connections
 * by requiring approval from an administrator.
 */
public class ApprovalAuthenticationProvider extends AbstractAuthenticationProvider{
    
    /**
     * Injector which will manage the object graph of this authentication
     * provider.
     */
    private final Injector injector;
    
    /**
     * The approval request service, which will be a single instance of the
     * service for all users of this module.
     */
    private final ApprovalRequests approvalRequests = new ApprovalRequests();
    
    /**
     * Create a new version of this authentication provider module.
     * 
     * @throws GuacamoleException 
     *     If an error occurs creating the module.
     */
    public ApprovalAuthenticationProvider() throws GuacamoleException {

        // Set up Guice injector.
        injector = Guice.createInjector(
            new ApprovalAuthenticationProviderModule(this, approvalRequests)
        );
    }
    
    @Override
    public String getIdentifier() {
        return "approval";
    }
    
    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        // Initialize the connection service.
        ApprovalConnectionService connectionService = 
                injector.getInstance(ApprovalConnectionService.class);
        connectionService.init(approvalRequests, context.self());
        
        return new ApprovalUserContext(context);
    }
    
}
