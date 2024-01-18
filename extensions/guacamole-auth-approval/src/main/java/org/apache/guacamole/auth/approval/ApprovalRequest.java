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

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.apache.guacamole.net.auth.User;

/**
 * A class that represents a request submitted by a user to get approval to
 * access a connection or connection group.
 */
public class ApprovalRequest {
    
    /**
     * A custom data type that represents the overall status of an approval
     * request.
     */
    public enum ApprovalRequestStatus {
        
        /**
         * The request has been approved.
         */
        APPROVED,
        
        /**
         * The request has been canceled or withdrawn.
         */
        CANCELED,
        
        /**
         * The request has been denied.
         */
        DENIED,
        
        /**
         * The request is still pending.
         */
        PENDING;
        
    }
    
    /**
     * The UUID of this approval request.
     */
    private final UUID uuid;
    
    /**
     * The timestamp when the request was entered.
     */
    private final Instant requestTimestamp;
    
    /**
     * The user who is requesting the resource.
     */
    private final User requestingUser;
    
    /**
     * The duration which the user has requested approval for access to the resource.
     */
    private final Duration requestedDuration;
    
    /**
     * The object that is being requested.
     */
    private final Approvable resource;
    
    /**
     * Whether or not the request has been approved.
     */
    private ApprovalRequestStatus requestStatus = ApprovalRequestStatus.PENDING;
    
    /**
     * The duration for which the request has been approved.
     */
    private Duration approvedDuration = null;
    
    /**
     * The time at which the decision to approve or deny the request was made.
     */
    private Instant decisionTimestamp = null;
    
    /**
     * The user who has approved the request.
     */
    private User decisionUser = null;
    
    /**
     * Any comment about the decision, optionally provided by the deciding user,
     * usually when the request is denied.
     */
    private String decisionComment = "";
    
    /**
     * The default duration for which the approval should be good if one
     * is not specifically requested.
     */
    public static final Duration DEFAULT_DURATION = Duration.ofHours(8);
    
    /**
     * Create a new approval request for the given User, requesting access to the
     * specified resource. This method uses the defaults for timestamp and duration.
     * 
     * @param requestingUser
     *     The user who is requesting access to the resource.
     * 
     * @param requestedResource 
     *     The resource to which the user is requesting access.
     */
    public ApprovalRequest(User requestingUser, Approvable requestedResource) {
        this(requestingUser, requestedResource, DEFAULT_DURATION);
    }
    
    /**
     * Create a new approval request for the given user, requesting access to
     * the specified resource, for the specified duration (in minutes).
     * 
     * @param requestingUser
     *     The user making the request.
     * 
     * @param resource
     *     The Approvable resource to which the user is requesting access.
     * 
     * @param duration 
     *     The duration the user is requesting access for, in minutes.
     */
    public ApprovalRequest(User requestingUser, Approvable resource, Duration duration) {
        this.uuid = UUID.randomUUID();
        this.requestTimestamp = Instant.now();
        this.requestingUser = requestingUser;
        this.resource = resource;
        this.requestedDuration = duration;
    }
    
    /**
     * Return true if the request has been approved, otherwise return false. Note
     * that a request that has *not* been approved is not necessarily denied, it
     * maybe still be awaiting a decision.
     * 
     * @return 
     *     True if the request has been approved, otherwise false.
     */
    public boolean isApproved() {
        return (this.requestStatus == ApprovalRequestStatus.APPROVED);
    }
    
    /**
     * Return true if this request has been canceled or withdrawn.
     * 
     * @return 
     *     True if the request has been canceled, otherwise false.
     */
    public boolean isCanceled() {
        return (this.requestStatus == ApprovalRequestStatus.CANCELED);
    }
    
    /**
     * Return true if the request has been denied, otherwise return false. Note
     * that a request that has *not* been denied may not necessarily be approved,
     * as it could still be pending approval or have been withdrawn by the original
     * requestor.
     * 
     * @return 
     *     True if the request has been denied, otherwise false.
     */
    public boolean isDenied() {
        return (this.requestStatus == ApprovalRequestStatus.DENIED);
    }
    
    /**
     * Return True if this request is still pending a decision, or false if
     * a decision (approve or deny) has already been entered.
     * 
     * @return 
     *     True if the request is still pending, otherwise false.
     */
    public boolean isPending() {
        return (this.requestStatus == ApprovalRequestStatus.PENDING);
    }
    
