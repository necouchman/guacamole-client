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

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.auth.approval.ApprovalRequest;
import org.apache.guacamole.auth.approval.connection.ApprovalConnection;
import org.apache.guacamole.auth.approval.connectiongroup.ApprovalConnectionGroup;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.ConnectionGroup;

/**
 * A REST API resource that provides functionality for creating and managing
 * approval requests.
 */
@Produces(MediaType.APPLICATION_JSON)
public class ApprovalUserResource {
    
    /**
     * The UserContext of the currently logged-in user.
     */
    private final ApprovalUserContext userContext;
    
    /**
     * Create a new REST API resource that will be used to manage requests
     * for connections and approvals.
     * 
     * @param userContext 
     *     The UserContext of the currently logged-in user.
     */
    public ApprovalUserResource(ApprovalUserContext userContext) {
        this.userContext = userContext;
    }
    
    /**
     * Create a new request for the connection with the given identifier,
     * and optionally the specified duration. If the duration is left empty or
     * non-positive, the default duration will be used.
     * 
     * @param identifier
     *     The identifier of the connection to request access to.
     * 
     * @param duration
     *     The duration, in minutes, for which the approval should be good.
     * 
     * @return 
     *     The UUID of the request.
     * 
     * @throws GuacamoleException
     *     If an error occurs requesting access to the connection.
     */
    @POST
    @Path("connection/{identifier}")
    public String requestConnection(@PathParam("identifier") String identifier,
            @FormParam("duration") long duration) throws GuacamoleException {
        
        // Get the resource with the specified identifier
        Connection requestedConnection = userContext.getConnectionDirectory().get(identifier);
        
        Duration requestDuration = ApprovalRequest.DEFAULT_DURATION;
        
        if (duration > 0)
            requestDuration = Duration.ofMinutes(duration);
        
        if (requestedConnection instanceof ApprovalConnection)
            return userContext.requestConnect((ApprovalConnection)requestedConnection, requestDuration).toString();
        
        throw new GuacamoleServerException("Error requesting connection.");

    }
    
    /**
     * Create a new request or the connection group with the given identifier,
     * and optionally the specified duration. If the duration is left empty or
     * non-positive, the default duration will be used.
     * 
     * @param identifier
     *     The identifier of the connection group to request access to.
     * 
     * @param duration
     *     The duration, in minutes, for which the approval should be good.
     * 
     * @return 
     *     The UUID of the request.
     * 
     * @throws GuacamoleException
     *     If an error occurs requesting access to the connection group.
     */
    @POST
    @Path("connectionGroup/{identifier}")
    public String requestConnectionGroup(@PathParam("identifier") String identifier,
            @FormParam("duration") long duration) throws GuacamoleException {
        
        // Get the resource with the specified identifier
        ConnectionGroup requestedConnection = userContext.getConnectionGroupDirectory().get(identifier);
        
        Duration requestDuration = ApprovalRequest.DEFAULT_DURATION;
        
        if (duration > 0)
            requestDuration = Duration.ofMinutes(duration);
        
        if (requestedConnection instanceof ApprovalConnectionGroup)
            return userContext.requestConnect((ApprovalConnectionGroup)requestedConnection, requestDuration).toString();
        
        throw new GuacamoleServerException("Error requesting ConnectionGroup.");
    }
    
    /**
     * Mark the specified request as approved, with the optional duration. If
     * the duration is non-positive, then the duration requested by the user
     * will be approved.
     * 
     * @param uuid
     *     The UUID of the request to approve.
     * 
     * @param duration
     *     The duration, in minutes, for which the approval is granted.
     * 
     * @param comment
     *     A comment associated with the approval that will be recorded.
     * 
     * @return 
     *     The UUID of the approved request.
     * 
     * @throws GuacamoleException
     *     If an error occurs marking a request as approved.
     */
    @POST
    @Path("{uuid}/approve")
    public String approveRequest(@PathParam("uuid") String uuid,
            @FormParam("duration") long duration,
            @FormParam("comment") String comment)
            throws GuacamoleException {
        
        return userContext
                .approveRequest(
                        UUID.fromString(uuid),
                        Duration.ofMinutes(duration),
                        comment)
                .toString();
        
    }
    
    /**
     * Mark the specified request as denied, with the optional comment providing
     * a reason for the denial.
     * 
     * @param uuid
     *     The UUID of the request to deny.
     * 
     * @param comment
     *     A comment explaining why the request was denied.
     * 
     * @return 
     *     The UUID of the denied request.
     * 
     * @throws GuacamoleException
     *     If an error occurs marking a request as denied.
     */
    @POST
    @Path("{uuid}/deny")
    public String denyRequest(@PathParam("uuid") String uuid,
            @FormParam("comment") String comment) throws GuacamoleException {
        
        return userContext.denyRequest(UUID.fromString(uuid), comment).toString();
        
    }
    
    /**
     * Cancel the specified request.
     * 
     * @param uuid
     *     The UUID of the request to cancel.
     * 
     * @return 
     *     The UUID of the canceled request.
     * 
     * @throws GuacamoleException
     *     If an error occurs deleting the request.
     */
    @DELETE
    @Path("{uuid}")
    public String deleteRequest(@PathParam("uuid") String uuid)
            throws GuacamoleException  {

        return userContext.cancelRequest(UUID.fromString(uuid)).toString();
        
    }
    
    /**
     * Retrieve the request with the specified UUID.
     * 
     * @param uuid
     *     The UUID of the request to retrieve.
     * 
     * @return 
     *     The request with the given UUID.
     * 
     * @throws GuacamoleException
     *     If an error occurs retrieving the request.
     */
    @GET
    @Path("{uuid}")
    public ApprovalRequest getRequest(@PathParam("uuid") String uuid)
            throws GuacamoleException {
        return userContext.getRequest(UUID.fromString(uuid));
    }
    
    /**
     * Retrieve a list of requests that the current user has entered.
     * 
     * @return 
     *     The list of requests that the current user has entered.
     * 
     * @throws GuacamoleException
     *     If an error occurs retrieving current user's requests.
     */
    @GET
    @Path("requests")
    public Map<UUID, ApprovalRequest> getRequests() throws GuacamoleException {
        return userContext.getMyRequests();
    }
    
    /**
     * Retrieve a list of requests that the current user is authorized to
     * approve.
     * 
     * @return 
     *     The list of requests that the current user is authorized to approve.
     * 
     * @throws GuacamoleException
     *     If an error occurs retrieving approvals.
     */
    @GET
    @Path("/approvals")
    public Map<UUID, ApprovalRequest> getApprovals() throws GuacamoleException {
        return userContext.getMyApprovals();
    }
    
}
