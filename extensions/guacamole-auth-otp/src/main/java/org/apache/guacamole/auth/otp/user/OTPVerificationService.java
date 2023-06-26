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

package org.apache.guacamole.auth.otp.user;

import com.google.inject.Inject;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.MessagingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.otp.OTPSessionMap;
import org.apache.guacamole.auth.otp.conf.OTPCharacterClasses;
import org.apache.guacamole.auth.otp.conf.OTPConfigurationService;
import org.apache.guacamole.auth.otp.conf.OTPMailEncryption;
import org.apache.guacamole.auth.otp.conf.OTPMessagingMethod;
import org.apache.guacamole.auth.otp.usergroup.OTPUserGroup;
import org.apache.guacamole.form.Field;
import org.apache.guacamole.form.PasswordField;
import org.apache.guacamole.language.TranslatableGuacamoleClientException;
import org.apache.guacamole.language.TranslatableGuacamoleInsufficientCredentialsException;
import org.apache.guacamole.language.TranslatableGuacamoleSecurityException;
import org.apache.guacamole.net.auth.AuthenticatedUser;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.User;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.net.auth.UserGroup;
import org.apache.guacamole.net.auth.credentials.CredentialsInfo;
import org.apache.guacamole.otp.OneTimePassword;
import org.apache.guacamole.otp.OTPMailProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service class for sending and verifying a one-time password for a user
 * login to Guacamole.
 */
