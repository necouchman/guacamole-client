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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * DefaultsConfiguration implementation that is annotated for deserialization by
 * Jackson.
 */
public class JacksonDefaultsConfiguration implements DefaultsConfiguration {

    /**
     * The list that will be used to collect the defaults that can apply to
     * all connections accessed through this instance of Guacamole.
     */
    @JsonProperty("all")
    private List<DefaultConnectionParameter> allDefaults;
    
    /**
     * The array that will be used to collect per-protocol defaults for this
     * instance of Guacamole, where the key is a Guacamole-supported protocol
     * and the value is a list of default values for that protocol.
     */
    @JsonProperty("protocols")
    private Map<String, List<DefaultConnectionParameter>> protocolDefaults;
    
    /**
     * The array that will be used to store template names and lists of defaults
     * that are part of that template, where the key is the template name and
     * the value is the list of connection parameters.
     */
    @JsonProperty("templates")
    private Map<String, List<DefaultConnectionParameter>> templateDefaults;
    
    @Override
    public List<DefaultConnectionParameter> getAllDefaultConnectionParameters() {
        return allDefaults;
    }
    
    @Override
    public Map<String, List<DefaultConnectionParameter>> getProtocolDefaultConnectionParameters() {
        return protocolDefaults;
    }
    
    @Override
    public List<DefaultConnectionParameter> getProtocolDefaultConnectionParameters(String protocol) {
        return protocolDefaults.get(protocol);
    }
    
    @Override
    public Map<String, List<DefaultConnectionParameter>> getTemplateDefaultConnectionParameters() {
        return templateDefaults;
    }
    
    @Override
    public List<DefaultConnectionParameter> getTemplateDefaultConnectionParameters(String template) {
        return templateDefaults.get(template);
    }

}
