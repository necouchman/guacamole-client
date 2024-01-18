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
 * Service for managing approval requests.
 */
angular.module('guacApproval').factory('approvalService', ['$injector',
        function approvalService($injector) {

    // Required services
    var authenticationService = $injector.get('authenticationService');
    var cacheService          = $injector.get('cacheService');

    // Empty object to store this service.
    var service = {};
    
    /**
     * Send a REST API request to request approval for the current user to
     * access the connection specified by the identifier, for the optional
     * duration, returning a promise that will contain the UUID of the request
     * upon successful creation.
     * 
     * @param {String} identifier
     *     The connection identifier of the connection to request access to.
     *     
     * @param {long} duration
     *     The requested duration, if different from the default.
     *     
     * @returns {Promise.<String>}
     *     A promise that will resolve with the UUID of the newly-created
     *     Approval Request for the specified connection.
     */
    service.requestConnection = function requestConnection(identifier, duration) {
        
        return authenticationService.request({
            method  : 'POST',
            url     : 'api/session/ext/approval/connection/' + encodeURIComponent(identifier),
            data    : {duration: duration}
        })
        .then(function connectionRequested(requestUuid) {
            // Clear connections and users from cache.
            cacheService.requests.removeAll();
            cacheService.approvals.removeAll();

            // Pass on the connection identifier
            return requestUuid;
        });
        
    };
    
    /**
     * Send a REST API call to the approval extension to request access for the
     * current user to use the specified connection group, returning a promise
     * containing the UUID of the created request.
     * 
     * @param {String} identifier
     *     The identifier of the connection group to request access to.
     *     
     * @param {long} duration
     *     The optional duration in minutes of the request if other than the default.
     *     
     * @returns {Promise.<String>}
     *     A promise that will resolve with the UUID of the newly-created
     *     ApprovalRequest for the specified connection group.
     */
    service.requestConnectionGroup = function requestConnectionGroup(identifier, duration) {

        return authenticationService.request({
            method  : 'POST',
            url     : 'api/session/ext/approval/connectionGroup/' + encodeURIComponent(identifier),
            data    : {duration: duration}
        })
        .then(function connectionRequested(requestUuid) {
            // Clear requests and approvals from cache.
            cacheService.requests.removeAll();
            cacheService.approvals.removeAll();

            // Pass on the request UUID
            return requestUuid;
        });
        
        
    };
    
    /**
     * Approve the request with the specified UUID, returning a promise that
     * contains the UUID of the approved request.
     * 
     * @param {String} uuid
     *     The UUID of the request to approve.
     *     
     * @param {long} duration
     *     The duration of the request, if the requested duration is being
     *     adjusted.
     *     
     * @param {String} comment
     *     An optional comment to leave with the approval.
     *     
     * @returns {Promise.<String>}
     *     A Promise that will resolve with the UUID of the approved request
     *     upon completion.
     */
    service.approveRequest = function approveRequest(uuid, duration, comment) {

        return authenticationService.request({
            method  : 'POST',
            url     : 'api/session/ext/approval/' + encodeURIComponent(uuid) + '/approve',
            data    : {duration: duration, comment: comment}
        })
        .then(function requestApproved(requestUuid) {
            // Clear requests and approvals from cache.
            cacheService.requests.removeAll();
            cacheService.approvals.removeAll();

            // Pass on the request UUID
            return requestUuid;
        });
        
    };
    
    /**
     * Send the REST API request to deny an approval, leaving an optional comment
     * and returning a promise that contains the request UUID upon completion.
     * 
     * @param {String} uuid
     *     The UUID of the request to deny.
     *     
     * @param {String} comment
     *     A comment to leave with the decision.
     *     
     * @returns {Promise.<String>}
     *     A promise that will resolve with the UUID of the 
     */
    service.denyRequest = function denyRequest(uuid, comment) {

        return authenticationService.request({
            method  : 'POST',
            url     : 'api/session/ext/approval/' + encodeURIComponent(uuid) + '/deny',
            data    : {comment: comment}
        })
        .then(function requestDenied(requestUuid) {
            // Clear requests and approvals from cache.
            cacheService.requests.removeAll();
            cacheService.approvals.removeAll();

            // Pass on the request UUID
            return requestUuid;
        });
        
    };
    
    /**
     * Delete the request with the given UUID, returning a promise that contains
     * the UUID of the request upon completion.
     * 
     * @param {String} uuid
     *     The UUID of the request to the delete.
     *     
     * @returns {Promise.<String>}
     *     A promise that will resolve with the UUID of the ApprovalRequest to
     *     delete.
     */
    service.deleteRequest = function deleteRequest(uuid) {

        return authenticationService.request({
            method  : 'DELETE',
            url     : 'api/session/ext/approval/' + encodeURIComponent(uuid)
        })
        .then(function requestDeleted(requestUuid) {
            // Clear requests and approvals from cache.
            cacheService.requests.removeAll();
            cacheService.approvals.removeAll();

            // Pass on the request UUID
            return requestUuid;
        });
        
    };
    
    /**
     * Retrieve the request stored by the provided UUID.
     * 
     * @param {String} uuid
     *     The UUID that identifies the request to be retrieved.
     *     
     * @returns {Promise.<ApprovalRequest>}
     *     A promise that will resolve with the appoval request associated with
     *     the specified UUID.
     */
    service.getRequest = function getRequest(uuid) {

        // Get the specified request.
        return authenticationService.request({
            cache   : cacheService.requests,
            method  : 'GET',
            url     : 'api/session/ext/approval/' + encodeURIComponent(uuid)
        });
        
    };
    
    /**
     * Get all of the requests for the currently logged-in user.
     * 
     * @returns {Promise.<Object.<String, ApprovalRequest>>}
     *     A promise that will resolve with a Map of UUID and ApprovalRequest
     *     objects.
     */
    service.getRequests = function getRequests() {
        
        // Contact REST API and retrieve all requests for the current user.
        return authenticationService.request({
            cache   : cacheService.requests,
            method  : 'GET',
            url     : 'api/session/ext/approval/requests'
        });
        
    };
    
    /**
     * Get all of the pending approvals that the currently logged in user is
     * authorized to approve.
     * 
     * @returns {Promise.<Object.<String, ApprovalRequest>>}
     *     A promise that will resolve with a map of UUID and ApprovalRequest
     *     objects.
     */
    service.getApprovals = function getApprovals() {
        
        // Contact REST API and retrieve all approvals for the current user.
        return authenticationService.request({
            cache   : cacheService.approvals,
            method  : 'GET',
            url     : 'api/session/ext/approval/approvals'
        });
        
    };
    
    return service;
            
}]);