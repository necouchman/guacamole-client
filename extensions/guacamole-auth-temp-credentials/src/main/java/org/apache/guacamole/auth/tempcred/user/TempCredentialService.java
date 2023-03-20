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

package org.apache.guacamole.auth.tempcred.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.tempcred.form.HiddenField;
import org.apache.guacamole.form.Field;
import org.apache.guacamole.form.MultilineField;
import org.apache.guacamole.form.PasswordField;
import org.apache.guacamole.form.TextField;
import org.apache.guacamole.form.UsernameField;
import org.apache.guacamole.language.TranslatableGuacamoleInsufficientCredentialsException;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.credentials.CredentialsInfo;
import org.apache.guacamole.token.TokenName;

/**
 * A service that provides prompting for temporary credentials.
 */
public class TempCredentialService {
    
    /**
     * The HTTP request parameter name for the temporary username.
     */
    public static final String PARAMETER_NAME_USERNAME = "guac-temp-username";
    
    /**
     * The HTTP request parameter name for the temporary password.
     */
    public static final String PARAMETER_NAME_PASSWORD = "guac-temp-password";
    
    /**
     * The HTTP request parameter name for the temporary domain.
     */
    public static final String PARAMETER_NAME_DOMAIN = "guac-temp-domain";
    
    /**
     * The HTTP request parameter name for the temporary SSH key.
     */
    public static final String PARAMETER_NAME_SSHKEY = 
            "guac-temp-sshkey";
    
    /**
     * The HTTP request parameter name for the temporary SSH key passphrase.
     */
    public static final String PARAMETER_NAME_SSHPASSPHRASE = 
            "guac-temp-sshpassphrase";
    
    /**
     * The HTTP request parameter name that allows this authentication service
     * to know that the temporary credential prompt has been presented.
     */
    public static final String PARAMETER_NAME_PROVIDED = 
            "guac-temp-credentials-provided";
    
    /**
     * The list of all of the HTTP parameters, except the hidden one used to
     * know if the prompt has occurred or not.
     */
    private static final List<String> TEMP_CREDENTIAL_PARAMETER_LIST = 
            Arrays.asList(
                PARAMETER_NAME_USERNAME,
                PARAMETER_NAME_PASSWORD,
                PARAMETER_NAME_DOMAIN,
                PARAMETER_NAME_SSHKEY,
                PARAMETER_NAME_SSHPASSPHRASE
            );

    /**
     * The form field that will be used for prompting for the temporary username.
     */
    private static final Field TEMP_FIELD_USERNAME = 
            new UsernameField(PARAMETER_NAME_USERNAME);
    
    /**
     * The form field that will be used for prompting for the temporary password.
     */
    private static final Field TEMP_FIELD_PASSWORD = 
            new PasswordField(PARAMETER_NAME_PASSWORD);
    
    /**
     * The form field that will be used for prompting for the temporary domain.
     */
    private static final Field TEMP_FIELD_DOMAIN = 
            new TextField(PARAMETER_NAME_DOMAIN);
    
    /**
     * The form field that will be used for prompting for the temporary SSH key.
     */
    private static final Field TEMP_FIELD_SSHKEY = 
            new MultilineField(PARAMETER_NAME_SSHKEY);
    
    /**
     * The form field that will be used for prompting for the temporary SSH key
     * passphrase.
     */
    private static final Field TEMP_FIELD_SSHPASSPHRASE = 
            new PasswordField(PARAMETER_NAME_SSHPASSPHRASE);
    
    /**
     * The hidden form field used to identify if the temporary credential prompt
     * has occurred.
     */
    private static final Field TEMP_FIELD_PROVIDED = 
            new HiddenField(PARAMETER_NAME_PROVIDED);
    
    /**
     * The full list of fields that will be presented to the user as possible
     * fields for temporary credentials.
     */
    private static final List<Field> TEMP_CREDENTIAL_FIELD_LIST = Arrays.asList(
            TEMP_FIELD_USERNAME,
            TEMP_FIELD_PASSWORD,
            TEMP_FIELD_DOMAIN,
            TEMP_FIELD_SSHKEY,
            TEMP_FIELD_SSHPASSPHRASE,
            TEMP_FIELD_PROVIDED
    );
    
    /**
     * Retrieve the temporary credentials from the user during authentication,
     * throwing an exception that will trigger the web application for additional
     * information from the user, and then return those values as a Map of 
     * key/value pairs.
     * 
     * @param authenticatedUser
     *     The user who is in the process of logging in.
     * 
     * @return
     *     A map of key/value pairs of temporary credentials entered by the
     *     user.
     * 
     * @throws GuacamoleException 
     *     If a credential prompt has not occurred, in order to trigger the
     *     prompt for the user.
     */
    public static Map<String, String> getTemporaryCredentials(AuthenticatedUser authenticatedUser)
            throws GuacamoleException {
        
        // Pull the original HTTP request used to authenticate
        Credentials credentials = authenticatedUser.getCredentials();
        HttpServletRequest request = credentials.getRequest();
        
        // If hidden parameter has not been provided, then prompt for temp credentials.
        if (request.getParameter(PARAMETER_NAME_PROVIDED) == null) {
            
            throw new TranslatableGuacamoleInsufficientCredentialsException(
                    "Please provide a temporary set of credentials that "
                    + "can be used as tokens in connections.",
                    "TEMP_CREDENTIAL.INFO_PROVIDE_TEMP_CREDENTIALS",
                    new CredentialsInfo(TEMP_CREDENTIAL_FIELD_LIST));
            
        }
        
        // Temporary credential prompt has been received - add tokens
        Map<String, String> tokens = new HashMap<>();
        for (String parameter : TEMP_CREDENTIAL_PARAMETER_LIST) {
            
            String value = request.getParameter(parameter);
            if (value != null)
                tokens.put(TokenName.canonicalize(parameter), value);
        }
        
        return tokens;
        
    }
    
}
