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

package org.apache.guacamole.auth.credprof.rest;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import org.apache.guacamole.auth.credprof.connection.CredentialProfileConnection;
import org.apache.guacamole.auth.credprof.user.CredentialProfile;
import org.apache.guacamole.auth.credprof.user.CredentialProfileUserContext;

/**
 *
 * @author nick_couchman.sa
 */
public class CredentialProfileResource {
    
    private final CredentialProfileUserContext userContext;
    
    public CredentialProfileResource(CredentialProfileUserContext userContext) {
        this.userContext = userContext;
    }

    @GET
    @Path("profile")
    public Map<String, String> getCredentialProfile()  {
        
        CredentialProfile credentialProfile = userContext.getCredentialProfile();
        Map<String, String> profile = new HashMap<>();
        profile.put("identifier", credentialProfile.getProfileName());
        profile.put(CredentialProfileConnection.TOKEN_NAME_USERNAME, credentialProfile.getUsername());
        profile.put(CredentialProfileConnection.TOKEN_NAME_DOMAIN, credentialProfile.getDomain());
        profile.put(CredentialProfileConnection.TOKEN_NAME_PASSWORD, String.valueOf(credentialProfile.hasPassword()));
        profile.put(CredentialProfileConnection.TOKEN_NAME_SSH_KEY, String.valueOf(credentialProfile.hasSSHKey()));
        profile.put(CredentialProfileConnection.TOKEN_NAME_SSH_PASSPHRASE, String.valueOf(credentialProfile.hasSSHPassphrase()));
        
        return profile;
        
    }
    
    @PUT
    @Path("profile")
    public void putCredentialProfile(@FormParam("identifier") String identifier,
            @FormParam("username") String username, @FormParam("password") String password,
            @FormParam("domain") String domain, @FormParam("ssh_key") String ssh_key,
            @FormParam("ssh_passphrase") String ssh_passphrase) {
        
        userContext.setCredentialProfile(identifier, username, password, domain, ssh_key, ssh_passphrase);
        
    }
    
}
