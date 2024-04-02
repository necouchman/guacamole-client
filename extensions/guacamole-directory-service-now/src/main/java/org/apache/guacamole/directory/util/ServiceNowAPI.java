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

package org.apache.guacamole.directory.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.directory.conf.ConfigurationService;
import org.apache.guacamole.directory.connection.ServiceNowConnectionModel;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.environment.LocalEnvironment;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * A utility class that provides translation between the Service Now REST API
 * and Guacamole.
 */
public class ServiceNowAPI {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceNowAPI.class);
    
    /**
     * The path to the API endpoint to get servers.
     */
    private static final String SERVICE_NOW_CMDB_API = 
            "/api/now/table/cmdb_ci_server";
    
    /**
     * The default query to use for servers in Service Now
     */
    private static final String SERVICE_NOW_DEFAULT_QUERY = 
            "sysparam_query=operational_status=1^ip_addressISNOTEMPTY^ORfqdnISNOTEMPTY";
    
    /**
     * ObjectMapper for deserializing REST API objects.
     */
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * The Service Now instance name that will be queried.
     */
    private final String serviceNowInstance;
    
    /**
     * The username for authentication to the Service Now instance.
     */
    private final String serviceNowUsername;
    
    /**
     * The password for authentication to the Service Now instance.
     */
    private final String serviceNowPassword;
    
    /**
     * The HttpClient that will be used to run the REST API queries.
     */
    private final CloseableHttpClient httpClient;
    
    /**
     * Create a new ServiceNowAPI instance, using the provided instance name,
     * username, and password, to configure the API connection.
     * 
     * @throws GuacamoleException
     *     If any required data is missing, the connection to the Service Now
     *     instance cannot be established, or the configuration file cannot
     *     be parsed.
     */
    public ServiceNowAPI() throws GuacamoleException {
        
        Environment environment = LocalEnvironment.getInstance();
        
        this.serviceNowInstance = environment.getRequiredProperty(ConfigurationService.SERVICE_NOW_INSTANCE_NAME);
        this.serviceNowUsername = environment.getRequiredProperty(ConfigurationService.SERVICE_NOW_USERNAME);
        this.serviceNowPassword = environment.getRequiredProperty(ConfigurationService.SERVICE_NOW_PASSWORD);
        
        String serviceNowFqdn = serviceNowInstance + ".service-now.com";
        int serviceNowPort = 443;
        this.httpClient = HttpClients
                .custom()
                .setDefaultCredentialsProvider(
                        CredentialsProviderBuilder
                                .create()
                                .add(
                                        new AuthScope(serviceNowFqdn, serviceNowPort),
                                        serviceNowUsername, serviceNowPassword.toCharArray()
                                )
                                .build()
                )
                .build();
        
    }
    
    /**
     * Connect to the Service Now REST API and read in the CMDB CIs, parsing
     * out the required information to create connections.
     * 
     * @return
     *     A Collection of connections from available data in the Service Now
     *     CMDB.
     * 
     * @throws GuacamoleException 
     *     Some time, under some circumstances.
     */
    public Collection<ServiceNowConnectionModel> getConnections()
            throws GuacamoleException {
        
        try {
            // Build Query URI
            URI snowServerQuery = new URI("https", 
                    serviceNowInstance + ".service-now.com",
                    SERVICE_NOW_CMDB_API,
                    SERVICE_NOW_DEFAULT_QUERY,
                    null);

            // Query the Service Now API
            HttpGet httpGet = new HttpGet(snowServerQuery);
            String serviceNowJson = httpClient.execute(httpGet, response -> {
                return EntityUtils.toString(response.getEntity());
            });
            
            LOGGER.debug(">>> JSON String: {}", serviceNowJson);

            // Parse the JSON output into results
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode rootNode = mapper.readTree(serviceNowJson);
            JsonNode result = rootNode.get("result");
            return mapper.readValue(
                    result.traverse(),
                    new TypeReference<List<ServiceNowConnectionModel>>(){}
            );
            
        }
        catch (URISyntaxException e) {
            throw new GuacamoleServerException("Invalid URI for Service Now query.", e);
        }
        catch (IOException e) {
            throw new GuacamoleServerException("IO Error attempting to consume REST API response.", e);
        }
    }
    
}
