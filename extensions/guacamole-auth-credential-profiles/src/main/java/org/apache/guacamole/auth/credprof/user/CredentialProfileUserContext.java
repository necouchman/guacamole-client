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

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.credprof.connection.CredentialProfileConnection;
import org.apache.guacamole.auth.credprof.rest.CredentialProfileResource;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.UserContext;

/**
 * A UserContext implementation to support temporarily storing credentials
 * and decorating connections.
 */
public class CredentialProfileUserContext extends DelegatingUserContext {

    private CredentialProfile credentialProfile;
    
    /**
     * Create a new UserContext for this class, which delegates functionality
     * to the specified underlying UserContext.
     * 
     * @param userContext 
     *     The UserContext to delegate to.
     */
    public CredentialProfileUserContext(UserContext userContext) {
        super(userContext);
    }

    @Override
    public Directory<Connection> getConnectionDirectory() throws GuacamoleException {
        return new DecoratingDirectory<Connection>(super.getConnectionDirectory()) {
            
            @Override
            protected Connection decorate(Connection object) {
                if (object instanceof CredentialProfileConnection)
                    return object;
                return new CredentialProfileConnection(object, credentialProfile);
            }
            
            @Override
            protected Connection undecorate(Connection object) {
                assert(object instanceof CredentialProfileConnection);
                return ((CredentialProfileConnection) object).getUndecorated();
            }
            
        };
    }
    
    public void setCredentialProfile(String identifier, String username,
            String password, String domain, String ssh_key, String ssh_passphrase) {
        this.credentialProfile = new CredentialProfile(identifier, username, password, domain, ssh_key, ssh_passphrase);
    }
    
    public CredentialProfile getCredentialProfile() {
        return credentialProfile;
    }
    
    @Override
    public Object getResource() {
        return new CredentialProfileResource(this);
    }
    
}
