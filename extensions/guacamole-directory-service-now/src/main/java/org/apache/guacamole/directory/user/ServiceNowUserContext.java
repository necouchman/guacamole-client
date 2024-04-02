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

package org.apache.guacamole.directory.user;

import java.util.Collection;
import java.util.HashSet;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.directory.connection.ServiceNowConnectionDirectory;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.TokenInjectingUserContext;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.directory.connectiongroup.ServiceNowConnectionGroup;
import org.apache.guacamole.directory.util.ServiceNowAPI;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.net.auth.Connection;

/**
 * UserContext implementation that manages objects related to ServiceNow
 * CMDB connections.
 */
public class ServiceNowUserContext extends TokenInjectingUserContext {

    /**
     * 
     */
    private final ServiceNowAPI snowApi;
    
    /**
     * 
     */
    private final Directory<Connection> snowConnectionDirectory;
    
    /**
     * Creates a new ServiceNowUserContext that wraps the given UserContext,
     * providing the ability to configure Service Now integration and pull in
     * connection details from Service Now CMDB.
     *
     * @param context
     *     The UserContext to wrap.
     * 
     * @throws GuacamoleException
     */
    public ServiceNowUserContext(UserContext context) throws GuacamoleException {
        super(context);

        this.snowApi = new ServiceNowAPI();
        this.snowConnectionDirectory = new ServiceNowConnectionDirectory(super.getConnectionDirectory());
        
    }
    
    @Override
    public Directory<ConnectionGroup> getConnectionGroupDirectory() throws GuacamoleException {
        
        ServiceNowUserContext serviceNowContext = this;
        
        return new DecoratingDirectory<ConnectionGroup>(super.getConnectionGroupDirectory()) {
            
            @Override
            public ConnectionGroup decorate(ConnectionGroup object) throws GuacamoleException {
                if (object instanceof ServiceNowConnectionGroup)
                    return object;
                return new ServiceNowConnectionGroup(object, serviceNowContext);
            }
            
            @Override
            public ConnectionGroup undecorate(ConnectionGroup object) {
                if (object instanceof ServiceNowConnectionGroup)
                    return ((ServiceNowConnectionGroup)object).getWrappedConnectionGroup();
                return object;
            }
            
        };
    }
    
    @Override
    public Collection<Form> getConnectionGroupAttributes() {
        Collection<Form> connectionGroupForms = new HashSet<>(super.getConnectionGroupAttributes());
        connectionGroupForms.add(ServiceNowConnectionGroup.SERVICE_NOW_CONNECTIONGROUP_FORM);
        return connectionGroupForms;
    }
    
    @Override
    public Directory<Connection> getConnectionDirectory() throws GuacamoleException {
        
        return snowConnectionDirectory;
        
    }
    
    public ServiceNowAPI getSnowApi() {
        return snowApi;
    }

}
