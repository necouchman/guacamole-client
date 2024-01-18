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

package org.apache.guacamole.auth.approval.connection;

import com.google.inject.Inject;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleSecurityException;
import org.apache.guacamole.auth.approval.Approvable;
import org.apache.guacamole.auth.approval.ApprovalConnectionService;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DelegatingConnection;
import org.apache.guacamole.protocol.GuacamoleClientInformation;

/**
 * A connection that should be restricted such that approval is required to
 * access it, but delegates the remaining functionality to an underlying
 * authentication module.
 */
public class ApprovalConnection extends DelegatingConnection implements Approvable {
    
    /**
     * Service to assist with handling connection approval look-ups.
     */
    @Inject
    private ApprovalConnectionService connectionService;
    
    /**
     * Create a new ApprovalConnection, which wraps the given connection,
     * delegating much of the functionality to the wrapped connection
     * class, and with a default status of the connection not being approved
     * for the current user.
     * 
     * @param connection 
     *     The existing Connection object to wrap.
     */
    public ApprovalConnection(Connection connection) {
        super(connection);
    }
    
    /**
     * Retrieve the underlying Connection object.
     * 
     * @return 
     *     The underlying Connection object that this object wraps.
     */
    public Connection getUndecorated() {
        return getDelegateConnection();
    }
    
    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info, Map<String, String> tokens) throws GuacamoleException {
        if (connectionService.isApproved(this))
            return super.connect(info, tokens);
        
        throw new GuacamoleSecurityException("Access to this Connection has not been approved for this User.");
        
    }
    
}
