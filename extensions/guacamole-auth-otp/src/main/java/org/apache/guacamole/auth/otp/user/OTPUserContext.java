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

import java.util.ArrayList;
import java.util.Collection;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.otp.usergroup.OTPUserGroup;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.DelegatingUserContext;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.User;
import org.apache.guacamole.net.auth.UserContext;
import org.apache.guacamole.net.auth.UserGroup;

/**
 * A UserContext implementation that wraps another UserContext and then provides
 * OTP-specific extensions to the underlying/wrapped UserContext.
 */
public class OTPUserContext extends DelegatingUserContext {
    
    /**
     * Create a new instance of this UserContext, wrapping it around the
     * specified userContext.
     * 
     * @param userContext 
     *     The UserContext object to wrap.
     */
    public OTPUserContext(UserContext userContext) {
        super(userContext);
    }
    
    @Override
    public Directory<User> getUserDirectory() throws GuacamoleException {
        return new DecoratingDirectory<User>(super.getUserDirectory()) {
            
            @Override
            public User decorate(User object) {
                if (object instanceof OTPUser)
                    return object;
                return new OTPUser(object);
            }
            
            @Override
            public User undecorate(User object) {
                if (object instanceof OTPUser)
                    return ((OTPUser) object).getUndecorated();
                return object;
            }
            
        };
    }
    
    @Override
    public Collection<Form> getUserAttributes() {
        Collection<Form> attributes = new ArrayList<>(super.getUserAttributes());
        attributes.add(OTPUser.OTP_USER_CONFIG);
        return attributes;
    }
    
    @Override
    public Directory<UserGroup> getUserGroupDirectory() throws GuacamoleException {
        return new DecoratingDirectory<UserGroup>(super.getUserGroupDirectory()) {
            
            @Override
            public UserGroup decorate(UserGroup object) {
                if (object instanceof OTPUserGroup)
                    return object;
                return new OTPUserGroup(object);
            }
            
            @Override
            public UserGroup undecorate(UserGroup object) {
                if (object instanceof OTPUserGroup)
                    return ((OTPUserGroup) object).getUndecorated();
                return object;
            }
            
        };
    }
    
    @Override
    public Collection<Form> getUserGroupAttributes() {
        Collection<Form> attributes = new ArrayList<>(super.getUserGroupAttributes());
        attributes.add(OTPUserGroup.OTP_USER_GROUP_CONFIG);
        return attributes;
    }
    
}
