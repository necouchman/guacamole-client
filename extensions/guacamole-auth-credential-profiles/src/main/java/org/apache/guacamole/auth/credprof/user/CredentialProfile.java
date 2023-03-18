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

package org.apache.guacamole.auth.credprof.user;

import java.util.HashMap;
import java.util.Map;
import org.apache.guacamole.auth.credprof.connection.CredentialProfileConnection;

/**
 *
 * @author nick_couchman.sa
 */
public class CredentialProfile {
    
    private final String profile_name;
    
    private String username;
    
    private String password;
    
    private String domain;
    
    private String ssh_key;
    
    private String ssh_passphrase;
    
    public CredentialProfile(String profile_name, String username, String password, String domain, String ssh_key, String ssh_passphrase) {
        this.profile_name = profile_name;
        this.username = username;
        this.password = password;
        this.domain = domain;
        this.ssh_key = ssh_key;
        this.ssh_passphrase = ssh_passphrase;
    }
    
    public String getProfileName() {
        return profile_name;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    protected String getPassword() {
        return password;
    }
    
    public boolean hasPassword() {
        return (password != null && !password.isEmpty());
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    protected String getSSHKey() {
        return ssh_key;
    }
    
    public boolean hasSSHKey() {
        return (ssh_key != null && !password.isEmpty());
    }
    
    public void setSSHKey(String ssh_key) {
        this.ssh_key = ssh_key;
    }
    
    protected String getSSHPassphrase() {
        return ssh_passphrase;
    }
    
    public boolean hasSSHPassphrase() {
        return (ssh_passphrase != null && !ssh_passphrase.isEmpty());
    }
    
    public void setSSHPassphrase(String ssh_passphrase) {
        this.ssh_passphrase = ssh_passphrase;
    }
    
    public Map<String, String> getProfileTokens() {
        Map<String, String> profileTokens = new HashMap<>();
        profileTokens.put(CredentialProfileConnection.TOKEN_NAME_USERNAME, username);
        profileTokens.put(CredentialProfileConnection.TOKEN_NAME_PASSWORD, password);
        profileTokens.put(CredentialProfileConnection.TOKEN_NAME_DOMAIN, domain);
        profileTokens.put(CredentialProfileConnection.TOKEN_NAME_SSH_KEY, ssh_key);
        profileTokens.put(CredentialProfileConnection.TOKEN_NAME_SSH_PASSPHRASE, ssh_passphrase);
        
        return profileTokens;
    }
    
}
