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
package org.apache.guacamole.template.connectiongroup;

/**
 * A connection group that delegates functionality to an underlying
 * connection group, and provides a mechanism for defining and storing
 * arbitrary tokens that can be used by connections within the group.
 */
public class TemplateConnectionGroup extends DelegatingConnectionGroup {
    
    /**
     * The prefix used to store the tokens in the database.
     */
    public static final String TEMPLATE_ATTRIBUTE_PREFIX = "guac-template-";
    
    /**
     * The prefix used to access the tokens within connection parameters.
     */
    public static final String TEMPLATE_TOKEN_PREFIX = "GUAC_TEMPLATE_";
    
    public TemplateConnectionGroup(ConnectionGroup connectionGroup) {
        super(connectionGroup);
    }
    
    public ConnectionGroup getUndecorated() {
        return getDelegateConnectionGroup();
    }
    
    
    
}
