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

package org.apache.guacamole.auth.otp;

import com.google.inject.Singleton;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.guacamole.auth.otp.conf.OTPCharacterClasses;
import org.apache.guacamole.otp.OneTimePassword;
import org.apache.guacamole.otp.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A map that contains the currently-valid one-time-password for a user.
 */
@Singleton
public class OTPSessionMap {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OTPSessionMap.class);
    
    /**
     * A map containing a list of user identifiers and one-time password
     * sessions.
     */
    private final Map<String, OneTimePassword> sessionMap = new ConcurrentHashMap<>();
    
    /**
     * Executor service which runs the one-time password cleanup task.
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    
    /**
     * Create a new session map for one-time passwords and start up the
     * cleanup task.
     */
    public OTPSessionMap() {
        // Clean up expired tokens once every minute.
        executor.scheduleAtFixedRate(new ExpiredOTPTask(), 1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * Task which iterates through all one-time passwords, removing those that
     * have expired.
     */
    private class ExpiredOTPTask implements Runnable {
        
        private void removeExpiredOTPs() {

            // Get start time of session check time
            long sessionCheckStart = System.currentTimeMillis();
            
            LOGGER.debug("Checking for expired one-time passwords...");
            
            // Loop through current one-time passwords in the map.
            Iterator<Map.Entry<String, OneTimePassword>> entries = sessionMap.entrySet().iterator();            
            while (entries.hasNext()) {
                Map.Entry<String, OneTimePassword> entry = entries.next();
                OneTimePassword password = entry.getValue();
                
                // Check validity, and remove if it has expired
                if (!password.isValid()) {
                    LOGGER.debug("Removing one-time password for user \"{}\".", entry.getKey());
                    entries.remove();
                }
                
            }
                
            // Log completion and duration
            LOGGER.debug("One-time password check completed in {} ms.",
                    System.currentTimeMillis() - sessionCheckStart);
        }

        @Override
        public void run() {

            // The evictExpiredOrInvalidSessions() function should already
            // automatically handle and log all unexpected internal errors,
            // but wrap the entire call in a try/catch plus additional logging
            // to ensure that absolutely no errors can result in the entire
            // thread dying
            try {
                removeExpiredOTPs();
            }
            catch (Throwable t) {
                LOGGER.error("An unexpected error occurred while running the "
                        + "clean-up task for one-time passwords.", t);
            }

        }

    }
    
    /**
     * Add an OTP session to the map with the given username and OTP session. If
     * a previous value is present for the given user identifier it will be
     * replaced with this value.
     * 
     * @param identifier
     *     The user identifier for this map.
     * 
     * @param oneTimePassword
     *     The one-time password 
     */
    public void add(String identifier, OneTimePassword oneTimePassword) {
        sessionMap.put(identifier, oneTimePassword);
    }
    
    /**
     * Generate a new one-time password with the given characteristics, for
     * the given username, and store it in the session map.
     * 
     * @param identifier
     *     The identifier of the user that this one-time password belongs to.
     * 
     * @param length
     *     The length of the password to generate.
     * 
     * @param timeout
     *     The number of seconds that this password should be valid for.
     * 
     * @param characters 
     *     The character classes that the password should include.
     * 
     * @return
     *     The generated password that has been stored in the session map.
     */
    public OneTimePassword generate(String identifier, int length, int timeout, OTPCharacterClasses characters) {
        OneTimePassword password = new OneTimePassword(RandomString.getRandomString(length, characters), timeout);
        sessionMap.put(identifier, password);
        
        return password;
    }
    
    /**
     * Check the given password for the user with the given identifier, returning
     * true if the password is in the map and matches, otherwise false. If the
     * password check is successful it is removed from the map.
     * 
     * @param identifier
     *     The identifier of the user to check the password for.
     * 
     * @param password
     *     The password to check.
     * 
     * @return 
     *     True if the entry is in the map and matches, otherwise false.
     */
    public boolean checkPassword(String identifier, String password) {
        
        // No entry exists in the map, so the check fails.
        if (!sessionMap.containsKey(identifier))
            return false;

        // Return the status of the password check.
        return sessionMap.get(identifier).checkOtp(password);
    }
    
    public void invalidate(String identifier) {
        sessionMap.remove(identifier);
    }
    
    /**
     * Execute tasks that shut down the session map, cleaning up data and
     * stopping running tasks.
     */
    public void shutdown() {
        
        // Stop the cleanup task
        executor.shutdown();
        
        // Clear the session map.
        sessionMap.clear();
        
    }
    
}