public class OTPVerificationService {
    
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OTPVerificationService.class);
    
    /**
     * The name of the field that will be used to challenge the user for
     * their one-time password.
     */
    public static final String OTP_FIELD_NAME = "otp-challenge-field";
    
    /**
     * Service for retrieving OTP configuration information.
     */
    @Inject
    private OTPConfigurationService otpConfig;
    
    /**
     * The session map, containing the list of users and their associated
     * one-time passwords.
     */
    @Inject
    private OTPSessionMap sessionMap;
    
    /**
     * Check whether or not the one-time password requirement is disabled
     * for a particular user who is logging in, providing the user context
     * and the authenticated user. True will be returned if the OTP requirement
     * has been disabled for the user or a group the user is a member of,
     * otherwise this method will return false.
     * 
     * @param context
     *     The UserContext of the user who has successfully authenticated
     *     to the first criteria.
     * 
     * @param authenticatedUser
     *     The AuthenticatedUser object of the successfully-authenticated user.
     * 
     * @return
     *     True if the OTP requirement is disabled for this user, otherwise
     *     false.
     * 
     * @throws GuacamoleException 
     *     If an error occurs retrieving the user's OTP information.
     */
    private boolean otpDisabled(UserContext context, AuthenticatedUser authenticatedUser) throws GuacamoleException {
        
        // If OTP is disabled for this user, return, allowing login to continue
        Map<String, String> myAttributes = context.self().getAttributes();
        if (myAttributes != null
                && OTPUser.TRUTH_VALUE.equals(myAttributes.get(OTPUser.OTP_KEY_DISABLED_ATTRIBUTE_NAME))) {

            LOGGER.warn("One-time password validation has been disabled for user \"{}\"",
                    context.self().getIdentifier());
            return true;

        }
        
        // Check if any effective user groups have OTP marked as disabled
        Set<String> userGroups = authenticatedUser.getEffectiveUserGroups();
        Directory<UserGroup> directoryGroups = context.getPrivileged().getUserGroupDirectory();
        for (String userGroup : userGroups) {
            UserGroup thisGroup = directoryGroups.get(userGroup);
            if (thisGroup == null)
                continue;
            
            Map<String, String> grpAttributes = thisGroup.getAttributes();
            if (grpAttributes != null 
                    && OTPUserGroup.TRUTH_VALUE.equals(grpAttributes.get(OTPUserGroup.OTP_KEY_DISABLED_ATTRIBUTE_NAME))) {

                LOGGER.warn("TOTP validation will be bypassed for user \"{}\""
                            + " because it has been disabled for group \"{}\"",
                            context.self().getIdentifier(), userGroup);
                return true;

            }
        }
        
        // OTP has not been disabled
        return false;
        
    }
    
    /**
     * Return the effective OTP method for a user, checking the user attributes,
     * group attributes, and finally system-configured default.
     * 
     * @param context
     *     The UserContext of the user who is logging in.
     * 
     * @param authenticatedUser
     *     The AuthenticatedUser of the user in the process of logging in.
     * 
     * @return
     *     The messaging method that should be used to send the one-time password
     *     for the user.
     * 
     * @throws GuacamoleException 
     *     If an error occurs retrieving attributes or getting the system
     *     configuration.
     */
    private OTPMessagingMethod getEffectiveOTPMethod(UserContext context, AuthenticatedUser authenticatedUser) throws GuacamoleException {
        
        // Pull attributes for the user
        Map<String, String> myAttributes = context.self().getAttributes();
        
        // If user has a value configured, return that.
        if (myAttributes.containsKey(OTPUser.OTP_KEY_METHOD_ATTRIBUTE_NAME))
            return OTPMessagingMethod.valueOf(myAttributes.get(OTPUser.OTP_KEY_METHOD_ATTRIBUTE_NAME));
        
        // Loop groups and return the first configure value
        Set<String> userGroups = authenticatedUser.getEffectiveUserGroups();
        Directory<UserGroup> directoryGroups = context.getPrivileged().getUserGroupDirectory();
        for (String userGroup : userGroups) {
            UserGroup thisGroup = directoryGroups.get(userGroup);
            if (thisGroup == null) {
                continue;
            }

            Map<String, String> grpAttributes = thisGroup.getAttributes();
            if (grpAttributes != null
                    && grpAttributes.containsKey(OTPUserGroup.OTP_KEY_METHOD_ATTRIBUTE_NAME)) {

                return OTPMessagingMethod.valueOf(myAttributes.get(OTPUserGroup.OTP_KEY_METHOD_ATTRIBUTE_NAME));

            }
        }
        
        // If nothing else, use the system-configured default value.
        return otpConfig.getDefaultOTPMethod();
        
    }
    
    /**
     * Check whether or not a particular user has the required information
     * configured to complete one-time password authentication, returning true
     * if the information is present, otherwise returning false.
     * 
     * @param context
     *     The UserContext of an already-authenticated user who is being
     *     challenged for a one-time password.
     * 
     * @param authenticatedUser
     *     The AuthenticatedUser object of an already-authenticated user who is
     *     being challenged for a one-time password.
     * 
     * @return
     *     True if the user has required attributes populated, otherwise false.
     * 
     * @throws GuacamoleException 
     *     If an error occurs checking for the required attributes.
     */
    private boolean hasRequired(UserContext context, AuthenticatedUser authenticatedUser) throws GuacamoleException {
        
        // Pull attributes for the user
        Map<String, String> myAttributes = context.self().getAttributes();
        
        // Check the effective OTP method for the user and verify that attributes are present.
        switch (getEffectiveOTPMethod(context, authenticatedUser)) {
            case EMAIL:
                String email = myAttributes.get(User.Attribute.EMAIL_ADDRESS);
                String altEmail = myAttributes.get(OTPUser.OTP_USER_ALTERNATE_EMAIL_ATTRIBUTE_NAME);
                return ((email != null && !email.isEmpty()) || (altEmail != null && !altEmail.isEmpty()));
            case SMS:
                String sms = myAttributes.get(OTPUser.OTP_USER_PHONE_ATTRIBUTE_NAME);
                return (sms != null && !sms.isEmpty());
        }
        
        return false;
        
    }
    
    /**
     * Send the one-time password to the user who is in the process of logging
     * in.
     * 
     * @param context
     *     The UserContext of the user in the process of logging in.
     * 
     * @param authenticatedUser
     *     The AuthenticatedUser of the user who is in the process of logging in.
     * 
     * @throws GuacamoleException 
     */
    private void sendOTP(UserContext context, AuthenticatedUser authenticatedUser) throws GuacamoleException {
        
        OTPMailProvider otpMailProvider = new OTPMailProvider(otpConfig.getOTPMailSender(),
                otpConfig.getOTPMailServer(), otpConfig.getOTPMailPort(),
                otpConfig.getOTPMailEncryption() == OTPMailEncryption.SSL,
                otpConfig.getOTPMailEncryption() == OTPMailEncryption.TLS,
                otpConfig.getOTPMailAuthentication(), otpConfig.getOTPMailUsername(),
                otpConfig.getOTPMailPassword());
        
        // Get the required attributes for the one-time password
        Map<String, String> myAttributes = context.self().getAttributes();
        OTPCharacterClasses characters = otpConfig.getOTPCharacterClasses();
        int otpLength = otpConfig.getOTPLength();
        int otpTimeout = otpConfig.getDefaultOTPTimeout();
        
        // Look for user-configured timeout.
        if (myAttributes.containsKey(OTPUser.OTP_KEY_TIMEOUT_ATTRIBUTE_NAME))
            otpTimeout = Integer.parseInt(myAttributes.get(OTPUser.OTP_KEY_TIMEOUT_ATTRIBUTE_NAME));
        
        // Loop groups and find group-configured timeout, stopping at the first one.
        else {
            // Loop groups and return the first configure value
            Set<String> userGroups = authenticatedUser.getEffectiveUserGroups();
            Directory<UserGroup> directoryGroups = context.getPrivileged().getUserGroupDirectory();
            for (String userGroup : userGroups) {
                UserGroup thisGroup = directoryGroups.get(userGroup);
                if (thisGroup == null) {
                    continue;
                }

                Map<String, String> grpAttributes = thisGroup.getAttributes();
                if (grpAttributes != null
                        && grpAttributes.containsKey(OTPUserGroup.OTP_KEY_TIMEOUT_ATTRIBUTE_NAME)) {

                    otpTimeout = Integer.parseInt(grpAttributes.get(OTPUserGroup.OTP_KEY_TIMEOUT_ATTRIBUTE_NAME));
                    break;

                }
            }
        }
        
        OneTimePassword password = sessionMap.generate(authenticatedUser.getIdentifier(), otpLength, otpTimeout, characters);
        
        switch(getEffectiveOTPMethod(context, authenticatedUser)) {
            case EMAIL:
                List<InternetAddress> emailAddresses = new ArrayList<>();
                try {
                    if (myAttributes.containsKey(User.Attribute.EMAIL_ADDRESS)) {
                        String emailAddress = myAttributes.get(User.Attribute.EMAIL_ADDRESS);
                        if (emailAddress != null && !emailAddress.isEmpty())
                            emailAddresses.add(new InternetAddress(myAttributes.get(User.Attribute.EMAIL_ADDRESS)));
                    }

                    if (myAttributes.containsKey(OTPUser.OTP_USER_ALTERNATE_EMAIL_ATTRIBUTE_NAME)) {
                        String altEmailAddress = myAttributes.get(OTPUser.OTP_USER_ALTERNATE_EMAIL_ATTRIBUTE_NAME);
                        if (altEmailAddress != null && !altEmailAddress.isEmpty())
                            emailAddresses.add(new InternetAddress(myAttributes.get(OTPUser.OTP_USER_ALTERNATE_EMAIL_ATTRIBUTE_NAME)));
                    }
                }
                catch (AddressException e) {
                    throw new TranslatableGuacamoleSecurityException("Invalid recipient address.", "OTP.ERROR_INVALID_RECIPIENT_ADDRESS", e);
                }
                
                if (!emailAddresses.isEmpty()) {
                    try {
                        otpMailProvider.sendOTPMessage(emailAddresses, password);
                    }
                    catch (MessagingException e) {
                        LOGGER.error("Error sending OTP e-mail: {}", e.getMessage());
                        LOGGER.debug("Error sending OTP e-mail.", e);
                        throw new TranslatableGuacamoleSecurityException("Error sending one-time password e-mail.", "OTP.ERROR_SENDING_EMAIL", e);
                    }
                }

            case SMS:
                
        }
        
        
    }
    
    /**
     * 
     * @param context
     * @param authenticatedUser
     * @throws GuacamoleException 
     */
    public void verifyUser(UserContext context, AuthenticatedUser authenticatedUser) throws GuacamoleException {
        
        // Ignore anonymous users
        String username = authenticatedUser.getIdentifier();
        if (username.equals(AuthenticatedUser.ANONYMOUS_IDENTIFIER))
            return;
        
        // If OTP is disabled for this user bypass any further checks.
        if (otpDisabled(context, authenticatedUser))
            return;
        
        // Pull the original HTTP request used to authenticate
        Credentials credentials = authenticatedUser.getCredentials();
        HttpServletRequest request = credentials.getRequest();
        
        // Find parameter for OTP login
        String otpResponse = request.getParameter(OTP_FIELD_NAME);
        
        // If no parameter, send request
        if (otpResponse == null) {
            
            // If information missing and logins allowed continue
            if (!hasRequired(context, authenticatedUser))
                throw new TranslatableGuacamoleClientException(
                        "User is missing required attributes for One-Time Password.",
                        "OTP.INFO_USER_MISSING_ATTRIBUTES"
                );
            
            // First, generate the password and send it.
            sendOTP(context, authenticatedUser);
            
            // Send the user an indication that more credentials are required.
            throw new TranslatableGuacamoleInsufficientCredentialsException(
                    "In order to complete authentication you must enter the one-time password you received: ",
                    "OTP.INFO_PASSWORD_REQUIRED", new CredentialsInfo(Collections.<Field>singletonList(new PasswordField(OTP_FIELD_NAME)))
            );
        }
        
        // Parameter provided, so check it against sent value.
        if (sessionMap.checkPassword(username, otpResponse))
            return;
        
        // User-provided one-time password does not match.
        throw new TranslatableGuacamoleClientException("Invalid one-time password provided.", "OTP.INFO_VERIFICATION_FAILED");
        
    }
    
}
