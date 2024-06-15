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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.form.BooleanField;
import org.apache.guacamole.form.NumericField;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.form.TextField;
import org.apache.guacamole.language.TranslatableGuacamoleClientException;
import org.apache.guacamole.vault.conf.VaultAttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service that exposes Hashi Corp Vault-specific attributes, allowing setting
 * vault configuration through the admin interface.
 */
@Singleton
public class HashiCorpVaultAttributeService implements VaultAttributeService {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HashiCorpVaultAttributeService.class);
    
    /**
     * The attribute that contains the address of the Hashi Corp Vault server.
     */
    private static final String VAULT_ADDRESS_ATTRIBUTE = "vault-address";
    
    /**
     * The attribute that specifies the token used to access the
     * Hashi Corp Vault.
     */
    private static final String VAULT_TOKEN_ATTRIBUTE = "vault-token";
    
    /**
     * The attribute that specifies the timeout for opening a connection to the
     * Hashi Corp Vault.
     */
    private static final String VAULT_OPEN_TIMEOUT_ATTRIBUTE = "vault-open-timeout";
    
    /**
     * The attribute that specifies the timeout for reading a secret from the
     * Hashi Corp Vault.
     */
    private static final String VAULT_READ_TIMEOUT_ATTRIBUTE = "vault-read-timeout";
    
    /**
     * The attribute that specifies whether or not SSL verification should be
     * performed when accessing the vault.
     */
    private static final String VAULT_SSL_VERIFY_ATTRIBUTE = "vault-ssl-verify";
    
    /**
     * The attribute that specifies the file that contains the SSL trust store
     * for validating the SSL certificate for the Hashi Corp Vault.
     */
    private static final String VAULT_SSL_TRUST_STORE_ATTRIBUTE = "vault-ssl-trust-store";
    
    /**
     * The attribute that specifies the JKS file that contains the certificate
     * and key pair for performing SSL-based authentication with the Hashi
     * Corp Vault.
     */
    private static final String VAULT_SSL_KEY_STORE_ATTRIBUTE = "vault-ssl-key-store";
    
    /**
     * The attribute that contains the PEM file which contains authority certificates
     * used to validate the Hashi Corp Vault server. This is an alternative to
     * using a JKS file.
     */
    private static final String VAULT_SSL_SERVER_PEM_ATTRIBUTE = "vault-ssl-server-pem";
    
    /**
     * The attribute that contains the PEM-formatted client certificate used
     * to authenticate to the Hashi Corp Vault, as an alternative to specifying
     * a JKS file.
     */
    private static final String VAULT_SSL_CLIENT_PEM_ATTRIBUTE = "vault-ssl-client-pem";
    
    /**
     * The attribute that contains the PEM-formatted client private key used to
     * authentication to the Hashi Corp Vault, as an alternative to specifying
     * a JKS file. This file must be unencrypted.
     */
    private static final String VAULT_SSL_CLIENT_KEY_ATTRIBUTE = "vault-ssl-client-key";
    
    /**
     * Service for retrieving KSM configuration details.
     */
    @Inject
    private HashiCorpVaultConfigurationService configurationService;

    /**
     * The string value used by Hashi Corp Vault attributes to represent the
     * boolean value "true".
     */
    public static final String TRUTH_VALUE = "true";
    
    /**
     * All expected fields in the Hashi Corp Vault configuration JSON blob.
     */
    private static final List<String> HASHI_CORP_VAULT_ATTRIBUTES = (
            Collections.unmodifiableList(Arrays.asList(
                    VAULT_ADDRESS_ATTRIBUTE,
                    VAULT_TOKEN_ATTRIBUTE,
                    VAULT_OPEN_TIMEOUT_ATTRIBUTE,
                    VAULT_READ_TIMEOUT_ATTRIBUTE,
                    VAULT_SSL_VERIFY_ATTRIBUTE,
                    VAULT_SSL_TRUST_STORE_ATTRIBUTE,
                    VAULT_SSL_KEY_STORE_ATTRIBUTE,
                    VAULT_SSL_SERVER_PEM_ATTRIBUTE,
                    VAULT_SSL_CLIENT_PEM_ATTRIBUTE,
                    VAULT_SSL_CLIENT_KEY_ATTRIBUTE
    )));

    /**
     * The HashiCorp Vault configuration attribute contains sensitive
     * information, so it should not be exposed through the directory. Instead,
     * if a value is set on the attributes of an object, the following value
     * will be exposed in its place, and correspondingly the underlying value
     * will not be changed if this value is provided to an update call.
     */
    public static final String HASHI_CORP_VAULT_ATTRIBUTE_PLACEHOLDER_VALUE = "**********";

    /**
     * All attributes related to configuring the HashiCorp Vault on a
     * per-connection-group or per-user basis.
     */
    public static final Form HASHI_CORP_VAULT_CONFIGURATION_FORM = new Form(
            "vault-config",
            Arrays.asList(
                    new TextField(VAULT_ADDRESS_ATTRIBUTE),
                    new PasswordField(VAULT_TOKEN_ATTRIBUTE),
                    new NumericField(VAULT_OPEN_TIMEOUT_ATTRIBUTE),
                    new NumericField(VAULT_READ_TIMEOUT_ATTRIBUTE),
                    new BooleanField(VAULT_SSL_VERIFY_ATTRIBUTE, TRUTH_VALUE),
                    new TextField(VAULT_SSL_TRUST_STORE_ATTRIBUTE),
                    new TextField(VAULT_SSL_KEY_STORE_ATTRIBUTE),
                    new TextField(VAULT_SSL_SERVER_PEM_ATTRIBUTE),
                    new TextField(VAULT_SSL_CLIENT_PEM_ATTRIBUTE),
                    new TextField(VAULT_SSL_CLIENT_KEY_ATTRIBUTE)
            )
    );

    /**
     * All HashiCorp Vault-specific attributes for users, connections, or
     * connection groups, organized by form.
     */
    public static final Collection<Form> HASHI_CORP_VAULT_ATTRIBUTE_FORM =
            Collections.unmodifiableCollection(
                    Arrays.asList(HASHI_CORP_VAULT_CONFIGURATION_FORM));

    /**
     * The name of the attribute which can controls whether a Hashi Corp Vault
     * user configuration is enabled on a connection-by-connection basis.
     */
    public static final String HCV_USER_CONFIG_ENABLED_ATTRIBUTE =
            "vault-user-config-enabled";

    /**
     * All attributes related to configuring the HashiCorp Vault on a
     * per-connection basis.
     */
    public static final Form HASHI_CORP_VAULT_CONNECTION_FORM = new Form("vault-config",
            Arrays.asList(new BooleanField(HCV_USER_CONFIG_ENABLED_ATTRIBUTE, TRUTH_VALUE)));

    /**
     * All HashiCorp Vault-specific attributes for connections, organized by form.
     */
    public static final Collection<Form> HASHI_CORP_VAULT_CONNECTION_ATTRIBUTES =
            Collections.unmodifiableCollection(Arrays.asList(HASHI_CORP_VAULT_CONNECTION_FORM));

    @Override
    public Collection<Form> getConnectionAttributes() {
        return HASHI_CORP_VAULT_CONNECTION_ATTRIBUTES;
    }

    @Override
    public Collection<Form> getConnectionGroupAttributes() {
        return HASHI_CORP_VAULT_ATTRIBUTES;
    }

    @Override
    public Collection<Form> getUserAttributes() {

        try {

            // Expose the user attributes if user-level HashiCorp Vault configuration is enabled
            return configurationService.getAllowUserConfig() ? HASHI_CORP_VAULT_ATTRIBUTE_FORM : Collections.emptyList();

        }

        catch (GuacamoleException e) {

            LOGGER.warn(
                    "Unable to determine if Hashi Corp Vault user attributes "
                    + "should be exposed due to config parsing error: {}.", e.getMessage());
            LOGGER.debug(
                    "Config parsing error prevented checking user attribute configuration",
                    e);

            // If the configuration can't be parsed, default to not exposing the attributes
            return Collections.emptyList();
        }

    }

    @Override
    public Collection<Form> getUserPreferenceAttributes() {

        // Hashi Corp Vault-specific user preference attributes have the same
        // semantics as user attributes
        return getUserAttributes();
    }

    /**
     * Sanitize the value of the provided Hashi Corp Vault config attribute.
     * If the provided config value is non-empty, it will be replaced with the
     * placeholder value to avoid leaking sensitive information. If the value is
     * empty, it will be replaced by `null`.
     *
     * @param hcvAttributeValue
     *     The Hashi Corp Vault configuration attribute value to sanitize.
     *
     * @return
     *     The sanitized Hashi Corp Vault configuration attribute value, stripped
     *     of any sensitive information.
     */
    public static String sanitizeHcvAttributeValue(String hcvAttributeValue) {

        // Any non-empty values may contain sensitive information, and should
        // be replaced by the safe placeholder value
        if (hcvAttributeValue != null && !hcvAttributeValue.trim().isEmpty())
            return HASHI_CORP_VAULT_ATTRIBUTE_PLACEHOLDER_VALUE;

        // If the configuration value is empty, expose a null value
        else
            return null;

    }

    /**
     * Given a map of attribute values, check for the presence of the
     * HASHI_CORP_VAULT_CONFIGURATION_ATTRIBUTE attribute.If it's set, check if
 it's a valid Hashi Corp Vault token. If so, attempt to translate it to a
 base-64-encoded json Hashi Corp Vault config blob. If it's already a
 Hashi Corp Vault config blob, validate it as config blob. If either
 validation fails, a GuacamoleException will be thrown. The processed
 attribute values will be returned.
     *
     * @param attributes
     *     The attributes for which the Hashi Corp Vault configuration attribute
     *     parsing/validation should be performed.
     * 
     * @throws GuacamoleException
     *     If the HASHI_CORP_VAULT_CONFIGURATION_ATTRIBUTE is set, but fails to
     *     validate as either a HashiCorp Vault one-time-token, or a Hashi Corp
     *     Vault base64-encoded JSON config blob.
     */
    public Map<String, String> processAttributes(
            Map<String, String> attributes) throws GuacamoleException {

        // Get the value of the Hashi Corp Vault config attribute in the provided map
        String hcvConfigValue = attributes.get(HASHI_CORP_VAULT_CONFIGURATION_ATTRIBUTE);

        // If the placeholder value was provided, do not update the attribute
        if (HASHI_CORP_VAULT_ATTRIBUTE_PLACEHOLDER_VALUE.equals(hcvConfigValue)) {

            // Remove the attribute from the map so it won't be updated
            attributes = new HashMap<>(attributes);
            attributes.remove(HASHI_CORP_VAULT_CONFIGURATION_ATTRIBUTE);

        }

        // Check if the attribute is set to a non-empty value
        else if (hcvConfigValue != null && !hcvConfigValue.trim().isEmpty()) {

            // If it's already base64-encoded, it's a KSM configuration blob,
            // so validate it immediately
            if (isBase64(hcvConfigValue)) {

                // Attempt to validate the config as a base64-econded KSM config blob
                try {
                    KsmConfig.parseKsmConfig(ksmConfigValue);

                    // If it validates, the entity can be left alone - it's already valid
                    return attributes;
                }

                catch (GuacamoleException exception) {

                    // If the parsing attempt fails, throw a translatable error for display
                    // on the frontend
                    throw new TranslatableGuacamoleClientException(
                            "Invalid base64-encoded JSON KSM config provided for "
                            + HASHI_CORP_VAULT_CONFIGURATION_ATTRIBUTE + " attribute",
                            "CONNECTION_GROUP_ATTRIBUTES.ERROR_INVALID_KSM_CONFIG_BLOB",
                            exception);
                }
            }

            // It wasn't a valid base64-encoded string, it should be a one-time token, so
            // attempt to validat it as such, and if valid, update the attribute to the
            // base64 config blob generated by the token
            try {

                // Create an initially empty storage to be populated using the one-time token
                InMemoryStorage storage = new InMemoryStorage();

                // Populate the in-memory storage using the one-time-token
                SecretsManager.initializeStorage(storage, ksmConfigValue, null);

                // Create an options object using the values we extracted from the one-time token
                SecretsManagerOptions options = new SecretsManagerOptions(
                    storage, null,
                    configurationService.getAllowUnverifiedCertificate());

                // Attempt to fetch secrets using the options we created. This will both validate
                // that the configuration works, and potentially populate missing fields that the
                // initializeStorage() call did not set.
                SecretsManager.getSecrets(options);

                // Create a map to store the extracted values from the KSM storage
                Map<String, String> configMap = new HashMap<>();

                // Go through all the expected fields, extract from the KSM storage,
                // and write to the newly created map
                EXPECTED_KSM_FIELDS.forEach(configKey -> {

                    // Only write the value into the new map if non-null
                    String value = storage.getString(configKey);
                    if (value != null)
                        configMap.put(configKey, value);

                });

                // JSON-encode the value, and then base64 encode that to get the format
                // that KSM would expect
                String jsonString = objectMapper.writeValueAsString(configMap);
                String base64EncodedJson = Base64.getEncoder().encodeToString(
                        jsonString.getBytes(StandardCharsets.UTF_8));

                // Finally, try to parse the newly generated token as a KSM config. If this
                // works, the config should be fully functional
                KsmConfig.parseKsmConfig(base64EncodedJson);

                // Make a copy of the existing attributes, modifying just the value for
                // KSM_CONFIGURATION_ATTRIBUTE
                attributes = new HashMap<>(attributes);
                attributes.put(
                        KsmAttributeService.KSM_CONFIGURATION_ATTRIBUTE, base64EncodedJson);

            }

            // The KSM SDK only throws raw Exceptions, so we can't be more specific
            catch (Exception exception) {

                // If the parsing attempt fails, throw a translatable error for display
                // on the frontend
                throw new TranslatableGuacamoleClientException(
                        "Invalid one-time KSM token provided for "
                        + HASHI_CORP_VAULT_CONFIGURATION_ATTRIBUTE + " attribute",
                        "CONNECTION_GROUP_ATTRIBUTES.ERROR_INVALID_HASHI_CORP_VAULT_ONE_TIME_TOKEN",
                        exception);
            }
        }

        return attributes;

    }


}
