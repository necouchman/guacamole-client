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

package org.apache.guacamole.auth.otp.usergroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.guacamole.auth.otp.conf.OTPMessagingMethod;
import org.apache.guacamole.form.BooleanField;
import org.apache.guacamole.form.EnumField;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.form.NumericField;
import org.apache.guacamole.net.auth.DelegatingUserGroup;
import org.apache.guacamole.net.auth.UserGroup;

/**
 * An OTP implementation of a UserGroup which wraps an existing UserGroup, providing
 * additional OTP-related functionality to members of that group.
 */
public class OTPUserGroup extends DelegatingUserGroup {
    
    /**
     * The attribute associated with a group that disables the OTP requirement
     * for any users that are a member of that group, or are members of any
     * groups that are members of this group.
     */
    public static final String OTP_KEY_DISABLED_ATTRIBUTE_NAME = "guac-otp-disabled";
    
    /**
     * The attribute associated with a group that sets the method used to send
     * one-time passwords for any users that are members of the group, unless
     * overriden specifically for a user.
     */
    public static final String OTP_KEY_METHOD_ATTRIBUTE_NAME = "guac-otp-method";
    
    /**
     * The attribute that sets the timeout for one-time passwords for any users
     * that are members of this group or any sub-groups.
     */
    public static final String OTP_KEY_TIMEOUT_ATTRIBUTE_NAME = "guac-otp-timeout";
    
    /**
     * A list of all OTP-related attributes that can be stored for a group.
     */
    public static final List<String> OTP_GROUP_ATTRIBUTES = Arrays.asList(
            OTP_KEY_DISABLED_ATTRIBUTE_NAME,
            OTP_KEY_METHOD_ATTRIBUTE_NAME,
            OTP_KEY_TIMEOUT_ATTRIBUTE_NAME
    );
    
    /**
     * The string value used by OTP user groups attributes to represent the
     * boolean value "true".
     */
    public static final String TRUTH_VALUE = "true";
    
    /**
     * The form that contains fields for configuring OTP for members of this
     * group.
     */
    public static final Form OTP_USER_GROUP_CONFIG = new Form("otp-user-group-config",
            Arrays.asList(
                    new BooleanField(OTP_KEY_DISABLED_ATTRIBUTE_NAME, TRUTH_VALUE),
                    new EnumField(OTP_KEY_METHOD_ATTRIBUTE_NAME, OTPMessagingMethod.getMessagingMethods()),
                    new NumericField(OTP_KEY_TIMEOUT_ATTRIBUTE_NAME)
            )
    );
    
    /**
     * Create a new OTPUserGroup, wrapping the specified UserGroup and providing
     * additional OTP-related configuration.
     * 
     * @param userGroup 
     *     The UserGroup that will be wrapped by this OTPUserGroup.
     */
    public OTPUserGroup(UserGroup userGroup) {
        super(userGroup);
    }
    
    /**
     * Return the original UserGroup object that this instance wraps.
     * 
     * @return 
     *     The original UserGroup object that this instance wraps.
     */
    public UserGroup getUndecorated() {
        return this.getDelegateUserGroupGroup();
    }
    
    @Override
    public Map<String, String> getAttributes() {
        
        // Create a mutable copy of the attributes
        Map<String, String> attributes = new HashMap<>(super.getAttributes());
        
        // Add any missing attributes so they will be displayed on the management page.
        for (String attribute : OTP_GROUP_ATTRIBUTES) {
            if (!attributes.containsKey(attribute))
                attributes.put(attribute, null);
        }
        
        return Collections.unmodifiableMap(attributes);
        
    }
    
}
