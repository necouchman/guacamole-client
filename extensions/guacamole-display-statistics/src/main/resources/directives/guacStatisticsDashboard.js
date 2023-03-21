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
 * A directive which displays connection statistics for the Guacamole Client
 * instance.
 */
angular.module('home').directive('guacStatisticsDashboard', function guacStatisticsDashboard() {
    
    const directive = {
        restrict: 'E',
        templateUrl: 'app/ext/display-stats/templates/guacStatisticsDashboard.html',
    };

    directive.scope = {};

    directive.controller = ['$scope', '$injector', function guacStatisticsDashboardController($scope, $injector) {
            
        // Required services
        const activeConnectionService = $injector.get('activeConnectionService');
        const authenticationService   = $injector.get('authenticationService');
        
        var activeConnections = [];

        
        var dataSources = authenticationService.getAvailableDatsources();
        angular.forEach(dataSources, function getActiveConnectionsPerDatasource(datasource) {
            activeConnectionService.getActiveConnections(datasource, null)
                    .then(function retrievedActiveConnections(datasourceConnections) {
                        activeConnections.push(datasourceConnections);
                    });

        });
        
        $scope.chartData = {
            labels: dataSources,
            datasets: [{
                label: 'Active Connections',
                data: activeConnections,
            }]
        };
        
        $scope.chartConfig = {
            type: 'doughnut',
            data: $scope.chartData
        };
            
    }];

    return directive;

});

