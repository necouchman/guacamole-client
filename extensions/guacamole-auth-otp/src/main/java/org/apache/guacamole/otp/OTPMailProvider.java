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

import jakarta.mail.Authenticator;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.guacamole.language.TranslatableMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that provides the capability for sending one-time passwords via
 * e-mail.
 */
public class OTPMailProvider {
    
    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OTPMailProvider.class);
    
    /**
     * The name of the variable that will be used in the translation message to
     * substitute the one-time password.
     */
    public static final String MSG_OTP_VARIABLE = "OTP";
    
    /**
     * The name of the variable that will be used in the translation message to
     * substitute the expiration date of the one-time password.
     */
    public static final String MSG_EXPIRES_VARIABLE = "EXPIRES";
    
    /**
     * The Properties object that stores the SMTP configuration.
     */
    private final Properties mailProperties = new Properties();
    
    /**
     * The default TCP port to use if one is not specified.
     */
    private static final int DEFAULT_SMTP_PORT = 25;
         
    /**
     * Whether or not to attempt to authenticate to the SMTP server.
     */
    private final boolean authenticate;
    
    /**
     * The username to use for authenticating to the SMTP server, or null if
     * authentication will not be used.
     */
    private final String username;
    
    /**
     * The password to use to authenticate to the SMTP server, or null if
     * authentication will not be used.
     */
    private final String password;
    
    /**
     * Create a new one-time password e-mail provider with the specified
     * parameters.
     * 
     * @param smtpSender
     *     The sender address for the OTP messages.
     * 
     * @param smtpServer
     *     The SMTP e-mail server to use to send the messages.
     * 
     * @param smtpPort
     *     The TCP port to use to connect to the SMTP server.
     * 
     * @param useSSL
     *     True if SSL connections to the SMTP server should be attempted,
     *     otherwise false.
     * 
     * @param useTLS
     *     True if TLS connections to the SMTP server should be attempted,
     *     otherwise false.
     * 
     * @param authenticate
     *     True if authentication should be attempted with the SMTP server,
     *     otherwise false.
     * 
     * @param username
     *     The username for authentication to the SMTP server, or null if
     *     authentication is not in use.
     * 
     * @param password 
     *     The password for authentication to the SMTP server, or null if
     *     authentication is not in use.
     */
    public OTPMailProvider(InternetAddress smtpSender, String smtpServer,
            int smtpPort, boolean useSSL, boolean useTLS, boolean authenticate,
            String username, String password) {

        this.authenticate = authenticate;
        this.username = username;
        this.password = password;
        
        // Set the SMTP properties
        mailProperties.setProperty("mail.smtp.host", smtpServer);
        mailProperties.setProperty("mail.smtp.from", smtpSender.toString());
        mailProperties.setProperty("mail.smtp.port", Integer.toString(smtpPort));
        mailProperties.setProperty("mail.smtp.auth", Boolean.toString(authenticate));
        mailProperties.setProperty("mail.smtp.starttls.enable", Boolean.toString(useTLS));
        mailProperties.setProperty("mail.smtp.ssl.enable", Boolean.toString(useSSL));
        
    }
    
    /**
     * Create a new one-time password e-mail provider with the specified sender
     * and SMTP server. The default TCP port will be used, SSL and TLS will be
     * disabled, and authentication will be disabled and username and password
     * will be null.
     * 
     * @param smtpSender
     *     The sender address to use for e-mail messages.
     * 
     * @param smtpServer 
     *     The SMTP server to use for e-mail messages.
     */
    public OTPMailProvider(InternetAddress smtpSender, String smtpServer) {
        this(smtpSender, smtpServer, DEFAULT_SMTP_PORT, false, false, false, null, null);
    }
    
    /**
     * Create a new one-time password e-mail provider with the specified sender
     * address, SMTP server, and SMTP port. SSL and TLS will be disabled, and
     * authentication will not be attempted and the username and password will
     * be set to null.
     * 
     * @param smtpSender
     *     The sender address to use for e-mail messages.
     * 
     * @param smtpServer
     *     The SMTP server to use for sending e-mail messages.
     * 
     * @param smtpPort
     *     The TCP port to use to communicate with the SMTP server.
     */
    public OTPMailProvider(InternetAddress smtpSender, String smtpServer,
            int smtpPort) {
        this(smtpSender, smtpServer, smtpPort, false, false, false, null, null);
    }
    
    /**
     * Create a new one-time password e-mail provider with the specified sender
     * address, SMTP server, SMTP port, and username and password. Authentication
     * will be enabled, but SSL and TLS will be disabled.
     * 
     * @param smtpSender
     *     The sender address to use for the OTP messages.
     * 
     * @param smtpServer
     *     The SMTP server to use to send the OTP messages.
     * 
     * @param smtpPort
     *     The TCP port to use to connect to the SMTP server.
     * 
     * @param username
     *     The username to use for authenticating to the SMTP server.
     * 
     * @param password 
     *     The password to use for authenticating to the SMTP server.
     */
    public OTPMailProvider(InternetAddress smtpSender, String smtpServer,
            int smtpPort, String username, String password) {
        this(smtpSender, smtpServer, smtpPort, false, false, true, username, password);
    }
    
    public void sendOTPMessage(List<InternetAddress> recipients, OneTimePassword oneTimePassword) throws MessagingException {
        
        Map<String, String> msgVariables = new HashMap<>();
        msgVariables.put(MSG_OTP_VARIABLE, oneTimePassword.getPassword());
        msgVariables.put(MSG_EXPIRES_VARIABLE, oneTimePassword.getExpirationString());
        
        // This is all because:
        // https://github.com/jakartaee/mail-api/issues/665
        Thread t = Thread.currentThread();
        ClassLoader orig = t.getContextClassLoader();
        t.setContextClassLoader(getClass().getClassLoader());
        try {

            Session mailSession;
            if (authenticate) {
                Authenticator smtpAuth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                };
                mailSession = Session.getInstance(mailProperties, smtpAuth);
            }
            else {
                mailSession = Session.getInstance(mailProperties);
            }
        
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setRecipients(Message.RecipientType.TO, recipients.toArray(new InternetAddress[0]));
            
            /**
            msg.setSubject(new TranslatableMessage("OTP.INFO_OTP_MAIL_SUBJECT").toString());
            msg.setContent(new TranslatableMessage("OTP.INFO_OTP_MAIL_BODY", msgVariables).toString(), "text/plain");
            **/
            
            msg.setSubject("Guacamole One-time Password");
            msg.setContent("Your one-time password is: " + oneTimePassword.getPassword() +  "\n\nThe password will expire on: " + oneTimePassword.getExpirationString(), "text/plain");

            Transport.send(msg);
        
        }
        finally {
            t.setContextClassLoader(orig);
        }
        
    }
    
}
