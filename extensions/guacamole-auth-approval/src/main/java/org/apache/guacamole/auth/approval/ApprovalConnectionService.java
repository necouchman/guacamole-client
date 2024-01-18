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
package org.apache.guacamole.auth.approval;

import java.util.Map;
import java.util.UUID;
import org.apache.guacamole.net.auth.User;

/**
 * A service that assists with providing status of approvals to Connection
 * and ConnectionGroup objects during connection time.
 */
public class ApprovalConnectionService {
    
    /**
     * The object containing all of the current approval requests.
     */
    private ApprovalRequests approvalRequests;
    
    /**
     * The currently logged-in user.
     */
    private User currentUser;
    
    /**
     * Initialize the service with the provided requests and current user.
     * 
     * @param approvalRequests
     *     The object containing all current requests.
     * 
     * @param currentUser 
     *     The currently logged-in user.
     */
    public void init(ApprovalRequests approvalRequests, User currentUser) {
        
        this.approvalRequests = approvalRequests;
        this.currentUser = currentUser;
        
    }
    
    /**
     * Check if the provided Approvable object is approved for the currently
     * logged-in user.
     * 
     * @param approvable
     *     The Approvable object to check.
     * 
     * @return 
     *     true if the connection is approved for the current user, otherwise
     *     false.
     */
    public boolean isApproved(Approvable approvable) {
        
        for (Map.Entry<UUID, ApprovalRequest> entry : approvalRequests.getAll().entrySet()) {
            ApprovalRequest request = entry.getValue();
            if (request.getRequestingUser().equals(currentUser)
                    && request.getResource().equals(approvable))
                return request.isApproved();
        }
        
        return false;
        
    }
    
}
