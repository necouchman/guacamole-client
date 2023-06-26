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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.otp.user.OTPUserContext;
import org.apache.guacamole.auth.otp.user.OTPVerificationService;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An authentication module that adds generic One-Time Password functionality
 * to an existing authentication module, using either e-mail or SMS to send
 * a message to a user.
 */
public class OTPAuthenticationProvider extends AbstractAuthenticationProvider {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OTPAuthenticationProvider.class);
    
    /**
     * Injector that will be used to manage dependencies.
     */
    private final Injector injector;
    
    /**
     * The session map that will keep track of in-progress OTP authentication
     * attempts.
     */
    private final OTPSessionMap otpSessionMap = new OTPSessionMap();
    
    /**
     * Create a new instance of the OTPAuthenticationProvider class, setting
     * up the Guice injector.
     * 
     * @throws GuacamoleException
     *     If a required property is missing or an error occurs retrieving
     *     or parsing the configuration.
     */
    public OTPAuthenticationProvider() throws GuacamoleException {
        
        // Set up Guice injector.
        injector = Guice.createInjector(
            new OTPAuthenticationProviderModule(this, otpSessionMap)
        );
        
    }
    
    @Override
    public String getIdentifier() {
        return "otp";
    }
    
    @Override
    public UserContext decorate(UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        
        LOGGER.debug("Decorating context: {}", context.getClass().getName());
        
        OTPVerificationService verificationService =
                injector.getInstance(OTPVerificationService.class);

        // Verify identity of user
        verificationService.verifyUser(context, authenticatedUser);

        // User has been verified, and authentication should be allowed to
        // continue
        return new OTPUserContext(context);
        
    }
    
    @Override
    public UserContext redecorate(UserContext decorated, UserContext context,
            AuthenticatedUser authenticatedUser, Credentials credentials)
            throws GuacamoleException {
        return new OTPUserContext(context);
    }
    
    @Override
    public void shutdown() {
        otpSessionMap.shutdown();
    }
    
    
}
