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

package org.apache.guacamole.otp;

import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A class that contains a one-time password with a limited lifetime.
 */
public class OneTimePassword {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OneTimePassword.class);
    
    /**
     * The password.
     */
    private final String password;
    
    /**
     * When the one-time password should expire.
     */
    private final Date expires;
    
    /**
     * Create a new One-Time password session with the given one-time password
     * and expiration date.
     * 
     * @param password
     *     The one-time password.
     * 
     * @param expires 
     *     The date at which the one-time password expires.
     */
    public OneTimePassword(String password, Date expires) {
        this.password = password;
        this.expires = expires;
    }
    
    /**
     * Create a new one-time password session with the given one-time password
     * that is valid for the specified number of seconds.
     * 
     * @param password
     *     The one-time password.
     * 
     * @param timeout 
     *     The validity time out in seconds.
     */
    public OneTimePassword(String password, int timeout) {
        this.password = password;
        this.expires = Date.from(Instant.now().plusSeconds(timeout));
    }
    
    /**
     * Return true if the one-time password is currently valid (has not expired)
     * otherwise false.
     * 
     * @return 
     *     True if the one-time password is currently valid, otherwise false.
     */
    public boolean isValid() {
        Date nowDate = new Date();
        LOGGER.debug(">>> Is {} before {}?", nowDate.toString(), expires.toString());
        return nowDate.before(expires);
    }
    
    /**
     * Return true if the one-time password is valid and the value given matches
     * the stored value, or false if either of these conditions are not met.
     * 
     * @param tryPassword
     *     The one-time password to check against the stored value.
     * 
     * @return 
     *     True if the one-time password is valid and matches, otherwise false.
     */
    public boolean checkOtp(String tryPassword) {
        LOGGER.debug(">>> Checking {} against {}", tryPassword, password);
        return isValid() && password.equals(tryPassword);
    }
    
    /**
     * Return the String value of the one-time password.
     * 
     * @return 
     *     The String value of the one-time password.
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Returns the expiration date of this one-time password.
     * 
     * @return 
     *     The expiration date of this one-time password.
     */
    public Date getExpiration() {
        return expires;
    }
    
    /**
     * Returns the String representation of the expiration date of this one-time
     * password.
     * 
     * @return 
     *     The String representation of the expiration date of this one-time
     *     password.
     */
    public String getExpirationString() {
        return expires.toString();
    }
    
}
