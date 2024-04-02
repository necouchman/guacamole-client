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

package org.apache.guacamole.directory.util;

/**
 * A data type representing possible protocols and settings.
 */
public enum ServiceNowConnectionProtocol {
    
    /**
     * The Kubernetes protocol.
     */
    KUBERNETES(443),
    
    /**
     * The RDP protocol.
     */
    RDP(3389),
    
    /**
     * The SSH protocol.
     */
    SSH(22),
    
    /**
     * The Telnet protocol.
     */
    TELNET(23),
    
    /**
     * The VNC protocol.
     */
    VNC(5900);
    
    /**
     * The default port associated with this protocol.
     */
    private final int port;
    
    /**
     * Create a new instance of the Service Now protocol, with the given
     * default port.
     * 
     * @param port 
     *     The default TCP port for the protocol.
     */
    ServiceNowConnectionProtocol(int port) {
        this.port = port;
    }
    
    /**
     * Return the default port for this protocol.
     * 
     * @return 
     *     The default port to use for this protocol.
     */
    public int getPort() {
        return port;
    }
    
}
