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

import com.google.inject.AbstractModule;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.environment.LocalEnvironment;
import org.apache.guacamole.net.auth.AuthenticationProvider;
import org.apache.guacamole.net.auth.User;

/**
 * Guice module which configures Approval-specific injections.
 */
public class ApprovalAuthenticationProviderModule extends AbstractModule {

    /**
     * Guacamole server environment.
     */
    private final Environment environment;

    /**
     * A reference to the ApprovalAuthenticationProvider on behalf of which
     * this module has configured injection.
     */
    private final AuthenticationProvider authProvider;
    
    /**
     * The approval request service.
     */
    private final ApprovalRequests approvalRequests;
    
    /**
     * The approval connection service.
     */
    private final ApprovalConnectionService approvalConnectionService = 
            new ApprovalConnectionService();

    /**
     * Creates a new Approval authentication provider module which
     * configures injection for the ApprovalAuthenticationProvider.
     *
     * @param authProvider
     *     The AuthenticationProvider for which injection is being configured.
     * 
     * @param approvalRequests
     *     The ApprovalRequests for this authentication provider.
     *
     * @throws GuacamoleException
     *     If an error occurs while retrieving the Guacamole server
     *     environment.
     */
    public ApprovalAuthenticationProviderModule(
            AuthenticationProvider authProvider,
            ApprovalRequests approvalRequests) throws GuacamoleException {

        // Get local environment
        this.environment = LocalEnvironment.getInstance();

        // Store associated auth provider
        this.authProvider = authProvider;
        
        // Associate the request service
        this.approvalRequests = approvalRequests;
        
    }

    @Override
    protected void configure() {

        // Bind core implementations of guacamole-ext classes
        bind(AuthenticationProvider.class).toInstance(authProvider);
        bind(Environment.class).toInstance(environment);
        
        // Bind approval-specific classes
        bind(ApprovalRequests.class).toInstance(approvalRequests);
        bind(ApprovalConnectionService.class).toInstance(approvalConnectionService);

    }

}