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

package org.apache.guacamole.auth.active;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.active.user.ActiveConnectionUserContext;
import org.apache.guacamole.net.auth.AbstractAuthenticationProvider;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.UserContext;

/**
 *
 * @author nick
 */
public class GuacamoleActiveConnectionProvider extends AbstractAuthenticationProvider {

    @Override
    public String getIdentifier() {
        return "active-connections";
    }
    
    @Override
    public UserContext decorate(
            UserContext context,
            AuthenticatedUser authenticatedUser,
            Credentials credentials) throws GuacamoleException {
        
        if (context instanceof ActiveConnectionUserContext)
            return context;
        
        return new ActiveConnectionUserContext(context);
        
    }
    
    @Override
    public UserContext redecorate(
            UserContext decorated,
            UserContext context,
            AuthenticatedUser authenticatedUser,
            Credentials credentials) throws GuacamoleException {
        
        return new ActiveConnectionUserContext(context);
        
    }
    
}
