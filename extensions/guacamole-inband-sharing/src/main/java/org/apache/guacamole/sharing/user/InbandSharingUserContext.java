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

package org.apache.guacamole.sharing.user;

import com.google.inject.Inject;
import java.util.Collection;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.SharingProfile;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.sharing.InbandSharingProfile;

/**
 * A UserContext implementation that wraps an existing user context, providing
 * additional connection sharing options and functionality.
 */
public class InbandSharingUserContext extends DelegatingUserContext {
    
    /**
     * Create a new InbandSharingUserContext instance, wrapping the
     * given UserContext.
     * 
     * @param userContext
     *     The UserContext object to wrap and delegate functionality to.
     */
    public InbandSharingUserContext(UserContext userContext) {
        super(userContext);
    }
    
    /**
     * The connection directory that contains the shared connections.
     */
    @Inject
    private Directory<Connection> connectionDirectory;
    
    /**
     * The connection group directory for the shared connections.
     */
    @Inject
    private Directory<ConnectionGroup> connectionGroupDirectory;
    
    @Override
    public Directory<Connection> getConnectionDirectory() throws GuacamoleException {
        return connectionDirectory;
    }
    
    @Override
    public Directory<ConnectionGroup> getConnectionGroupDirectory() throws GuacamoleException {
        return connectionGroupDirectory;
    }
    
    @Override
    public Directory<SharingProfile> getSharingProfileDirectory() throws GuacamoleException {
        return new DecoratingDirectory<SharingProfile>(super.getSharingProfileDirectory()) {
            
            @Override
            protected SharingProfile decorate(SharingProfile object) {
                if (object instanceof InbandSharingProfile)
                    return object;
                return new InbandSharingProfile(object);
            }
            
            @Override
            protected SharingProfile undecorate(SharingProfile object) {
                if (object instanceof InbandSharingProfile)
                    return ((InbandSharingProfile) object).getUndecorated();
                return object;
            }
            
        };
    }
    
    @Override
    public Collection<Form> getSharingProfileAttributes() {
        Collection<Form> attributes = super.getSharingProfileAttributes();
        return attributes;
    }
    
}
