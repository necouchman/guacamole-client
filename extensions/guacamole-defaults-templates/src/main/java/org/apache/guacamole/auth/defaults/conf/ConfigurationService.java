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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for retrieving configuration data for this extension.
 */
@Singleton
public class ConfigurationService {
    
    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    
    /**
     * ObjectMapper for deserializing YAML.
     */
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
            .registerModule(new SimpleModule().addDeserializer(
                    Pattern.class, new CaseInsensitivePatternDeserializer()
                )
            );
    
    /**
     * The name of the file within GUACAMOLE_HOME that defines the defaults and
     * templates available to the Guacamole client.
     */
    private static final String DEFAULTS_YML = "defaults.yml";

    /**
     * The timestamp that the {@link #DEFAULTS_YML} was last modified when
     * it was read, as would be returned by {@link File#lastModified()}.
     */
    private final AtomicLong lastModified = new AtomicLong(0);
    
    /**
     * The cached copy of the configuration read from {@link #DEFAULTS_YML}.
     * If the current set of defaults has not yet been read from the YAML
     * configuration file this will be null.
     */
    private JacksonDefaultsConfiguration cachedDefaults = null;
    
    /**
     * The Guacamole server environment.
     */
    @Inject
    private Environment environment;
    
    /**
     * Returns the default and template connection configurations, checking
     * the GUACAMOLE_HOME directory for an updated file and reading the
     * contents into the cached configuration, or returning the cached
     * configuration if the file has not been updated.
     * 
     * @return
     *     The default and template configurations as defined within the
     *     YAML file.
     * 
     * @throws GuacamoleException 
     */
    public JacksonDefaultsConfiguration getDefaultsConfiguration() throws GuacamoleException {
        
        File defaultsConfig = new File(environment.getGuacamoleHome(), DEFAULTS_YML);
        
        // If the file exists, attempt to read it.
        if (defaultsConfig.exists()) {
            
            long oldLastModified = lastModified.get();
            long currentLastModified = defaultsConfig.lastModified();
            
            // Compare the modification times, updating the cached configuration
            // only if the file has been modified more recently than the cached
            // configuration.
            if (currentLastModified > oldLastModified && lastModified.compareAndSet(oldLastModified, currentLastModified)) {
                try {

                    cachedDefaults = mapper.readValue(defaultsConfig, new TypeReference<JacksonDefaultsConfiguration>() {});
                    
                    if (cachedDefaults != null) {
                        logger.debug(cachedDefaults.toString());
                    }
                    
                    else
                        logger.debug(">>> NO DEFAULTS IN FILE. <<<");

                }
                catch (IOException e) {
                    logger.error(">>> \"{}\" could not be read/parsed: {} <<<", defaultsConfig, e.getMessage());
                }
            }
            else
                logger.debug(">>> Using cached defaults configuration from \"{}\". <<<", defaultsConfig);
            
        }
        
        // The file does not exist, but there is a cached config - clear it.
        else if (cachedDefaults != null) {
            long oldLastModified = lastModified.get();
            if (lastModified.compareAndSet(oldLastModified, 0)) {
                logger.debug(">>> Clearing cached defaults configuration from \"{}\" (file no longer exists). <<<", cachedDefaults);
                cachedDefaults = null;
            }
        }
        
        else {
            logger.debug(">>> NO DEFAULTS FILE EXISTS.<<<");
        }
        
        // Return the result.
        return cachedDefaults;
        
    }
    
}
