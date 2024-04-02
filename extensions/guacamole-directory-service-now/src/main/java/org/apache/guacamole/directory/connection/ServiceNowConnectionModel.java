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

package org.apache.guacamole.directory.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.directory.util.ServiceNowConnectionProtocol;
import org.apache.guacamole.protocol.GuacamoleConfiguration;

/**
 * A model used to map connection data from Service Now REST APIs to the
 * required properties in Guacamole.
 */
public class ServiceNowConnectionModel {
    
    /**
     * The system identifier of the CMDB Configuration Item from Service Now.
     */
    @JsonProperty("sys_id")
    private String sys_id;
    
    /**
     * The Service Now system class.
     */
    @JsonProperty("sys_class_name")
    private String sys_class_name;
    
    /**
     * The name of the CMDB Configuration Item.
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * The fully-qualified domain name of the CMDB configuration item.
     */
    @JsonProperty("fqdn")
    private String fqdn;
    
    /**
     * The IP address of the CMDB configuration item.
     */
    @JsonProperty("ip_address")
    private String ip_address;
    
    /**
     * Return the Service Now sys_id for the configuration item.
     * 
     * @return 
     *     The sys_id for the configuration item.
     */
    public String getSysId() {
        return sys_id;
    }
    
    /**
     * Set the sys_id for the configuration item.
     * 
     * @param sys_id 
     *     The sys_id for the configuration item.
     */
    public void setSysId(String sys_id) {
        this.sys_id = sys_id;
    }
    
    /**
     * Return the Service Now system class name of the configuration item.
     * 
     * @return 
     *     The system class name of the configuration item.
     */
    public String getSysClassName() {
        return sys_class_name;
    }
    
    /**
     * Set the system class name of the configuration item.
     * 
     * @param sys_class_name 
     *     The system class name to be set for the configuration item.
     */
    public void setSysClassName(String sys_class_name) {
        this.sys_class_name = sys_class_name;
    }
    
    /**
     * Return the name of the configuration item.
     * 
     * @return 
     *     The name of the configuration item.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of the configuration item.
     * 
     * @param name 
     *     The name to be set for the configuration item.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Return the fully-qualified domain name of the configuration item.
     * 
     * @return 
     *     The fully-qualified domain name of the configuration item.
     */
    public String getFqdn() {
        return fqdn;
    }
    
    /**
     * Set the fully-qualified domain name of the configuration item.
     * 
     * @param fqdn 
     *     The fully-qualified domain name to set for the configuration item.
     */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }
    
    /**
     * Return the IP address of the configuration item.
     * 
     * @return 
     *     The IP address of the configuration item.
     */
    public String getIpAddress() {
        return ip_address;
    }
    
    /**
     * Set the IP address of the configuration item.
     * 
     * @param ip_address 
     *     The IP address of the configuration item.
     */
    public void setIpAddress(String ip_address) {
        this.ip_address = ip_address;
    }
    
    /**
     * Generate a GuacamoleConfiguration object for this connection model.
     * 
     * @param protocol
     *     The protocol for which a configuration should be generated.
     * 
     * @return
     *     A GuacamoleConfiguration object associated with this connection model.
     * 
     * @throws GuacamoleException 
     *     If the required data is not available for to generate a complete
     *     configuration.
     */
    public GuacamoleConfiguration generateGuacamoleConfiguration(
            ServiceNowConnectionProtocol protocol) throws GuacamoleException {
        
        GuacamoleConfiguration guacConfig = new GuacamoleConfiguration();
        
        if (sys_id == null || sys_id.isEmpty())
            throw new GuacamoleServerException("Cannot generate a GuacamoleConfiguration with an empty identifier.");
        
        if (sys_class_name == null || sys_class_name.isEmpty())
            throw new GuacamoleServerException("Cannot generate a GuacamoleConfiguration with an unknown class.");
        
        if (fqdn != null && !fqdn.isEmpty())
            guacConfig.setParameter("hostname", fqdn);
        
        else if (ip_address != null && !ip_address.isEmpty())
            guacConfig.setParameter("hostname", ip_address);
        
        else
            throw new GuacamoleServerException("Cannot generate a GuacamoleConfiguration without a hostname or IP address.");

        guacConfig.setProtocol(protocol.name().toLowerCase());
        guacConfig.setParameter("port", Integer.toString(protocol.getPort()));

        return guacConfig;
        
    }
    
}
