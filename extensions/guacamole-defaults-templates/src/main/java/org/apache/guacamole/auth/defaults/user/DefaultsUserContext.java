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

package org.apache.guacamole.auth.defaults.user;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.defaults.connection.DefaultsConnection;
import org.apache.guacamole.auth.defaults.connection.DefaultsConnectionFactory;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.UserContext;

/**
 * A UserContext implementation that delegates functionality to an underlying
 * UserContext implementation, implementing methods that provide for the default
 * connection parameter functionality implemented by this extension.
 */
public class DefaultsUserContext extends DelegatingUserContext {
    
    @Inject
    private DefaultsConnectionFactory defaultsConnectionFactory;
    
    @AssistedInject
    public DefaultsUserContext(@Assisted UserContext userContext) {
        super(userContext);
    }
    
    @Override
    public Directory<Connection> getConnectionDirectory() throws GuacamoleException {
        return new DecoratingDirectory<Connection>(super.getConnectionDirectory()) {
            @Override
            protected Connection decorate(Connection object) throws GuacamoleException {
                if (object instanceof DefaultsConnection)
                    return object;
                return defaultsConnectionFactory.create(object);
            }
            
            @Override
            protected Connection undecorate(Connection object) throws GuacamoleException {
                if (object instanceof DefaultsConnection)
                    return ((DefaultsConnection) object).getUndecorated();
                return object;
            }
            
        };
    }
    
}
