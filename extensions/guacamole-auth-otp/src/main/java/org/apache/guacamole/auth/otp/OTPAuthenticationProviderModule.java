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

package org.apache.guacamole.auth.otp;

import com.google.inject.AbstractModule;
import org.apache.guacamole.auth.otp.user.OTPVerificationService;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.environment.LocalEnvironment;
import org.apache.guacamole.net.auth.AuthenticationProvider;

/**
 * A Guice module that provides injections for the OTP Authentication Provider.
 */
public class OTPAuthenticationProviderModule extends AbstractModule {
    
    /**
     * Guacamole server environment.
     */
    private final Environment environment;

    /**
     * A reference to the OTPAuthenticationProvider on behalf of which this
     * module has configured injection.
     */
    private final AuthenticationProvider authProvider;
    
    /**
     * The OTPSessionMap that contains a map of user identifiers to one-time
     * passwords.
     */
    private final OTPSessionMap otpSessionMap;
    
    /**
     * Create a new Guice module for handling one-time password authentication.
     * 
     * @param authProvider
     *     The authentication provider associated with this Guice module.
     * 
     * @param otpSessionMap 
     *     The session map containing the current user identifier to one-time
     *     passwords.
     */
    public OTPAuthenticationProviderModule(AuthenticationProvider authProvider, OTPSessionMap otpSessionMap) {
        
        // Get the local server environment
        this.environment = LocalEnvironment.getInstance();
        
        // Associate with the OTP Authentication Provider module.
        this.authProvider = authProvider;
        
        // The session map that will store in-progress OTP requests.
        this.otpSessionMap = otpSessionMap;
    }
    
    @Override
    protected void configure() {
        
        // Bind core implementations of guacamole-ext classes
        bind(AuthenticationProvider.class).toInstance(authProvider);
        bind(Environment.class).toInstance(environment);
        
        // Bind OTP-specific classes
        bind(OTPVerificationService.class);
        requestStaticInjection(OTPAuthenticationEventListener.class);
        bind(OTPSessionMap.class).toInstance(otpSessionMap);
        
    }
    
}
