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
package org.apache.guacamole.directory.connectiongroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.directory.connection.ServiceNowConnectionDirectory;
import org.apache.guacamole.directory.connection.ServiceNowConnectionModel;
import org.apache.guacamole.directory.user.ServiceNowUserContext;
import org.apache.guacamole.directory.util.ServiceNowConnectionProtocol;
import org.apache.guacamole.form.BooleanField;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.form.TextField;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.DelegatingConnectionGroup;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.simple.SimpleConnection;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * A connection group that delegates to an underlying group, extending the
 * functionality to support Service Now CMDB integration.
 */
public class ServiceNowConnectionGroup extends DelegatingConnectionGroup {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceNowConnectionGroup.class);
    
    /**
     * The name of the attribute that contains the value indicating whether or
     * not Service Now functionality should be enabled for this connection group.
     */
    public static final String SERVICE_NOW_ENABLED_ATTRIBUTE_NAME = "guac-service-now-enabled";
    
    /**
     * The table to query for the configuration items, if one other than the
     * default (cmdb_ci_server) should be used.
     */
    public static final String SERVICE_NOW_TABLE_ATTRIBUTE_NAME = "guac-service-now-table";
    
    /**
     * The name of the attribute that contains any query filters to apply to the
     * query for the Service Now instance.
     */
    public static final String SERVICE_NOW_QUERY_ATTRIBUTE_NAME = "guac-service-now-query";
    
    /**
     * The name of the attribute that contains the field returned from the query
     * by which results should be grouped into sub-connection-groups, if any.
     */
    public static final String SERVICE_NOW_GROUP_BY_ATTRIBUTE_NAME = "guac-service-now-group-by";
    
    /**
     * The name of the attribute that contains a comma-separated list of Service
     * Now CMDB CI classes that will be given a RDP connection.
     */
    public static final String SERVICE_NOW_RDP_CLASSES_ATTRIBUTE_NAME = "guac-service-now-rdp-classes";
    
    /**
     * The name of the attribute that contains a comma-separated list of Service
     * Now CMDB CI classes that will be given a SSH connection. 
     */
    public static final String SERVICE_NOW_SSH_CLASSES_ATTRIBUTE_NAME = "guac-service-now-ssh-classes";
    
    /**
     * The name of the attribute that contains a comma-separated list of Service
     * Now CMDB CI classes that will be given a Telnet connection.
     */
    public static final String SERVICE_NOW_TELNET_CLASSES_ATTRIBUTE_NAME = "guac-service-now-telnet-classes";
    
    /**
     * The name of the attribute that contains a comma-separated list of Service
     * NOW CMDB CI classes that will be given a VNC connection.
     */
    public static final String SERVICE_NOW_VNC_CLASSES_ATTRIBUTE_NAME = "guac-service-now-vnc-classes";
    
    /**
     * The name of the form that will be used to configure the Service Now
     * attributes for a connection group.
     */
    public static final String SERVICE_NOW_FORM_NAME = "guac-service-now-form";
    
    /**
     * A regular expression for comma-separated lists of items.
     */
    private static final Pattern COMMA_DELIMITED = Pattern.compile(",\\s*");
    
    /**
     * The list of all Service Now-related attributes.
     */
    public static final List<String> SERVICE_NOW_ATTRIBUTE_NAMES = Arrays.asList(
            SERVICE_NOW_ENABLED_ATTRIBUTE_NAME,
            SERVICE_NOW_TABLE_ATTRIBUTE_NAME,
            SERVICE_NOW_QUERY_ATTRIBUTE_NAME,
            SERVICE_NOW_GROUP_BY_ATTRIBUTE_NAME,
            SERVICE_NOW_RDP_CLASSES_ATTRIBUTE_NAME,
            SERVICE_NOW_SSH_CLASSES_ATTRIBUTE_NAME,
            SERVICE_NOW_TELNET_CLASSES_ATTRIBUTE_NAME,
            SERVICE_NOW_VNC_CLASSES_ATTRIBUTE_NAME
    );
    
    /**
     * The form that contains all of the fields used to configure the Service Now
     * connection group.
     */
    public static final Form SERVICE_NOW_CONNECTIONGROUP_FORM = new Form(
            SERVICE_NOW_FORM_NAME,
            Arrays.asList(
                    new BooleanField(SERVICE_NOW_ENABLED_ATTRIBUTE_NAME, "true"),
                    new TextField(SERVICE_NOW_TABLE_ATTRIBUTE_NAME),
                    new TextField(SERVICE_NOW_QUERY_ATTRIBUTE_NAME),
                    new TextField(SERVICE_NOW_GROUP_BY_ATTRIBUTE_NAME),
                    new TextField(SERVICE_NOW_RDP_CLASSES_ATTRIBUTE_NAME),
                    new TextField(SERVICE_NOW_SSH_CLASSES_ATTRIBUTE_NAME),
                    new TextField(SERVICE_NOW_TELNET_CLASSES_ATTRIBUTE_NAME),
                    new TextField(SERVICE_NOW_VNC_CLASSES_ATTRIBUTE_NAME)
            )
    );
    
    // All the connection identifiers in this connection group.
    private final Set<String> connectionIdentifiers = new HashSet<>();
    
    /**
     * Create a new ConnectionGroup that wraps an existing connection group,
     * providing additional ServiceNow functionality.
     * 
     * @param connectionGroup 
     *     The connection group to wrap.
     * 
     * @param userContext
     * 
     * @throws GuacamoleException
     */
    public ServiceNowConnectionGroup(ConnectionGroup connectionGroup, ServiceNowUserContext userContext) throws GuacamoleException {
        super(connectionGroup);
        
        Map<String, String> attributes = super.getAttributes();
        Directory<Connection> connectionDirectory = userContext.getConnectionDirectory();
        
        if (attributes.containsKey(SERVICE_NOW_ENABLED_ATTRIBUTE_NAME)) {
            
            Map<ServiceNowConnectionProtocol, List<String>> protocolMap = new HashMap<>();
            
            String rdpClass = attributes.get(SERVICE_NOW_RDP_CLASSES_ATTRIBUTE_NAME);
            if (rdpClass != null && !rdpClass.isEmpty())
                protocolMap.put(ServiceNowConnectionProtocol.RDP, Arrays.asList(COMMA_DELIMITED.split(rdpClass)));
            
            String sshClass = attributes.get(SERVICE_NOW_SSH_CLASSES_ATTRIBUTE_NAME);
            if (sshClass != null && !sshClass.isEmpty())
                protocolMap.put(ServiceNowConnectionProtocol.SSH, Arrays.asList(COMMA_DELIMITED.split(sshClass)));
            
            String telnetClass = attributes.get(SERVICE_NOW_TELNET_CLASSES_ATTRIBUTE_NAME);
            if (telnetClass != null && !telnetClass.isEmpty())
                protocolMap.put(ServiceNowConnectionProtocol.TELNET, Arrays.asList(COMMA_DELIMITED.split(telnetClass)));
            
            String vncClass = attributes.get(SERVICE_NOW_VNC_CLASSES_ATTRIBUTE_NAME);
            if (vncClass != null && !vncClass.isEmpty())
                protocolMap.put(ServiceNowConnectionProtocol.VNC, Arrays.asList(COMMA_DELIMITED.split(vncClass)));
            
            Collection<ServiceNowConnectionModel> connectionModels = userContext.getSnowApi().getConnections();
            
            for (ServiceNowConnectionModel connectionModel : connectionModels) {
                
                LOGGER.debug(">>> Evaluating item from Service Now: {} ({}) of type {}", connectionModel.getName(), connectionModel.getSysId(), connectionModel.getSysClassName());
                
                
                String hostname = connectionModel.getFqdn();
                
                if ((hostname == null || hostname.isEmpty())) {
                    String ipAddress = connectionModel.getIpAddress();
                    if (ipAddress != null && !ipAddress.isEmpty()) {
                        hostname = ipAddress;
                    }
                    else {
                        LOGGER.warn(">>> No hostname or IP address for {}", connectionModel.getName());
                        continue;
                    }
                }
                
                String sysClass = connectionModel.getSysClassName();
                
                for (ServiceNowConnectionProtocol protocol : protocolMap.keySet()) {
                    
                    LOGGER.debug(">>> Evaluation connections for protocol {}", protocol.toString());
                    
                    List<String> protocolClasses = protocolMap.get(protocol);
                    
                    if (sysClass != null && protocolClasses.contains(sysClass)) {
                        
                        GuacamoleConfiguration guacConfig = new GuacamoleConfiguration();
                        guacConfig.setProtocol(protocol.toString().toLowerCase());
                        guacConfig.setParameter("port", Integer.toString(protocol.getPort()));
                        guacConfig.setConnectionID(connectionModel.getSysId());
                        guacConfig.setParameter("hostname", hostname);
                        
                        String name = connectionModel.getName() + " (" + protocol.toString() + ")";
                        
                        Connection guacConnection = new SimpleConnection(name, connectionModel.getSysId(), guacConfig);
                        guacConnection.setParentIdentifier(this.getIdentifier());
                        
                        LOGGER.debug(">>> Adding connection {} to directory: {}", guacConnection.getName(), guacConnection.getIdentifier());
                        
                        // Add the Service Now connection to the directory
                        if (connectionDirectory instanceof ServiceNowConnectionDirectory)
                            ((ServiceNowConnectionDirectory)connectionDirectory).addServiceNow(guacConnection);
                        
                        connectionIdentifiers.add(connectionModel.getSysId());
                        
                    }
                    
                }
                
            }
            
        }
        
    }
    
    public ConnectionGroup getWrappedConnectionGroup() {
        return getDelegateConnectionGroup();
    }
    
    @Override
    public Map<String, String> getAttributes() {
        
        Map<String, String> attributes = new HashMap<>(super.getAttributes());
        
        for (String attribute : SERVICE_NOW_ATTRIBUTE_NAMES) {
            String value = attributes.get(attribute);
            if (value == null || value.isEmpty())
                attributes.put(attribute, null);
        }
        
        return attributes;
        
    }
    
    @Override
    public void setAttributes(Map<String, String> attributes) {
        
        attributes = new HashMap<>(attributes);
        
        for (String attribute : SERVICE_NOW_ATTRIBUTE_NAMES) {
            String value = attributes.get(attribute);
            if (value == null || value.isEmpty())
                attributes.remove(attribute);
        }
        
        super.setAttributes(attributes);
        
    }
    
    @Override
    public Set<String> getConnectionIdentifiers() {
        return Collections.unmodifiableSet(connectionIdentifiers);
    }
    
}
