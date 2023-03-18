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
 * Service for managing credentials through the REST API.
 */
angular.module('guacCredentialProfiles').factory('credentialService', ['$injector',
        function credentialService($injector) {

    // Required services
    var requestService        = $injector.get('requestService');
    var authenticationService = $injector.get('authenticationService');
    var cacheService          = $injector.get('cacheService');
            
    var service = {};
    
    service.getProfile = function getProfile() {
        
        var httpParameters = {};
        
        return requestService({
            method : 'GET',
            url    : 'api/session/ext/credprof/profile',
            params : httpParameters
        });
        
    };
    
    service.saveProfile = function saveProfile(credentialProfile) {
        
        var httpParameters = {};
        
        return requestService({
            method  : 'POST',
            url     : 'api/session/ext/credprof/profile',
            params  : httpParameters,
            data    : credentialProfile,
            headers : {'Content-Type': 'application/x-www-form-urlencoded'}
        });
        
    };
    
    service.deleteProfile = function deleteProfile(credentialProfile) {
        
        var httpParameters = {};
        
        return requestService({
            method  : 'DELETE',
            url     : '/api/session/ext/credprof/profile',
            params  : httpParameters
        });
        
    };
   
    return service;

}]);
