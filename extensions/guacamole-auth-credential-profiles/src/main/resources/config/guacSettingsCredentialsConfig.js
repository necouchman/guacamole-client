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
 * Config block which registers Duo-specific field types.
 */
angular.module('guacCredentialProfiles').config(['$routeProvider',
        function guacCredentialProfilesConfig($routeProvider) {

    /**
     * Attempts to re-authenticate with the Guacamole server, sending any
     * query parameters in the URL, along with the current auth token, and
     * updating locally stored token if necessary.
     *
     * @param {Service} $injector
     *     The Angular $injector service.
     * 
     * @returns {Promise}
     *     A promise which resolves successfully only after an attempt to
     *     re-authenticate has been made. If the authentication attempt fails,
     *     the promise will be rejected.
     */
    var updateCurrentToken = ['$injector', function updateCurrentToken($injector) {

        // Required services
        var $location             = $injector.get('$location');
        var authenticationService = $injector.get('authenticationService');

        // Re-authenticate including any parameters in URL
        return authenticationService.updateCurrentToken($location.search());

    }];

    console.log('Configuration credential profile route...');

    $routeProvider.when('/settings/credentials', {
        title         : 'SETTINGS_CREDENTIALS.SECTION_HEADER_CREDENTIAL_MANAGEMENT',
        bodyClassName : 'settings',
        templateUrl   : 'app/ext/credprof/templates/guacSettingsCredentialProfiles.html',
        controller    : 'guacSettingsCredentialProfilesController',
        resolve       : { updateCurrentToken: updateCurrentToken }
    });

}]);
