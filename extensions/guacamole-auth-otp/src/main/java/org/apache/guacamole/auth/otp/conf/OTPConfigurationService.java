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

package org.apache.guacamole.auth.otp.conf;

import com.google.inject.Inject;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.BooleanGuacamoleProperty;
import org.apache.guacamole.properties.EnumGuacamoleProperty;
import org.apache.guacamole.properties.IntegerGuacamoleProperty;
import org.apache.guacamole.properties.StringGuacamoleProperty;

/**
 * Service for retrieving configuration options for the OTP extension.
 */
public class OTPConfigurationService {
    
    /**
     * The Guacamole server environment.
     */
    @Inject
    private Environment environment;
    
    /**
     * The default method of sending OTP requests to users, if not
     * specified for a particular user or user group. Valid options are
     * "email" (the default) and "sms".
     */
    private static final EnumGuacamoleProperty<OTPMessagingMethod> OTP_DEFAULT_METHOD =
            new EnumGuacamoleProperty<OTPMessagingMethod>(OTPMessagingMethod.class) {
    
        @Override
        public String getName() { return "otp-default-method"; }
                
    };
    
    /**
     * The default OTP messaging method to use if none is specified.
     */
    private static final OTPMessagingMethod OTP_DEFAULT_METHOD_DEFAULT = OTPMessagingMethod.EMAIL;
    
    /**
     * The default timeout for OTP requests, if not specified for a particular
     * user or user group, in seconds. The default is 300 seconds (5 minutes).
     */
    private static final IntegerGuacamoleProperty OTP_DEFAULT_TIMEOUT =
            new IntegerGuacamoleProperty() {
                
        @Override
        public String getName() { return "otp-default-timeout"; }
                
    };
    
    /**
     * The default number of seconds for OTP timeout.
     */
    private static final int OTP_DEFAULT_TIMEOUT_DEFAULT = 300;
    
    /**
     * The action to take when data required to send an OTP request is missing
     * for a user. The default is to block the login and give the user an
     * authentication error; however, this can be changed to allow users to 
     * log in. Valid options are "block" and "allow".
     */
    private static final EnumGuacamoleProperty<OTPMissingAction> OTP_MISSING_ACTION =
            new EnumGuacamoleProperty<OTPMissingAction>(OTPMissingAction.class) {
    
        @Override
        public String getName() { return "otp-missing-action"; }
                
    };
    
    /**
     * The default action if a user is missing required configuration data.
     */
    private static final OTPMissingAction OTP_MISSING_ACTION_DEFAULT = OTPMissingAction.BLOCK;
    
    /**
     * The size of the one-time passwords to generate.
     */
    private static final IntegerGuacamoleProperty OTP_LENGTH =
            new IntegerGuacamoleProperty() {
      
        @Override
        public String getName() { return "otp-length"; }
                
    };
    
    /**
     * The default number of digits or characters to use in the one-time password.
     */
    private static final int OTP_LENGTH_DEFAULT = 6;
    
    /**
     * The character classes that should be used for the generated one-time
     * passwords.
     */
    private static final EnumGuacamoleProperty<OTPCharacterClasses> OTP_CHARACTERS =
            new EnumGuacamoleProperty<OTPCharacterClasses>(OTPCharacterClasses.class) {
    
        @Override
        public String getName() { return "otp-character-classes"; }
                
    };
    
    /**
     * The default character class to use for the One Time Password.
     */
    private static final OTPCharacterClasses OTP_CHARACTERS_DEFAULT = OTPCharacterClasses.NUMERIC;
    
    /**
     * The property that configures the sender address for e-mail-based
     * one-time passwords.
     */
    private static final EmailAddressProperty OTP_MAIL_SENDER =
            new EmailAddressProperty() {
        
        @Override
        public String getName() { return "otp-mail-sender"; }
                
    };
    
    /**
     * The default address used as the sender if unconfigured.
     */
    private static final String OTP_MAIL_SENDER_DEFAULT = "guacamole@localhost.localdomain";
    
    /**
     * The SMTP e-mail server to use to send e-mail.
     */
    private static final StringGuacamoleProperty OTP_MAIL_SERVER =
            new StringGuacamoleProperty() {
    
        @Override
        public String getName() { return "otp-mail-server"; }
                
    };
    
    /**
     * The default mail server to use for sending e-mail OTP messages.
     */
    private static final String OTP_MAIL_SERVER_DEFAULT = "localhost";
    
    /**
     * The TCP port on which to contact the mail server to send e-mail.
     */
    private static final IntegerGuacamoleProperty OTP_MAIL_PORT =
            new IntegerGuacamoleProperty() {
    
        @Override
        public String getName() { return "otp-mail-port"; }
           
    };
    
