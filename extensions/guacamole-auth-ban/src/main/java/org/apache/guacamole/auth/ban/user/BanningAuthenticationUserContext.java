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
package org.apache.guacamole.auth.ban.user;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.ban.rest.BanningAuthenticationProviderResource;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.net.auth.permission.SystemPermission;

/**
 * A user context that implements functionality for managing the in-memory
 * authentication failure table, delegating remaining functionality to an
 * underlying UserContext object.
 */
public class BanningAuthenticationUserContext extends DelegatingUserContext {
    
    /**
     * Create a new UserContext that delegates underlying functionality to the
     * specified UserContext, implementing additional functionality to handle
     * manipulation of the in-memory ban listing.
     * 
     * @param userContext 
     *     The UserContext to delegate functionality to.
     */
    public BanningAuthenticationUserContext(UserContext userContext) {
        super(userContext);
    }
    
    @Override
    public Object getResource() throws GuacamoleException {
        // Check for admin privileges.
        if (this.self().getEffectivePermissions().getSystemPermissions().hasPermission(SystemPermission.Type.ADMINISTER)) {
            return new BanningAuthenticationProviderResource();
        }
        
        // No admin privileges
        return null;
    }
    
}
