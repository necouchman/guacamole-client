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

package org.apache.guacamole.auth.credprof.connection;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.credprof.user.CredentialProfile;
import org.apache.guacamole.auth.credprof.user.CredentialProfileUserContext;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DelegatingConnection;
import org.apache.guacamole.protocol.GuacamoleClientInformation;

/**
 * A connection that delegates to and wraps an underlying connection.
 */
public class CredentialProfileConnection extends DelegatingConnection {
    
    private final CredentialProfile credentialProfile;
    
    public static final String TOKEN_NAME_USERNAME = "GUAC_PROFILE_USERNAME";
    
    public static final String TOKEN_NAME_PASSWORD = "GUAC_PROFILE_PASSWORD";
    
    public static final String TOKEN_NAME_DOMAIN = "GUAC_PROFILE_DOMAIN";
    
    public static final String TOKEN_NAME_SSH_KEY = "GUAC_PROFILE_SSH_KEY";
    
    public static final String TOKEN_NAME_SSH_PASSPHRASE = "GUAC_PROFILE_SSH_PASSPHRASE";
    
    public CredentialProfileConnection(Connection connection, CredentialProfile credentialProfile) {
        super(connection);
        this.credentialProfile = credentialProfile;
    }
    
    public Connection getUndecorated() {
        return getDelegateConnection();
    }
    
    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info,
            Map<String, String> tokens) throws GuacamoleException {

        Map<String, String> profileTokens = credentialProfile.getProfileTokens();
        profileTokens.putAll(tokens);
        
        return super.connect(info, profileTokens);
        
    }
    
}