    /**
     * The default TCP port to use when connecting to the mail server.
     */
    private static final int OTP_MAIL_PORT_DEFAULT = 25;
    
    /**
     * True if the SMTP client should try to authenticate to the e-mail
     * server, otherwise false. The default is false.
     */
    private static final BooleanGuacamoleProperty OTP_MAIL_AUTH = 
            new BooleanGuacamoleProperty() {
    
        @Override
        public String getName() { return "otp-mail-auth"; }
                
    };
    
    /**
     * Whether or not to use authentication when talking to the mail server by
     * default.
     */
    private static final boolean OTP_MAIL_AUTH_DEFAULT = false;
    
    /**
     * If authentication is enabled, the username to use for sending
     * e-mail.
     */
    private static final StringGuacamoleProperty OTP_MAIL_USERNAME =
            new StringGuacamoleProperty() {
      
        @Override
        public String getName() { return "otp-mail-username"; }
                
    };
    
    /**
     * If authentication is enabled, the password to use for sending
     * e-mail.
     */
    private static final StringGuacamoleProperty OTP_MAIL_PASSWORD =
            new StringGuacamoleProperty() {
                
        @Override
        public String getName() { return "otp-mail-password"; }
                
    };
    
    /**
     * The type of encryption to use when sending e-mail. Valid options are
     * "none" (the default), for plain-text traffic, "ssl", to use a specific
     * SSL port, and "tls", to use StartTLS.
     */
    private static final EnumGuacamoleProperty<OTPMailEncryption> OTP_MAIL_ENCRYPT =
            new EnumGuacamoleProperty<OTPMailEncryption>(OTPMailEncryption.class) {
    
        @Override
        public String getName() { return "otp-mail-encrypt"; }
                
    };
    
    private static final OTPMailEncryption OTP_MAIL_ENCRYPT_DEFAULT = OTPMailEncryption.NONE;
    
    /**
     * The SMS gateway server to use in order to send SMS messages.
     */
    private static final StringGuacamoleProperty OTP_SMS_SERVER =
            new StringGuacamoleProperty() {
    
        @Override
        public String getName() { return "otp-sms-server"; }
                
    };
    
    /**
     * The property that configures the sender number/address to use for SMS
     * one-time password messages.
     */
    private static final StringGuacamoleProperty OTP_SMS_SENDER =
            new StringGuacamoleProperty() {
    
        @Override
        public String getName() { return "otp-sms-sender"; }
                
    };
    
    /**
     * Returns the default OTP method, as configured in guacamole.properties,
     * or "email" if none is specified.
     * 
     * @return
     *     The default OTP method as configured in guacamole.properties, or
     *     "email" if none is specified.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read or there is a parsing error.
     */
    public OTPMessagingMethod getDefaultOTPMethod() throws GuacamoleException {
        return environment.getProperty(OTP_DEFAULT_METHOD, OTP_DEFAULT_METHOD_DEFAULT);
    }
    
    /**
     * Returns the default OTP timeout, as configured in guacamole.properties,
     * in seconds, or the default of 300 if none is specified.
     * 
     * @return
     *     The default timeout in number of seconds.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read or a parsing error occurs.
     */
    public int getDefaultOTPTimeout() throws GuacamoleException {
        return environment.getProperty(OTP_DEFAULT_TIMEOUT, OTP_DEFAULT_TIMEOUT_DEFAULT);
    }
    
    /**
     * Returns the default action to take if information (e.g. e-mail address)
     * is missing for a user attempting to log in, if specified in
     * guacamole.properties. If no action is specified the default value
     * of "block" is returned.
     * 
     * @return
     *     The default action to take when required user information is missing.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read or a parsing error occurs.
     */
    public OTPMissingAction getMissingAction() throws GuacamoleException {
        return environment.getProperty(OTP_MISSING_ACTION, OTP_MISSING_ACTION_DEFAULT);
    }
    
    /**
     * Returns the length of the one-time password, as specified in the
     * guacamole.properties file, or the default length of 6 if not specified.
     * 
     * @return
     *     The length of the one-time password.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read or a parsing error occurs.
     */
    public int getOTPLength() throws GuacamoleException {
        return environment.getProperty(OTP_LENGTH, OTP_LENGTH_DEFAULT);
    }
    
    /**
     * Returns the classes of characters that should be used for the one-time
     * password, or the default of numeric characters if not explicitly configured.
     * 
     * @return
     *     The character classes that should be used for the one-time password.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read, or a parsing error occurs.
     */
    public OTPCharacterClasses getOTPCharacterClasses() throws GuacamoleException {
        return environment.getProperty(OTP_CHARACTERS, OTP_CHARACTERS_DEFAULT);
    }
    
