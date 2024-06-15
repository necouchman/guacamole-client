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

package org.apache.guacamole.vault.hashi.conf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.net.URI;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.BooleanGuacamoleProperty;
import org.apache.guacamole.properties.StringGuacamoleProperty;
import org.apache.guacamole.properties.URIGuacamoleProperty;
import org.apache.guacamole.vault.conf.VaultConfigurationService;

/**
 * Service for retrieving configuration information regarding the Hashi Corp
 * Vault authentication extension.
 */
@Singleton
public class HashiCorpVaultConfigurationService extends VaultConfigurationService {

    /**
     * The Guacamole server environment.
     */
    @Inject
    private Environment environment;

    /**
     * The name of the file which contains the YAML mapping of connection
     * parameter token to secrets within Keeper Secrets Manager.
     */
    private static final String TOKEN_MAPPING_FILENAME = "hcv-token-mapping.yml";

    /**
     * The name of the properties file containing Guacamole configuration
     * properties whose values are the names of corresponding secrets within
     * Keeper Secrets Manager.
     */
    private static final String PROPERTIES_FILENAME = "guacamole.properties.hcv";

    /**
     * The Guacamole property that contains the URI of the Hashi Corp Vault
     * server. This property is required.
     */
    private static final URIGuacamoleProperty HCV_ADDRESS = new URIGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-address";
        }
        
    };
    
    /**
     * The property that contains the token used to access the vault. This
     * property is required.
     */
    private static final StringGuacamoleProperty HCV_TOKEN = new StringGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-token";
        }
        
    };
    
    /**
     * The property that contains the timeout, in seconds, when attempting to
     * open a connection to the vault. This is optional.
     */
    private static final IntegerGuacamoleProperty HCV_OPEN_TIMEOUT = new IntegerGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-open-timeout";
        }
        
    };
    
    /**
     * The property that contains the timeout, in seconds, when attempting to
     * read a secret from the vault. This is optional.
     */
    private static final IntegerGuacamoleProperty HCV_READ_TIMEOUT = new IntegeryGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-read-timeout";
        }
        
    };
    
    /**
     * The property that configures whether or not the Hashi Corp Vault client
     * will validate SSL certificates of the server. This is optional.
     */
    private static final BooleanGuacamoleProperty HCV_SSL_VERIFY = new BooleanGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-ssl-verify";
        }
        
    };
    
    /**
     * The property that contains the JKS file, relative to GUACAMOLE_HOME,
     * where trusted root certificates are stored for use by the Hashi Corp Vault
     * client.
     */
    private static final FileGuacamoleProperty HCV_SSL_TRUST_STORE = new FileGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-ssl-trust-store";
        }
        
    };
    
    /**
     * The property that contains the JKS file, relative to GUACAMOLE_HOME,
     * where the client certificate and key file are stored for use by the
     * Hashi Corp Vault client.
     */
    private static final FileGuacamoleProperty HCV_SSL_KEY_STORE = new FileGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-ssl-key-store";
        }
        
    };
    
    /**
     * The property that specifies the path to the file, relative to GUACAMOLE_HOME
     * that contains the PEM-formatted certificate(s) used to validate the
     * Hashi Corp Vault server certificate.
     */
    private static final FileGuacamoleProperty HCV_SERVER_PEM = new FileGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-server-pem";
        }
        
    };
            
    private static final FileGuacamoleProperty HCV_CLIENT_PEM = new FileGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-client-pem";
        }
        
    };
    
    private static final FileGuacamoleProperty HCV_CLIENT_KEY = new FileGuacamoleProperty() {
        
        @Override
        public String getName() {
            return "hcv-client-key";
        }
        
    };

    /**
     * Whether users should be able to supply their own KSM configurations.
     */
    private static final BooleanGuacamoleProperty ALLOW_USER_CONFIG = new BooleanGuacamoleProperty() {

        @Override
        public String getName() {
            return "hcv-allow-user-config";
        }
    };

    /**
     * Whether windows domains should be stripped off from usernames that are
     * read from the Hashi Corp Vault.
     */
    private static final BooleanGuacamoleProperty STRIP_WINDOWS_DOMAINS = new BooleanGuacamoleProperty() {

        @Override
        public String getName() {
            return "hcv-strip-windows-domains";
        }
    };

    /**
     * Whether domains should be considered when matching login records in the
     * Hashi Corp Vault. If true, both the domain and username must match for a
     * record to match when using tokens like "HASHI_USER_*". If false, only
     * the username must match.
     */
    private static final BooleanGuacamoleProperty MATCH_USER_DOMAINS = new BooleanGuacamoleProperty() {

        @Override
        public String getName() {
            return "hcv-match-domains-for-users";
        }
    };

    /**
     * Creates a new HashiCorpVaultConfigurationService which reads the
     * configuration from "hcv-token-mapping.yml" and properties from
     * "guacamole.properties.hcv". The token mapping is a YAML file which lists
     * each connection parameter token and the name of the secret from which
     * the value for that token should be read, while the properties file is an
     * alternative to guacamole.properties where each property value is the
     * name of a secret containing the actual value.
     */
    public HashiCorpVaultConfigurationService() {
        super(TOKEN_MAPPING_FILENAME, PROPERTIES_FILENAME);
    }

    @Override
    public boolean getAllowUserConfig() throws GuacamoleException {
        return environment.getProperty(ALLOW_USER_CONFIG, false);
    }

    @Override
    public boolean getSplitWindowsUsernames() throws GuacamoleException {
        return environment.getProperty(STRIP_WINDOWS_DOMAINS, false);
    }

    @Override
    public boolean getMatchUserRecordsByDomain() throws GuacamoleException {
        return environment.getProperty(MATCH_USER_DOMAINS, false);
    }
    
    /**
     * Retrieve the URI to use to access the Hashi Corp Vault server.
     * 
     * @return
     *     The URI to use to access the Hashi Corp Vault server.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed or this required property
     *     is missing.
     */
    public URI getHashiCorpVaulAddress() throws GuacamoleException {
        return environment.getRequiredProperty(HCV_ADDRESS);
    }
    
    /**
     * Retrieve the token to use to access the Hashi Corp Vault.
     * 
     * @return
     *     The token to use to access the Hashi Corp Vault.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed or this required property
     *     is missing.
     */
    public String getHashiCorpVaultToken() throws GuacamoleException {
        return environment.getRequiredProperty(HCV_TOKEN);
    }
    
    /**
     * Return the timeout, in seconds, to wait for a network connection to the
     * vault server.
     * 
     * @return
     *     The number of seconds to wait for a network connection to the vault
     *     server.
     * @throws GuacamoleException 
     */
    public int getHashiCorpVaultOpenTimeout() throws GuacamoleException {
        return environment.getProperty(HCV_OPEN_TIMEOUT, 30);
    }
    
    /**
     * Return the timeout, in seconds, to wait for the read of a secre from the
     * vault.
     * 
     * @return
     *     The number of seconds to wait for a read from the vault.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public int getHashiCorpVaultReadTimeout() throws GuacamoleException {
        return environment.getProperty(HCV_READ_TIMEOUT, 30);
    }
    
    /**
     * Return true if the client should validate the SSL certificate of the
     * vault server, otherwise false.
     * 
     * @return
     *     true if the SSL identity of the vault server should be verified,
     *     otherwise false.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public boolean getHashiCorpVaultSslVerify() throws GuacamoleException {
        return environment.getProperty(HCV_SSL_VERIFY, true);
    }
    
    /**
     * Return the path of the JKS file, relative to GUACAMOLE_HOME, where the
     * client should look for certificates to use to validate the vault server
     * certificate.
     * 
     * @return
     *     The path of the JKS file relative to GUACAMOLE_HOME for trusted
     *     certificates.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public File getHashiCorpVaultSslTrustStore() throws GuacamoleException {
        File sslStore = environment.getProperty(HCV_SSL_TRUST_STORE);
        if (sslStore != null) {
            File fullPathSslStore = new File(
                    environment.getGuacamoleHome().toString(),
                    sslStore.toString());
            if (fullPathSslStore.exists() && fullPathSslStore.canRead())
                return fullPathSslStore;
        return null;
    }
    
    /**
     * Return the path of the JKS file which contains the certificate and
     * private key that the client should use to authenticate to the vault
     * server.
     * 
     * @return
     *     The path of the JKS file which contains the certificate and private
     *     key that the client will use for authentication.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public File getHashiCorpVaultSslKeyStore() throws GuacamoleException {
        File keyStore = environment.getProperty(HCV_SSL_KEY_STORE);
        if (keyStore != null) {
            File fullPathKeyStore = new File(
                    environment.getGuacamoleHome().toString(),
                    keyStore.toString());
            if (fullPathKeyStore.exists() && fullPathKeyStore.canRead())
                return fullPathKeyStore;
        }
        return null;
    }
    
    /**
     * Return the file within GUACAMOLE_HOME which contains the PEM-formatted
     * certificate(s) to use to validate the identity of the vault server.
     * 
     * @return
     *     The File within GUACAMOLE_HOME which contains PEM-formatted trusted
     *     certificates to use to validate the identity of the vault server.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public File getHashiCorpVaultServerPem() throws GuacamoleException {
        File serverPem = environment.getProperty(HCV_SERVER_PEM);
        if (serverPem != null) {
            File fullPathServerPem = new File(
                    environment.getGuacamoleHome().toString(),
                    serverPem.toString());
            if (fullPathServerPem.exists() && fullPathServerPem.canRead())
                return fullPathServerPem;
        }
        return null;
    }
    
    /**
     * Return the file within GUACAMOLE_HOME which contains the PEM-formatted
     * certificate to use to perform SSL authentication with the vault server.
     * 
     * @return
     *     The PEM-formatted client certificate to use for SSL authentication.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public File getHashiCorpVaultClientPem() throws GuacamoleException {
        return environment.getProperty(HCV_CLIENT_PEM);
    }
    
    /**
     * Return the file within GUACAMOLE_HOME which contains the PEM-formatted
     * key file to use to perform SSL authentication with the vault server.
     * 
     * @return
     *     The PEM-formatted key to use for SSL authentication.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be parsed.
     */
    public File getHashiCorpVaultClientKey() throws GuacamoleException {
        return environment.getProperty(HCV_CLIENT_KEY);
    }
    
}