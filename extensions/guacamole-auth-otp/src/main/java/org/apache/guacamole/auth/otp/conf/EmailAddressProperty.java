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

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.properties.GuacamoleProperty;

/**
 * A Guacamole Property that specifies a well-formed e-mail address.
 */
public abstract class EmailAddressProperty implements GuacamoleProperty<InternetAddress> {
 
    @Override
    public InternetAddress parseValue(String value) throws GuacamoleException {
        
        if (value == null)
            return null;
        
        try {
            return new InternetAddress(value);
        }
        catch (AddressException e) {
            throw new GuacamoleServerException("Value \"{}\" is not a valid "
                    + "e-mail address.", e);
        }
        
    }
    
}
