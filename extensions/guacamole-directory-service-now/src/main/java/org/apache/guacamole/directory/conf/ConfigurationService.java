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

package org.apache.guacamole.directory.conf;

import com.google.inject.Inject;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.StringGuacamoleProperty;

/**
 * Global configuration options for the Service Now directory module.
 */
public class ConfigurationService {
    
    /**
     * The environment of the Guacamole Server.
     */ 
    @Inject
    private Environment environment;
    
    /**
     * The property used to configure the Service Now instance name that will
     * be queried.
     */
    public static final StringGuacamoleProperty SERVICE_NOW_INSTANCE_NAME = new StringGuacamoleProperty() {
        
        @Override
        public String getName() { return "service-now-instance-name"; }
        
    };
    
    /**
     * The property used to configure the username for authentication to the
     * Service Now instance.
     */
    public static final StringGuacamoleProperty SERVICE_NOW_USERNAME = new StringGuacamoleProperty() {
        
        @Override
        public String getName() { return "service-now-username"; }
        
    };
    
    /**
     * The property used to configure the password for authentication to the
     * Service Now instance.
     */
    public static final StringGuacamoleProperty SERVICE_NOW_PASSWORD = new StringGuacamoleProperty() {
        
        @Override
        public String getName() { return "service-now-password"; }
        
    };
    
    /**
     * Return the Service Now instance name that should be queried, or throw an
     * exception if this required property is not present or the configuration
     * file cannot be read.
     * 
     * @return
     *     The Service Now instance name as configured in guacamole.properties.
     * 
     * @throws GuacamoleException 
     *     If the configuration file cannot be read or the property is not
     *     present.
     */
    public String getServiceNowInstanceName() throws GuacamoleException {
        return environment.getRequiredProperty(SERVICE_NOW_INSTANCE_NAME);
    }
    
    /**
     * Return the username that should be used to authenticate to the Service Now
     * instance, or throw an exception if this required property is not present
     * or the configuration file cannot be read.
     * 
     * @return
     *     The username to authenticate to the Service Now instance, as
     *     configured in guacamole.properties.
     * 
     * @throws GuacamoleException 
     *     If the configuration file cannot be read or the property is not
     *     present.
     */
    public String getServiceNowUsername() throws GuacamoleException {
        return environment.getRequiredProperty(SERVICE_NOW_USERNAME);
    }
    
    
    /**
     * Return the password that should be used to authenticate to the Service Now
     * instance, or throw an exception if this required property is not present
     * or the configuration file cannot be read.
     * 
     * @return
     *     The password to authenticate to the Service Now instance, as
     *     configured in guacamole.properties.
     * 
     * @throws GuacamoleException 
     *     If the configuration file cannot be read or the property is not
     *     present.
     */
    public String getServiceNowPassword() throws GuacamoleException {
        return environment.getRequiredProperty(SERVICE_NOW_PASSWORD);
    }
    
}
