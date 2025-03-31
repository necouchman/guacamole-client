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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class that implements the fields and methods to create a default connection
 * parameter for a Guacamole connection.
 */
public class DefaultConnectionParameter {
    
    /**
     * A String which stores the name of the connection parameter.
     */
    @JsonProperty("name")
    private final String name;
    
    /**
     * A String which stores the value of the connection parameter.
     */
    @JsonProperty("value")
    private final String value;
    
    /**
     * true if the connection parameter should override or replace values
     * specified in the connections, otherwise false.
     */
    @JsonProperty("override")
    private boolean override = false;
    
    /**
     * Create a new default connection parameter with the provided name, value,
     * and boolean value indicating whether or not the value provided by this
     * default parameter should forcibly override or replace values provided by
     * the individual connection configurations.
     * 
     * @param override 
     *     true if this value should override or replace values provided by
     *     individual connection configurations, otherwise false.
     * 
     * @param name
     *     The name of the parameter for which to provide a default.
     * 
     * @param value
     *     The default value of the connection parameter.
     * 
     */
    @JsonCreator
    public DefaultConnectionParameter(@JsonProperty("override") boolean override,
            @JsonProperty("name") String name,
            @JsonProperty("value") String value) {
        this.override = override;
        this.name = name;
        this.value = value;
    }
    
    /**
     * Return the name of the connection parameter.
     * 
     * @return
     *     The name of the connection parameter.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Return the value of the connection parameter.
     * 
     * @return 
     *     The String value of the connection parameter.
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     * Return whether or not the value provided by this default should forcibly
     * override the value provided by individual connection configurations.
     * 
     * @return 
     *     true if the value of this default should override the individual
     *     connection configurations, otherwise false.
     */
    public boolean getOverride() {
        return this.override;
    }
    
}
