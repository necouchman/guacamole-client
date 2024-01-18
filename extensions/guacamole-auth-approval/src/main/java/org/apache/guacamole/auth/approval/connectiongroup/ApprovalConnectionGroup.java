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

package org.apache.guacamole.auth.approval.connectiongroup;

import com.google.inject.Inject;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleSecurityException;
import org.apache.guacamole.auth.approval.Approvable;
import org.apache.guacamole.auth.approval.ApprovalConnectionService;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.DelegatingConnectionGroup;
import org.apache.guacamole.protocol.GuacamoleClientInformation;

/**
 * A connection group that should be restricted such that access to it requires
 * approval, but the remaining functionality should be delegated to another
 * ConnectionGroup class.
 */
public class ApprovalConnectionGroup extends DelegatingConnectionGroup implements Approvable {
    
    /**
     * Service to assist with handling connection approval look-ups.
     */
    @Inject
    private ApprovalConnectionService connectionService;
    
    /**
     * Whether or not access to this resource has been approved for the
     * user who is currently logged in.
     */
    boolean approved = false;
    
    /**
     * Create a new ApprovalConnectionGroup, delegating functionality to the
     * specified ConnectionGroup class.
     * 
     * @param connectionGroup 
     *     The existing ConnectionGroup object to wrap.
     */
    public ApprovalConnectionGroup(ConnectionGroup connectionGroup) {
        super(connectionGroup);
    }
    
    /**
     * Retrieve the underlying ConnectionGroup object.
     * 
     * @return 
     *     The underlying ConnectionGroup object that this object wraps.
     */
    public ConnectionGroup getUndecorated() {
        return getDelegateConnectionGroup();
    }
    
    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info, Map<String, String> tokens) throws GuacamoleException {
        if (connectionService.isApproved(this))
            return super.connect(info, tokens);
        
        throw new GuacamoleSecurityException("Access to this ConnectionGroup has not been approved for this User.");
        
    }
    
}
