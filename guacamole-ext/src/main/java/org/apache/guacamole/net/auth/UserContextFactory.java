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

package org.apache.guacamole.net.auth;

/**
 * A factory for creating decorated UserContext instances when Guice injection
 * is required.
 */
public interface UserContextFactory {
    
    /**
     * Returns a new instance of a UserContext implementation that decorates
     * the given UserContext instance.
     *
     * @param userContext
     *     The UserContext instance to decorate.
     *
     * @return
     *     A new UserContext instance decorated with additional functionality.
     */
    UserContext create(UserContext userContext);
    
}
