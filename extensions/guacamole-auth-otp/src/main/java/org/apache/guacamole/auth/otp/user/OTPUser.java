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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.guacamole.auth.otp.conf.OTPMessagingMethod;
import org.apache.guacamole.form.BooleanField;
import org.apache.guacamole.form.EmailField;
import org.apache.guacamole.form.EnumField;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.form.NumericField;
import org.apache.guacamole.form.TextField;
import org.apache.guacamole.net.auth.DelegatingUser;
import org.apache.guacamole.net.auth.User;

/**
 * An OTP-specific User implementation that delegates functionality to an
 * underlying User object, adjusting that functionality for the purposes
 * of providing One-time password authentication.
 */
public class OTPUser extends DelegatingUser {
    
    /**
     * The name of the attribute associated with a user that disables the OTP
     * requirement for the user.
     */
    public static final String OTP_KEY_DISABLED_ATTRIBUTE_NAME = "guac-otp-disabled";
    
    /**
     * The name of the attribute associated with a user that sets the method
     * used to send one-time passwords to that user.
     */
    public static final String OTP_KEY_METHOD_ATTRIBUTE_NAME = "guac-otp-method";
    
    /**
     * The name of the attribute that sets the timeout for one-time passwords
     * for the user.
     */
    public static final String OTP_KEY_TIMEOUT_ATTRIBUTE_NAME = "guac-otp-timeout";
    
    /**
     * The name of the attribute that stores an alternate e-mail address to
     * which the user's one-time password may be sent, if the e-mail stored in
     * the underlying user profile cannot be used or does not exist.
     */
    public static final String OTP_USER_ALTERNATE_EMAIL_ATTRIBUTE_NAME = "guac-otp-email";
    
    /**
     * The name of the attribute that stores the phone number that should be
     * used for SMS-based one-time password authentication.
     */
    public static final String OTP_USER_PHONE_ATTRIBUTE_NAME  = "guac-otp-phone";
    
    /**
     * A list of all OTP-related attributes that can be stored for a user.
     */
    public static final List<String> OTP_USER_ATTRIBUTES = Arrays.asList(
            OTP_KEY_DISABLED_ATTRIBUTE_NAME,
            OTP_KEY_METHOD_ATTRIBUTE_NAME,
            OTP_KEY_TIMEOUT_ATTRIBUTE_NAME,
            OTP_USER_ALTERNATE_EMAIL_ATTRIBUTE_NAME,
            OTP_USER_PHONE_ATTRIBUTE_NAME
    );
    
    /**
     * The string value used by OTP user attributes to represent the boolean
     * value "true".
     */
    public static final String TRUTH_VALUE = "true";
    
    /**
     * The form that contains fields for configuring OTP for Users.
     */
    public static final Form OTP_USER_CONFIG = new Form("otp-user-config",
            Arrays.asList(
                    new BooleanField(OTP_KEY_DISABLED_ATTRIBUTE_NAME, TRUTH_VALUE),
                    new EnumField(OTP_KEY_METHOD_ATTRIBUTE_NAME, OTPMessagingMethod.getMessagingMethods()),
                    new NumericField(OTP_KEY_TIMEOUT_ATTRIBUTE_NAME),
                    new EmailField(OTP_USER_ALTERNATE_EMAIL_ATTRIBUTE_NAME),
                    new TextField(OTP_USER_PHONE_ATTRIBUTE_NAME)
            )
    );
    
    /**
     * Create a new instance of the OTPUser, wrapping the given User object.
     * 
     * @param user 
     *     The User object to wrap.
     */
    public OTPUser(User user) {
        super(user);
    }
    
    /**
     * Return the original, unwrapped User object.
     * 
     * @return 
     *     The original, unwrapped User object.
     */
    public User getUndecorated() {
        return getDelegateUser();
    }
    
    @Override
    public Map<String, String> getAttributes() {
        
        // Create a mutable copy of the attributes
        Map<String, String> attributes = new HashMap<>(super.getAttributes());
        
        // Add any missing attributes so they will be displayed on the management page.
        for (String attribute : OTP_USER_ATTRIBUTES) {
            if (!attributes.containsKey(attribute))
                attributes.put(attribute, null);
        }
        
        return Collections.unmodifiableMap(attributes);
        
    }
}
