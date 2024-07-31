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
package org.apache.guacamole.auth.restrict;

import org.apache.guacamole.calendar.RestrictionType;
import org.apache.guacamole.net.auth.Attributes;

/**
 * An interface which defines methods that apply to items that can have
 * restrictions applied to them.
 */
public interface Restrictable extends Attributes {
    
    /**
     * The name of the attribute that contains a list of weekdays and times (UTC)
     * that this object can be used. The presence of values within this
     * attribute will automatically restrict use of the object at any
     * times that are not specified.
     */
    public static final String RESTRICT_TIME_ALLOWED_ATTRIBUTE_NAME = "guac-restrict-time-allowed";
    
    /**
     * The name of the attribute that contains a list of weekdays and times (UTC)
     * that this object cannot be accessed. Denied times will always take
     * precedence over allowed times. The presence of this attribute without
     * guac-restrict-time-allowed will deny use only during the times listed
     * in this attribute, allowing access at all other times. The presence of
     * this attribute along with the guac-restrict-time-allowed attribute will
     * deny use at any times that overlap with the allowed times.
     */
    public static final String RESTRICT_TIME_DENIED_ATTRIBUTE_NAME = "guac-restrict-time-denied";
    
    /**
     * The name of the attribute that contains a list of hosts from which this
     * object may be used - either for login (User) or connection (Connection or
     * ConnectionGroup). The presence of this attribute will restrict
     * use for clients from the list of hosts contained in the attribute,
     * subject to further restriction by the guac-restrict-hosts-denied attribute.
     */
    public static final String RESTRICT_HOSTS_ALLOWED_ATTRIBUTE_NAME = "guac-restrict-hosts-allowed";
    
    /**
     * The name of the attribute that contains a list of hosts from which this
     * object may be used - either for login (User) or connection (Connection or
     * ConnectionGroup). The presence of this attribute, absent the
     * guac-restrict-hosts-allowed attribute, will allow use from all hosts 
     * except the ones listed in this attribute. The presence of this attribute
     * coupled with the guac-restrict-hosts-allowed attribute will block use
     * from any IPs in this list, overriding any that may be allowed.
     */
    public static final String RESTRICT_HOSTS_DENIED_ATTRIBUTE_NAME = "guac-restrict-hosts-denied";
    
    /**
     * The name of the attribute that contains the absolute UTC date and time
     * on which the object ceases to be available. If undefined, empty, or
     * invalid, use of the object will not be restricted by an absolute
     * expiration. If a valid UTC date and time is provided, access to the
     * object will not be allowed after the given date and time.
     */
    public static final String RESTRICT_DATETIME_EXPIRATION = "guac-restrict-expiration";
    
    /**
     * The name of the attribute that contains the maximum number of seconds
     * for which this object is available, after which the login or connection
     * will be terminated. If undefined, empty, or invalid, use of the object
     * will not be restricted by a duration of time.
     */
    public static final String RESTRICT_DURATION = "guac-restrict-duration";
    
    /**
     * Return the restriction state for this restrictable object at the
     * current date and time. By default this function will return an implict
     * allow status, as absence of time-based restriction data is intended to
     * allow use of the object.
     * 
     * @return 
     *     The restriction status for the current date and time.
     */
    default public RestrictionType getCurrentTimeRestriction() {
        return RestrictionType.IMPLICIT_ALLOW;
    }
    
    /**
     * Return the restriction state for this restrictable object for the host
     * from which the current user is logged in. By default this function will
     * return an implicit allow, as the absence of host-based restriction data
     * means that use of the object should be allowed.
     * 
     * @return 
     *     The restriction status for the host from which the current user is
     *     logged in.
     */
    default public RestrictionType getCurrentHostRestriction() {
        return RestrictionType.IMPLICIT_ALLOW;
    }
    
    /**
     * Return the restriction state for this restrictable object based on the
     * presence of a defined expiration time as compared to the current date
     * and time. By default return an implicit allow, as items do not expire if
     * no (valid) data is provided.
     * 
     * @return 
     *     The restriction status for this object based on the data configured
     *     for this object.
     */
    default public RestrictionType getExpirationRestriction() {
        return RestrictionType.IMPLICIT_ALLOW;
    }
    
    /**
     * Return the maximum number of seconds for which this object can be used,
     * or -1 if no restriction is present. By default -1 will be returned,
     * and use of the object will not be restricted to a certain duration.
     * 
     * @return 
     *     The maximum number of seconds for which the object can be used,
     *     or -1 if no restriction is present.
     */
    default public int getDurationRestriction() {
        return -1;
    }
    
    /**
     * Returns true if the current item is available based on the restrictions
     * for the given implementation of this interface, or false if the item is
     * not currently available. The default implementation checks current time
     * and host restrictions, allowing if both those restrictions allow access.
     * 
     * @return 
     *     true if the item is available, otherwise false.
     */
    default public boolean isAvailable() {
        return (getCurrentTimeRestriction().isAllowed() && getCurrentHostRestriction().isAllowed());
    }
    
}