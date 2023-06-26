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

import com.google.inject.Inject;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.event.AuthenticationSuccessEvent;
import org.apache.guacamole.net.event.listener.Listener;

/**
 * An implementation of a Listener that looks for successful authentication
 * events and handles the removal of OTPs that have already been used.
 */
public class OTPAuthenticationEventListener implements Listener {
    
    /**
     * The session map associated with this authentication extension that holds
     * one-time passwords that have been generated and are available for
     * authentication.
     */
    @Inject
    private static OTPSessionMap otpSessionMap;
    
    @Override
    public void handleEvent(Object event) {
        
        if (event instanceof AuthenticationSuccessEvent) {
            String identifier = ((AuthenticationSuccessEvent) event).getAuthenticatedUser().getIdentifier();
            if (identifier != null && !identifier.equals(AuthenticatedUser.ANONYMOUS_IDENTIFIER) && !identifier.isEmpty())
                otpSessionMap.invalidate(identifier);
        }
        
    }
    
}
