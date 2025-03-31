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

package org.apache.guacamole.auth.defaults.connection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import java.util.List;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.defaults.conf.ConfigurationService;
import org.apache.guacamole.auth.defaults.conf.DefaultConnectionParameter;
import org.apache.guacamole.auth.defaults.conf.DefaultsConfiguration;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.DelegatingConnection;
import org.apache.guacamole.protocol.GuacamoleClientInformation;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Connection implementation that pulls defaults configured by this extension
 * and either adds them or overrides the configured options.
 */
public class DefaultsConnection extends DelegatingConnection {
    
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsConnection.class);
    
    @Inject
    private ConfigurationService configurationService;
    
    /**
     * Create a new DefaultsConnection which delegates most of its implementation
     * to an underlying Connection implementation, but is able to read default
     * connection parameters from a configuration file and apply those parameters
     * to the connections.
     * 
     * @param connection 
     *     The connection which this implementation decorates, delegating most
     *     functionality to this Connection.
     */
    @AssistedInject
    public DefaultsConnection(@Assisted Connection connection) {
        super(connection);
    }
    
    /**
     * Return a copy of the underlying connection to which this connection
     * delegates its functionality.
     * 
     * @return 
     *     The underlying Connection to which this Connection delegates its
     *     functionality.
     */
    public Connection getUndecorated() {
        return this.getDelegateConnection();
    }
    
    @Override
    public GuacamoleConfiguration getConfiguration() {
        
        // Pull the original configuration and parameters.
        GuacamoleConfiguration config = super.getConfiguration();
        Map<String, String> parameters = config.getParameters();
        
        try {
            // Retrieve defaults from configuration service
            DefaultsConfiguration defaultsConfig = configurationService.getDefaultsConfiguration();

            // If there are no defaults, just bail out, now.
            if (defaultsConfig == null)
                return config;
            
            // Get defaults for all connections and the protocol of this connection
            List<DefaultConnectionParameter> allDefaults = defaultsConfig.getAllDefaultConnectionParameters();
            List<DefaultConnectionParameter> protocolDefaults = defaultsConfig.getProtocolDefaultConnectionParameters(config.getProtocol());
            
            // If the defaults configurations are empty, just return the config.
            if ((allDefaults == null || allDefaults.size() <= 0) && (protocolDefaults == null || protocolDefaults.size() <= 0))
                return config;

            // Loop provided defaults for all connections.
            for (DefaultConnectionParameter defaultParameter : allDefaults) {
                // If the parameter is not in the configuration, or the override option is set,
                // add/replace the configuration with the default value.
                if (!parameters.containsKey(defaultParameter.getName()) || defaultParameter.getOverride()) {
                    LOGGER.debug(">>> DEFAULTS: Setting or overriding parameter \"{}\"", defaultParameter.getName());
                    parameters.put(defaultParameter.getName(), defaultParameter.getValue());
                }
                else {
                    LOGGER.debug(">>> DEFAULTS: Parameter already exists, not overriding.");
                }
            }

            // Loop provided defaults for this protocol
            for (DefaultConnectionParameter defaultParameter : protocolDefaults) {
                // If the parameter is not in the configuration, or the override option is set,
                // add/replace the configuration with the default value.
                if (!parameters.containsKey(defaultParameter.getName()) || defaultParameter.getOverride()) {
                    LOGGER.debug(">>> DEFAULTS: Setting or overriding parameter \"{}\"", defaultParameter.getName());
                    parameters.put(defaultParameter.getName(), defaultParameter.getValue());
                }
                else {
                    LOGGER.debug(">>> DEFAULTS: Parameter already exists, not overriding.");
                }
            }

            // Apply the updated parameters to the configuration.
            config.setParameters(parameters);
            
        } catch (GuacamoleException e) {
            // If an exception occurs, we warn but continue.
            LOGGER.warn(">>> DEFAULTS: Failed to retrieve defaults configuration: \"{}\". Default settings will not be applied to this connection.", e.getLocalizedMessage());
            LOGGER.debug(">>> DEFAULTS: Exception while retrieving defaults from configuration service.", e);
        }
        
        // Return the (possibly update) config.
        return config;
        
    }
    
    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info,
            Map<String, String> tokens) throws GuacamoleException {
        
        // Get the config and pass it up to the parent object.
        super.setConfiguration(this.getConfiguration());
        
        // Connect using the parent object
        return super.connect(info, tokens);
        
    }
    
}