    /**
     * Returns the e-mail address that will be used as the sender on messages
     * for one-time passwords sent via e-mail.
     * 
     * @return
     *     The e-mail address that will be used as the sender on e-mail-based
     *     one-time passwords.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read, or a parsing error occurs.
     */
    public InternetAddress getOTPMailSender() throws GuacamoleException {
        try {
            return environment.getProperty(OTP_MAIL_SENDER, new InternetAddress(OTP_MAIL_SENDER_DEFAULT));
        }
        catch (AddressException e) {
            throw new GuacamoleServerException("Invalid e-mail address specified.", e);
        }
        
    }
    
    /**
     * Returns the e-mail server to use to send OTP via e-mail as specified
     * in guacamole.properties, or localhost if no value is given.
     * 
     * @return
     *     The e-mail server to use to send OTP via e-mail.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read or a parsing error occurs.
     */
    public String getOTPMailServer() throws GuacamoleException {
        return environment.getProperty(OTP_MAIL_SERVER, OTP_MAIL_SERVER_DEFAULT);
    }
    
    /**
     * Returns the TCP port to use when contacting the e-mail server to send
     * OTP e-mails as specified in guacamole.properties, or 25 if no value
     * is given.
     * 
     * @return
     *     The TCP port to use to talk to the e-mail server.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read or a parsing error occurs.
     */
    public int getOTPMailPort() throws GuacamoleException {
        return environment.getProperty(OTP_MAIL_PORT, OTP_MAIL_PORT_DEFAULT);
    }
    
    /**
     * Returns true if authentication should be attempted when talking to the
     * email server, otherwise false. The default, if not specified in the
     * guacamole.properties file, is false (no authentication).
     * 
     * @return
     *     True if authentication to the e-mail server should be attempted,
     *     otherwise false.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read, or a parsing error occurs.
     */
    public boolean getOTPMailAuthentication() throws GuacamoleException {
        return environment.getProperty(OTP_MAIL_AUTH, OTP_MAIL_AUTH_DEFAULT);
    }
    
    /**
     * Returns the username to use for authentication as configured in
     * guacamole.properties, or null if authentication is disabled.
     * 
     * @return
     *     The username to use for authentication, or null if authentication
     *     is disabled.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read, a parsing error occurs, or
     *     authentication is enabled but this value is not specified.
     */
    public String getOTPMailUsername() throws GuacamoleException {
        if (getOTPMailAuthentication())
            return environment.getRequiredProperty(OTP_MAIL_USERNAME);
        return null;
    }
    
    /**
     * Returns the password to use for authentication as configured in
     * guacamole.properties, or null if authentication is disabled.
     * 
     * @return
     *     The password to use for authentication to the e-mail server, or null
     *     if authentication is disabled.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read, a parsing error occurs, or
     *     authentication has been enabled but this value is not specified.
     */
    public String getOTPMailPassword() throws GuacamoleException {
        if (getOTPMailAuthentication())
            return environment.getRequiredProperty(OTP_MAIL_PASSWORD);
        return null;
    }
    
    /**
     * Returns the type of encryption to use for the connection to the e-mail
     * server as specified in guacamole.properties, or "none" if no value is
     * given.
     * 
     * @return
     *     The type of encryption to use when connecting to the mail server.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read or a parsing error occurs.
     */
    public OTPMailEncryption getOTPMailEncryption() throws GuacamoleException {
        return environment.getProperty(OTP_MAIL_ENCRYPT, OTP_MAIL_ENCRYPT_DEFAULT);
    }
    
    /**
     * Returns the server to use to send SMS messages for OTP authentication, or
     * null if no value is provided.
     * 
     * @return
     *     The server to use to send SMS messages.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot be read, a parsing error occurs, or
     *     the default method is set to SMS and this value is not provided.
     */
    public String getOTPSMSServer() throws GuacamoleException {
        if (getDefaultOTPMethod() == OTPMessagingMethod.SMS)
            return environment.getRequiredProperty(OTP_SMS_SERVER);
        return environment.getProperty(OTP_SMS_SERVER, null);
    }
    
    /**
     * Returns the sender number/address that will be used to send SMS-based
     * one-time password messages.
     * 
     * @return
     *     The number/address used as the sender for one-time password SMS-based
     *     messages.
     * 
     * @throws GuacamoleException 
     *     If guacamole.properties cannot e read, or a parsing error occurs.
     */
    public String getOTPSMSSender() throws GuacamoleException {
        return environment.getProperty(OTP_SMS_SENDER);
    }
    
}
