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

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import org.apache.guacamole.auth.otp.conf.OTPCharacterClasses;

/**
 * A class for generating a random string to be used as a one-time password.
 */
public class RandomString {
    
    /**
     * Upper-case characters to use for random strings.
     */
    private final static String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /**
     * Lower-case characters to use for random strings.
     */
    private final static String LOWER = UPPER.toLowerCase(Locale.ROOT);
    
    /**
     * Numeric digits to use for random strings.
     */
    private final static String NUMERIC = "1234567890";
    
    /**
     * Symbols to use for random strings.
     */
    private final static String SYMBOLS = "!@#$%^&*()[]{}:<>-+=";
    
    /**
     * The array of characters to use for this random generator.
     */
    private final char[] useCharacters;
    
    /**
     * Initialize a new RandomString class restricted to the given classes
     * of possible OTP characters.
     * 
     * @param charClasses 
     *     The classes of characters to which this random string generator
     *     should be restricted.
     */
    public RandomString(OTPCharacterClasses charClasses) {
        
        String useString = NUMERIC;
        
        switch (charClasses) {
            
            case ALL:
                useString += UPPER + LOWER + SYMBOLS;
                break;
            
            case ALPHA:
                useString = UPPER + LOWER;
                break;
            
            case ALPHANUMERIC:
                useString += UPPER + LOWER;
                break;
                
        }
        
        useCharacters = useString.toCharArray();
        
    }
    
    /**
     * Return a new random string of the given length.
     * 
     * @param length
     *     The length of the string to be generated, which should be at least
     *     1.
     * 
     * @return 
     *     A random string of the given length.
     */
    public String getRandomString(int length) {
        
        if (length < 1)
            throw new IllegalArgumentException("Length must be at least 1.");
        
        Random random = new SecureRandom();
        char[] buf = new char[length];
        
        for (int i = 0; i < buf.length; i++)
            buf[i] = useCharacters[random.nextInt(useCharacters.length)];
        
        return new String(buf);
    }
    
    /**
     * A static method for getting a random string of the given length with the
     * specified character classes.
     * 
     * @param length
     *     The length of the random string.
     * 
     * @param characters
     *     The classes of characters to limit the random string to.
     * 
     * @return 
     *     A random string of characters.
     */
    public static String getRandomString(int length, OTPCharacterClasses characters) {

        return (new RandomString(characters)).getRandomString(length);
        
    }
    
}
