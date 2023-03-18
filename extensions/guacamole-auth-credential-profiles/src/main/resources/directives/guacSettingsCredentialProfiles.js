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
 * A directive for managing preferences local to the current user.
 */
angular.module('guacCredentialProfiles').directive('guacSettingsCredentialProfiles',
        [function guacSettingsCredentialProfiles() {

    return {
        // Element only
        restrict: 'E',
        replace: true,

        scope: {},

        templateUrl : 'app/ext/credprof/templates/guacSettingsCredentialProfiles.html',
        controller: ['$scope', '$injector', function guacSettingsCredentialProfilesController($scope, $injector) {

            // Get required types
            const CredentialProfile = $injector.get('CredentialProfile');
            const PageDefinition    = $injector.get('PageDefinition');

            // Required services
            const $log                  = $injector.get('$log');
            const $routeParams          = $injector.get('$routeParams');
            const $translate            = $injector.get('$translate');
            const authenticationService = $injector.get('authenticationService');
            const credentialService     = $injector.get('credentialService');
            const userPageService       = $injector.get('userPageService');
            
            /**
             * The array of settings pages available to the current user, or null if
             * not yet known.
             *
             * @type Page[]
             */
            $scope.settingsPages = null;
            
            $scope.credentialProfile = null;
            
            $scope.activeTab = $routeParams.tab;

            /**
             * Returns whether critical data has completed being loaded.
             *
             * @returns {Boolean}
             *     true if enough data has been loaded for the user interface to be
             *     useful, false otherwise.
             */
            $scope.isLoaded = function isLoaded() {

                return $scope.settingsPages      !== null
                    && $scope.credentialProfile  !== null;

            };


            $scope.saveProfile = function saveProfile() {
            
                return credentialService.saveProfile($scope.credentialProfile);
                
            };
            
            // Register this settings page
            userPageService.getSettingsPages()
            .then(function settingsPagesRetrieved(pages) {
                $scope.settingsPages = pages;
                pages.push(new PageDefinition({
                    name: 'SETTINGS_CREDENTIALS.ACTION_CREDENTIAL_SETTINGS_PAGE',
                    url: '/settings/credentials'
                }));
            });
            
            credentialService.getProfile()
            .then(function credentialProfileRetrieved(profile) {
                $scope.credentialProfile = profile;
            });
            
            $log.debug('Loading Credential Settings pages.');

        }]
    };
}]);
