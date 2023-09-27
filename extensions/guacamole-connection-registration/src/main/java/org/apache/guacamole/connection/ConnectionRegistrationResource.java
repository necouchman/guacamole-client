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

package org.apache.guacamole.connection;

import java.util.Collections;
import java.util.Map;
import javax.ws.rs.Produces;
import org.apache.guacamole.GuacamoleException;

/**
 * A lass that implements REST endpoints for connection registration.
 */
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ConnectionRegistrationResource {
    
    /**
     * Register a new connection with the provider, with the given protocol, connection group
     * identifier, and list of arbitrary connection parameters, returning the identifier of the
     * connection that has been registered.
     * 
     * @param protocol
     *     The protocol that should be used to connect to the host that is
     *     registering.
     * 
     * @param connectionGroupId
     *     The identifier of an existing connection group where the host should
     *     be registered, or "ROOT" if it should be registered at the root
     *     of the connection directory.
     * 
     * @param parameters
     *     An array of connection parameters that should be specified for the
     *     connection, with the key being the parameter name and the value
     *     the parameter value. These connection parameters can be any valid
     *     parameters for the specified protocol, but, at a minimum should
     *     contain the values required to make a connection to the system.
     * 
     * @return
     *     The identifier of the connection that has been created.
     * 
     * @throws GuacamoleException 
     *     If an error occurs registering the connection.
     */
    @POST
    @Path("register")
    public String register(@FormParam("protocol") String protocol,
            @FormParam("connectionGroup") String connectionGroupId,
            @FormParam("parameters") Array<String, String> parameters) 
            throws GuacamoleException {

        
 
    }
    
    /**
     * Unregister the connection with the given identifier, returning that same identifier
     * if the de-registratino is successful.
     * 
     * @param identifier
     *     The identifier of the connection to un-register.
     * 
     * @return
     *     The identifier of the connection that has been un-registered.
     * 
     * @throws GuacamoleException 
     *     If an error occurs un-registering the connection.
     */
    @DELETE
    @Path("unregister")
    public String unregister(@FormParam("identifier") String identifier)
            throws GuacamoleException {
        
    }
    
}
