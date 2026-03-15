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
package org.apache.guacamole.directory.multiuser.user;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.directory.multiuser.connection.MultiUserActiveConnectionDirectory;
import org.apache.guacamole.directory.multiuser.connection.MultiUserConnection;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.UserContext;

/**
 * A UserContext that implements additional functionality that allows connections
 * to be accessed by multiple users concurrently.
 */
public class MultiUserContext extends DelegatingUserContext {
    
    /**
     * Create a new MultiUserContext, wrapping the given UserContext and delegating
     * most of the functionality to the wrapped UserContext, and providing
     * additional functionality for multi-user connections.
     * 
     * @param context 
     *     THe original user context to wrap.
     */
    public MultiUserContext(UserContext context) {
        super(context);
    }
    
    @Override
    public Directory<ActiveConnection> getActiveConnectionDirectory()
            throws GuacamoleException {
        
        return new MultiUserActiveConnectionDirectory(super.getActiveConnectionDirectory());
        
    }
    
    @Override
    public Directory<Connection> getConnectionDirectory()
            throws GuacamoleException {
        return new DecoratingDirectory<Connection>(super.getConnectionDirectory()) {
            
            @Override
            protected Connection decorate(Connection object) throws GuacamoleException {
                if (object instanceof MultiUserConnection)
                    return object;
                return new MultiUserConnection(object, getActiveConnectionDirectory());
            }

            @Override
            protected Connection undecorate(Connection object) throws GuacamoleException {
                if (object instanceof MultiUserConnection)
                    return ((MultiUserConnection) object).getUndecorated();
                return object;
            }
        
        };
        
    }
    
    @Override
    public Collection<Form> getConnectionAttributes() {
        Collection<Form> connectionAttributes = new HashSet<>(super.getConnectionAttributes());
        connectionAttributes.add(MultiUserConnection.MULTI_USER_CONNECTION_FORM);
        return Collections.unmodifiableCollection(connectionAttributes);
    }
    
}
