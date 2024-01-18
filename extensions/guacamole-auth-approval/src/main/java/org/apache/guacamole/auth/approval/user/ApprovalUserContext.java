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

package org.apache.guacamole.auth.approval.user;

import com.google.inject.Inject;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleSecurityException;
import org.apache.guacamole.auth.approval.Approvable;
import org.apache.guacamole.auth.approval.ApprovalRequest;
import org.apache.guacamole.auth.approval.ApprovalRequests;
import org.apache.guacamole.auth.approval.connection.ApprovalConnection;
import org.apache.guacamole.auth.approval.connectiongroup.ApprovalConnectionGroup;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.Permissions;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.net.auth.permission.ObjectPermission;
import org.apache.guacamole.net.auth.permission.SystemPermission;

/**
 * A user context that delegates underlying functionality to other extensions.
 */
public class ApprovalUserContext extends DelegatingUserContext {

    /**
     * The map containing approval requests.
     */
    @Inject
    private ApprovalRequests approvalRequests;
    
    /**
     * Create a new ApprovalUserContext, delegating the storage and managment
     * of users and connections to the specified underlying user context.
     * 
     * @param context 
     *     The context to which management of users, connections, etc., will be
     *     delegated.
     * 
     * @throws GuacamoleException
     *     If errors occur getting effective permissions.
     */
    public ApprovalUserContext(UserContext context) throws GuacamoleException {
        super(context);
    }
    
    @Override
    public Object getResource() {
        return new ApprovalUserResource(this);
    }
    
    @Override
    public Directory<Connection> getConnectionDirectory() throws GuacamoleException {
        
        return new DecoratingDirectory<Connection>(super.getConnectionDirectory()) {
            
            @Override
            protected Connection decorate(Connection object) {
                if (object instanceof ApprovalConnection)
                    return object;
                return new ApprovalConnection(object);
            }
            
            @Override
            protected Connection undecorate(Connection object) {
                if (object instanceof ApprovalConnection)
                    return ((ApprovalConnection) object).getUndecorated();
                return object;
            }
            
        };
    }
    
    @Override
    public Directory<ConnectionGroup> getConnectionGroupDirectory() throws GuacamoleException {
        
        return new DecoratingDirectory<ConnectionGroup>(super.getConnectionGroupDirectory()) {;
            
            @Override
            protected ConnectionGroup decorate(ConnectionGroup object) {
                if (object instanceof ApprovalConnectionGroup)
                    return object;
                return new ApprovalConnectionGroup(object);
            }
            
            @Override
            protected ConnectionGroup undecorate(ConnectionGroup object) {
                if (object instanceof ApprovalConnectionGroup)
                    return ((ApprovalConnectionGroup) object).getUndecorated();
                return object;
            }
            
        };
    }
    
    /**
     * For the given Approvable object, determine if the current user can approve
     * access to this connection for another user's request.
     * 
     * @param approvable
     *     The Approvable object to check for approver access.
     * 
     * @return
     *     true if the current user is allowed to approve this request, otherwise
     *     false.
     * 
     * @throws GuacamoleException 
     *     If permissions cannot be retrieved for the current user, or if an
     *     unknown type of Approvable object is provided to the method.
     */
    public boolean isApprover(Approvable approvable) throws GuacamoleException {
        
        Permissions myPermissions = this.self().getEffectivePermissions();
        if (myPermissions.getSystemPermissions().hasPermission(SystemPermission.Type.ADMINISTER))
                    return true;
        
        if (approvable instanceof Connection)
            return myPermissions.getConnectionPermissions().hasPermission(ObjectPermission.Type.ADMINISTER, approvable.getIdentifier());
        
        else if (approvable instanceof ConnectionGroup)
            return myPermissions.getConnectionGroupPermissions().hasPermission(ObjectPermission.Type.ADMINISTER, approvable.getIdentifier());
        
        throw new GuacamoleSecurityException("Cannot retrieve permissions for unknown object class.");
    }
    
    /**
     * Return the entire set of all approval requests that the currently logged-
     * in user can approve.
     * 
     * @return
     *     A map containing all of the approval requests for which this user is
     *     an approver.
     * 
     * @throws GuacamoleException
     *     If an error occurs retrieving the user's permissions.
     */
    public Map<UUID, ApprovalRequest> getMyApprovals() throws GuacamoleException {
        Map<UUID, ApprovalRequest> myApprovals = new HashMap<>();       

        for (Map.Entry<UUID, ApprovalRequest> entry : approvalRequests.getAll().entrySet()) {
            // Skip requests that belong to this user.
            if (entry.getValue().getRequestingUser().equals(this.self()))
                continue;
            
            // Check the resource for approval permissions.
            if (isApprover(entry.getValue().getResource()))
                myApprovals.put(entry.getKey(), entry.getValue());
        }

        return Collections.unmodifiableMap(myApprovals);
    }
    
    /**
     * Get a map of all requests that belong to the currently logged-in user.
     * 
     * @return
     *     A map of all requests that belong to the currently logged-in user. 
     */
    public Map<UUID, ApprovalRequest> getMyRequests() {
        Map<UUID, ApprovalRequest> myRequests = new HashMap<>();

        approvalRequests.getAll().forEach((key,value) -> {
            if (value.getRequestingUser().equals(this.self()))
                myRequests.put(key, value);
            });

        return Collections.unmodifiableMap(myRequests);
    }
    
