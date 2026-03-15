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
package org.apache.guacamole.directory.multiuser.connection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.form.BooleanField;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DelegatingConnection;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.protocol.GuacamoleClientInformation;

/**
 * A connection that wraps another connection, allowing multiple users to
 * access the same connection concurrently.
 */
public class MultiUserConnection extends DelegatingConnection {
    
    /**
     * The attribute that controls whether or not multi-user access to this
     * connection is allowed.
     */
    public final static String MULTI_USER_CONNECTION_ENABLED_ATTRIBUTE_NAME = 
            "multi-user-connection-enabled";
    
    /**
     * The value for the multi-user connection enabled attribute that marks
     * it as enabled for a connection.
     */
    public final static String MULTI_USER_CONNECTION_ENABLED_ATTRIBUTE_VALUE = "true";
    
    /**
     * The list of all attributes for this connection.
     */
    public final static List<String> MULTI_USER_CONNECTION_ATTRIBUTES = 
            Arrays.asList(MULTI_USER_CONNECTION_ENABLED_ATTRIBUTE_NAME);
    
    /**
     * The connection form for this connection, including all of the additional
     * fields.
     */
    public final static Form MULTI_USER_CONNECTION_FORM = new Form("multi-user-connection-form",
            Arrays.asList(
                    new BooleanField(MULTI_USER_CONNECTION_ENABLED_ATTRIBUTE_NAME, MULTI_USER_CONNECTION_ENABLED_ATTRIBUTE_VALUE)
            )
    );
    
    /**
     * Inject the active connection directory.
     */
    private final Directory<ActiveConnection> activeConnectionDirectory;
    
    /**
     * Create a new instance of a multi-user connection, wrapping the given
     * existing connection and providing an active connection directory that
     * can be queried to locate other active connections.
     * 
     * @param connection
     *     THe connection that this connection wraps.
     * 
     * @param activeConnectionDirectory 
     *     A reference to the active connection directory that contains 
     *     existing, running (active) connections.
     */
    public MultiUserConnection(Connection connection, Directory<ActiveConnection> activeConnectionDirectory) {
        super(connection);
        this.activeConnectionDirectory = activeConnectionDirectory;
    }
    
    /**
     * Return the undecorated version of this connection, the underlying
     * connection that this connection wraps.
     * 
     * @return 
     *     The undecorated connection that this connection wraps.
     */
    public Connection getUndecorated() {
        return super.getDelegateConnection();
    }
    
    /**
     * Check whether multi-user access to this connection is enabled, returning
     * a boolean true if it is, otherwise false.
     * 
     * @return 
     *      true if multi-user access is enabled for this connection, otherwise
     *      false.
     */
    private boolean multiUserEnabled() {
        
        /* Get current attributes for this connection. */
        Map<String, String> attributes = getAttributes();
        if (attributes != null) {
            
            /* Pull the value and check if it's enabled, returning true if it is. */
            String multiUserEnabledValue = attributes.get(MULTI_USER_CONNECTION_ENABLED_ATTRIBUTE_NAME);
            if (multiUserEnabledValue != null && multiUserEnabledValue.equals(MULTI_USER_CONNECTION_ENABLED_ATTRIBUTE_VALUE))
                return true;
        }
        
        /* Attribute not found or not set to the truth value. */
        return false;
    }
    
    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info,
            Map<String, String> tokens) throws GuacamoleException {
        
        GuacamoleTunnel multiUserTunnel;
        
        /* Check for multi-user support and that it is enabled for this connection. */
        if (multiUserEnabled() && activeConnectionDirectory instanceof MultiUserActiveConnectionDirectory) {
            
            /* Find an existing active connection matching this one. */
            if (((MultiUserActiveConnectionDirectory) activeConnectionDirectory).hasConnection(this.getIdentifier()))
                multiUserTunnel = activeConnectionDirectory.get(
                        ((MultiUserActiveConnectionDirectory) activeConnectionDirectory).getActiveConnectionIdentifier(this.getIdentifier())
                ).connect(info, tokens);
            
            /* No existing active connection exists, so start a new one. */
            else
                multiUserTunnel = super.connect(info, tokens);
            
        }
        
        /* No support for multi-user connections, or it is not enabled. */
        else
            multiUserTunnel = super.connect(info, tokens);
        
        /* Return whichever tunnel is configured. */
        return multiUserTunnel;
        
    }
    
    @Override
    public Map<String, String> getAttributes() {
        
        // Create independent, mutable copy of attributes
        Map<String, String> attributes = new HashMap<>(super.getAttributes());
        
        // Loop through extension-specific attributes and add them where no
        // values exist, so that they show up in the web UI.
        for (String attribute : MULTI_USER_CONNECTION_ATTRIBUTES) {
            String value = attributes.get(attribute);
            if (value == null || value.isEmpty())
                attributes.put(attribute,  null);
        }

        /* Return the attributes including the ones applicable to multi-user connections. */
        return attributes;
        
    }
    
    @Override
    public void setAttributes(Map<String, String> attributes) {
        
        // Create independent, mutable copy of attributes
        attributes = new HashMap<>(attributes);

        // Loop through extension-specific attributes, only sending ones
        // that are non-null and non-empty to the underlying storage mechanism.
        for (String attribute : MULTI_USER_CONNECTION_ATTRIBUTES) {
            String value = attributes.get(attribute);
            if (value != null && value.isEmpty())
                attributes.put(attribute, null);
        }

        /* Pass the attributes up to the wrapped connection. */
        super.setAttributes(attributes);
        
    }
    
}
