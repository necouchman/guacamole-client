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
package org.apache.guacamole.template.user;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.ConnectionGroup;

/**
 * A UserContext implementation that allows for connection templates via
 * the use of token injection.
 */
public class TemplateUserContext extends TokenInjectingUserContext {
    
    /**
     * Create a new UserContext that wraps the given UserContext, providing
     * the ability to define tokens that can be used by the connections contained
     * within a connection group.
     * 
     * @param context 
     *     The UserContext to wrap.
     */
    public TemplateUserContext(UserContext context) {
        super(context);
    }
    
    @Override
    public Directory<ConnectionGroup> getConnectionGroupDirectory() {
        return new DecoratingDirectory<ConnectionGroup>(super.getConnectionGroupDirectory()) {

            @Override
            protected Connection decorate(ConnectionGroup object) {
                return new TemplateConnectionGroup(object);
            }

            @Override
            protected Connection undecorate(ConnectionGroup object) throws GuacamoleException {
                return ((TemplateConnectionGroup) object).getUndecorated();
            }
        }
    }
    
}