    public ApprovalRequest getRequest(UUID requestUUID) throws GuacamoleException {
        
        ApprovalRequest approvalRequest = approvalRequests.get(requestUUID);
        
        if (approvalRequest != null && (isApprover(approvalRequest.getResource()) || approvalRequest.getRequestingUser().equals(self())))
            return approvalRequest;
        
        throw new GuacamoleSecurityException("The specified request is invalid or not allowed.");
        
    }
    
    /**
     * Request access to the specfied Approvable object, returning the UUID of
     * the request upon success.
     * 
     * @param approvable
     *     The Approvable object to which the current user is requesting access.
     * 
     * @param duration
     *     The duration for which access is being requested, in minutes.
     * 
     * @return 
     *     The UUID of the new approval request.
     */
    public UUID requestConnect(Approvable approvable, Duration duration) {
        ApprovalRequest approvalRequest = new ApprovalRequest(self(), approvable, duration);
        return approvalRequests.add(approvalRequest);
    }
    
    /**
     * Approve the request having the specified UUID, with the given duration
     * and comment.
     * 
     * @param requestUUID
     *     The UUID of the request to approve.
     * 
     * @param duration
     *     The duration for which to approve the request, which may be different
     *     from the user's requested duration.
     * 
     * @param comment
     *     An arbitrary comment to provide with the approval.
     * 
     * @return
     *     The UUID of the approved request.
     * 
     * @throws GuacamoleException 
     *     If the current user is not an approver for this request, the request
     *     is expired, or the request is not in a pending state.
     */
    public UUID approveRequest(UUID requestUUID, Duration duration, String comment) throws GuacamoleException {
        ApprovalRequest approvalRequest = approvalRequests.get(requestUUID);
        
        if (!approvalRequest.isPending())
            throw new GuacamoleSecurityException("Request is not in a pending state.");
        
        if (approvalRequest.isApprovalExpired())
            throw new GuacamoleSecurityException("The request has expired.");
        
        if (!isApprover(approvalRequest.getResource()))
            throw new GuacamoleSecurityException("You are not authorized to approve this request.");
        
        approvalRequest.approve(self(), duration, comment);
        return approvalRequest.getUUID();
            
    }
    
    /**
     * Approve the request specified by the provided UUID, accepting the duration
     * requested by the user, and providing no additional comment.
     * 
     * @param requestUUID
     *     The UUID of the request to approve.
     * 
     * @return
     *     The UUID of the approved request.
     * 
     * @throws GuacamoleException 
     *     If the current user is not an approver for this request, the request
     *     is expired, or the request is not in a pending state.
     */
    public UUID approveRequest(UUID requestUUID) throws GuacamoleException {
        ApprovalRequest approvalRequest = approvalRequests.get(requestUUID);
        return this.approveRequest(requestUUID, approvalRequest.getRequestedDuration(), null);
    }
    
    /**
     * Approve the request specified by the provided UUID, accepting the requested
     * duration and providing the comment.
     * 
     * @param requestUUID
     *     The UUID of the request to approve.
     * 
     * @param comment
     *     The comment to leave with the approval.
     * 
     * @return
     *     The UUID of the approved request.
     * 
     * @throws GuacamoleException 
     *     If the current user is not an approver for this request, the request
     *     is expired, or the request is not in a pending state.
     */
    public UUID approveRequest(UUID requestUUID, String comment) throws GuacamoleException {
        ApprovalRequest approvalRequest = approvalRequests.get(requestUUID);
        return this.approveRequest(requestUUID, approvalRequest.getRequestedDuration(), comment);
    }
    
    /**
     * For the request having the provided UUID, deny the request, providing an
     * arbitrary comment with information regarding the reason the request has
     * been denied.
     * 
     * @param requestUUID
     *     The UUID of the request to deny.
     * 
     * @param comment
     *     The arbitrary comment to leave explaining the reason for the denial.
     * 
     * @return
     *     The UUID of the denied request.
     * 
     * @throws GuacamoleException 
     *     If the request is not in a pending state, has expired, or the current
     *     user is not authorized to deny the request.
     */
    public UUID denyRequest(UUID requestUUID, String comment) throws GuacamoleException {
        ApprovalRequest approvalRequest = approvalRequests.get(requestUUID);
        
        if (!approvalRequest.isPending())
            throw new GuacamoleSecurityException("Request is not in pending state.");
        
        if (approvalRequest.isApprovalExpired())
            throw new GuacamoleSecurityException("Request has expired.");
        
        if (!isApprover(approvalRequest.getResource()))
            throw new GuacamoleSecurityException("User is not authorized to deny request.");
            
        approvalRequest.deny(self(), comment);
        
        return approvalRequest.getUUID();
        
    }
    
    /**
     * For the request having the provided UUID, cancel the request.
     * 
     * @param requestUUID
     *     The UUID of the request to cancel.
     * 
     * @return
     *     The UUID of the canceled request.
     * 
     * @throws GuacamoleException 
     *     If the request was not initiated by the current user, is not in a
     *     pending state, or has expired.
     */
    public UUID cancelRequest(UUID requestUUID) throws GuacamoleException {
        ApprovalRequest approvalRequest = approvalRequests.get(requestUUID);
        
        if (!approvalRequest.getRequestingUser().equals(self()))
            throw new GuacamoleSecurityException("User can only cancel their own requests.");
        
        if (!approvalRequest.isPending())
            throw new GuacamoleSecurityException("Request is not in a pending state.");
        
        if (approvalRequest.isApprovalExpired())
            throw new GuacamoleSecurityException("Request has expired.");
        
        approvalRequest.cancel();
        return approvalRequest.getUUID();
        
    }
    
}