    /**
     * Mark this request as approved, setting the user who approved it and the
     * duration for which the request is approved.
     * 
     * @param approvingUser
     *     The user who approved the request.
     * 
     * @param approvedDuration 
     *     The duration for which the request has been approved.
     * 
     * @param comment
     *     A string containing any arbitrary comment from the approver.
     */
    public void approve(User approvingUser, Duration approvedDuration, String comment) {
        this.requestStatus = ApprovalRequestStatus.APPROVED;
        this.decisionUser = approvingUser;
        this.decisionTimestamp = Instant.now();
        this.approvedDuration = approvedDuration;
        this.decisionComment = comment;
    }
    
    /**
     * Approve the request with the specified approving user, for the requested
     * duration and with no comment.
     * 
     * @param approvingUser 
     *     The User approving the request.
     */
    public void approve(User approvingUser) {
        this.approve(approvingUser, requestedDuration, null);
    }
    
    /**
     * Mark this request as canceled or withdrawn by the user.
     */
    public void cancel() {
        this.requestStatus = ApprovalRequestStatus.CANCELED;
        this.decisionTimestamp = Instant.now();
    }
    
    /**
     * Mark this request as denied, setting the user who denied it and zeroing
     * out the duration.
     * 
     * @param denyingUser 
     *     The user who denied the request.
     * 
     * @param comment
     *     A comment to explain why the request was denied.
     */
    public void deny(User denyingUser, String comment) {
        this.requestStatus = ApprovalRequestStatus.DENIED;
        this.decisionUser = denyingUser;
        this.approvedDuration = Duration.ZERO;
        this.decisionComment = comment;
    }
    
    /**
     * Return the User who entered the request.
     * 
     * @return 
     *     The User who entered the request.
     */
    public User getRequestingUser() {
        return this.requestingUser;
    }
    
    /**
     * Return the Instant at which the request was entered.
     * 
     * @return 
     *     The Instant at which the request was entered.
     */
    public Instant getRequestTimestamp() {
        return this.requestTimestamp;
    }
    
    /**
     * Return the Duration requested by the User for access to the resource.
     * 
     * @return 
     *     The Duration requested by the User.
     */
    public Duration getRequestedDuration() {
        return this.requestedDuration;
    }
    
    /**
     * Return the resource to which the User requested access.
     * 
     * @return 
     *     The resource to which the User requested access.
     */
    public Approvable getResource() {
        return this.resource;
    }
    
    /**
     * Return the status of this approval request.
     * 
     * @return 
     *     The status of this approval request.
     */
    public ApprovalRequestStatus getRequestStatus() {
        return this.requestStatus;
    }
    
    /**
     * Return the User who made the decision to approve or deny the request.
     * 
     * @return 
     *     The User who made the decision to approve or deny the request.
     */
    public User getDecisionUser() {
        return this.decisionUser;
    }
    
    /**
     * Return the Instant at which the decision was made to approve or deny
     * the request.
     * 
     * @return 
     *     The Instant at which the decision was made to approve or deny the
     *     request.
     */
    public Instant getDecisionTimestamp() {
        return this.decisionTimestamp;
    }
    
    /**
     * Return the Instant at which approval for the request expires.
     * 
     * @return 
     *     The Instant at which approval for the request expires.
     */
    public Instant getApprovalExpiration() {
        
        // If the request has not been approved, return something way in the past.
        if (this.requestStatus != ApprovalRequestStatus.APPROVED)
            return Instant.MIN;
        
        // Return the time the decision was made plus the duration.
        return (this.decisionTimestamp.plus(this.approvedDuration));
    }
    
    /**
     * Return True if this request has expired, otherwise false.
     * 
     * @return 
     *     True if this request has expired, otherwise false.
     */
    public boolean isApprovalExpired() {
        
        // If not approved, just return true.
        if (this.requestStatus != ApprovalRequestStatus.APPROVED)
            return true;
        
        // If there's a decision timestamp, compare it to now.
        if (this.decisionTimestamp != null)
            return (Instant.now().compareTo((this.decisionTimestamp.plus(approvedDuration))) > 0);
        
        // By default, return true to be safe.
        return true;
    }
    
    /**
     * Return the comment left by the decision-maker.
     * 
     * @return 
     *     The comment left by the decision-maker.
     */
    public String getDecisionComment() {
        return decisionComment;
    }
    
    /**
     * Return the UUID of the approval request.
     * 
     * @return 
     *     The UUID of the approval request.
     */
    public UUID getUUID() {
        return uuid;
    }
    
}
