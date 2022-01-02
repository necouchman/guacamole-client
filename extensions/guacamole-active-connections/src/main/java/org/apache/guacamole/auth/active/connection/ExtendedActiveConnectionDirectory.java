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

package org.apache.guacamole.auth.active.connection;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.simple.SimpleConnectionGroup;

/**
 *
 * @author nick
 */
public class ExtendedActiveConnectionDirectory extends DecoratingDirectory<ActiveConnection> {

    private final SimpleConnectionGroup rootGroup;
    
    public static final String ROOT_GROUP_ID = "ExtendedActiveConnections";
    
    public static final String ROOT_GROUP_NAME = "Extended Active Connections";
    
    public ExtendedActiveConnectionDirectory(Directory<ActiveConnection> directory) {
        super(directory);
        this.rootGroup = new SimpleConnectionGroup(
                ROOT_GROUP_ID,
                ROOT_GROUP_NAME,
                new ArrayList<>(),
                Collections.<String>emptySet()
        );
    }
    
    @Override
    protected ActiveConnection decorate(ActiveConnection object) {
        if (object instanceof ExtendedActiveConnection)
            return object;
        return new ExtendedActiveConnection(object);
    }

    @Override
    protected ActiveConnection undecorate(ActiveConnection object) {
        if (object instanceof ExtendedActiveConnection)
            return ((ExtendedActiveConnection)object).getUndecorated();
        return object;
    }
    
    public ConnectionGroup getRootConnectionGroup() {
        return rootGroup;
    }
    
}
