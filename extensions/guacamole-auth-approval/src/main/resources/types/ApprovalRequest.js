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

/**
 * Service which defines the ApprovalReqest class.
 */
angular.module('guacApproval').factory('ApprovalRequest', [function defineApprovalRequest() {
        
    var ApprovalRequest = function ApprovalRequest(template) {
        
        // Copy the template, or start with a blank object.
        template = template || {};
        
        /**
         * The time stamp at which this request was entered, in seconds since
         * 1970-01-01 00:00:00 UTC.
         * 
         * @type Number
         */
        this.requestTimestamp = template.requestTimestamp;
        
        /**
         * The user who entered the request.
         * 
         * @type String
         */
        this.requestingUser = template.requestingUser;
        
        /**
         * The duration requested by the user, in minutes.
         * 
         * @type Number
         */
        this.requestedDuration = template.requestedDuration;
        
        /**
         * The resource requested.
         * 
         * @type Object
         */
        this.requestedResource = template.requestedResource;
        
        /**
         * The status of the request.
         * 
         * @type Enum
         */
        this.requestStatus = template.requestStatus;
        
        /**
         * The user who made an approval decision on the request.
         * 
         * @type String
         */
        this.decisionUser = template.decisionUser;
        
        /**
         * The time stamp at which a decision was entered on the request, in
         * seconds since 1970-01-01 00:00:00 UTC.
         * 
         * @type Number
         */
        this.decisionTimestamp = template.decisionTimestamp;
        
        /**
         * A optional comment regarding the approval decision for the request.
         * 
         * @type String
         */
        this.decisionComment = template.decisionComment;
        
        /**
         * The time at which the request expires, in seconds since
         * 1970-01-01 00:00:00 UTC.
         * 
         * @type Number
         */
        this.approvalExpiration = template.approvalExpiration;
        
        /**
         * Valid approval request status values.
         */
        ApprovalRequest.ApprovalRequestStatus = {
            
            /**
             * The request has been approved.
             */
            APPROVED : "APPROVED",
            
            /**
             * The request has been canceled or withdrawn.
             */
            CANCELED : "CANCELED",
            
            /**
             * The request has been denied.
             */
            DENIED : "DENIED",
            
            /**
             * The request is still pending decision.
             */
            PENDING : "PENDING"
            
        };
        
    };
        
}]);