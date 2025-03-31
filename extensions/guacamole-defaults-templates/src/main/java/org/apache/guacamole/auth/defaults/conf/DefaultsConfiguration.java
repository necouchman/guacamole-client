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

package org.apache.guacamole.auth.defaults.conf;

import java.util.List;
import java.util.Map;

/**
 * An interface that defines how default configuration implementations behave.
 */
public interface DefaultsConfiguration {
    
    /**
     * Return the default connection parameters that are configured to apply
     * to all connections of this Guacamole install.
     * 
     * @return
     *     A List of the default connection parameters that are configured
     *     to apply to all connections on this system.
     */
    List<DefaultConnectionParameter> getAllDefaultConnectionParameters();
    
    /**
     * Return a Map of protocols and default connection parameters that are
     * configured for each protocol.
     * 
     * @return 
     *     A Map of protocol names and the list of configured default connection
     *     parameters for each protocol.
     */
    Map<String, List<DefaultConnectionParameter>> getProtocolDefaultConnectionParameters();
    
    /**
     * Return a List of default connection parameters configured for the provided
     * protocol.
     * 
     * @param protocol
     *     The protocol for which to return the configured default connection
     *     parameters.
     * 
     * @return 
     *     A List of default connection parameters for the specified protocol.
     */
    List<DefaultConnectionParameter> getProtocolDefaultConnectionParameters(String protocol);
    
    /**
     * Return a Map of template names and the default connection parameters
     * configured for each template.
     * 
     * @return 
     *     A Map of template names and the List of default connection parameters
     *     configured for that template.
     */
    Map<String, List<DefaultConnectionParameter>> getTemplateDefaultConnectionParameters();
    
    /**
     * The List of default connection parameters configured for the specified
     * template.
     * 
     * @param template
     *     The name of the template for which to gather the default connection
     *     parameters.
     * 
     * @return 
     *     The List of default connection parameters configured for the specified
     *     template.
     */
    List<DefaultConnectionParameter> getTemplateDefaultConnectionParameters(String template);
    
}
