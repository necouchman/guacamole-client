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

package org.apache.guacamole.favorites.user;

import java.util.Collections;
import java.util.Map;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.ConnectionGroup;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.TokenInjectingUserContext;
import org.apache.guacamole.net.auth.User;
import org.apache.guacamole.net.auth.UserContext;

/**
 * UserContext implementation that automatically defines ActivityLogs for
 * files that relate to history entries.
 */
public class FavoritesUserContext extends TokenInjectingUserContext {

    /**
     * The directory that stores the single connection group that will contain
     * the favorites for the user.
     */
    private final Directory<ConnectionGroup> = new SimpleDirectory<>();
    
    /**
     * Creates a new FavoritesUserContext that wraps the given UserContext,
     * automatically associating history entries with ActivityLogs based on
     * related files (session recordings, typescripts, etc.).
     *
     * @param context
     *     The UserContext to wrap.
     */
    public FavoritesUserContext(UserContext context) {
        super(context);
    }
    
    @Override
    public Directory<ConnectionGroup> getConnectionGroupDirectory() throws GuacamoleException {
        Directory<ConnectionGroup> favorites = new SimpleDirectory<>();
    }
    
    @Override
    public Directory<User> getUserDirectory() throws GuacamoleException {
        return new DecoratingDirectory<User>(super.getUserDirectory()) {
            
            @Override
            protected User decorate(User object) {
                if (object instanceof FavoriteUser)
                    return object;
                return new FavoriteUser(object);
            }
            
            @Override
            protected Connection undecorate(Connection object) {
                if (object instanceof FavoriteUser)
                    return ((FavoriteUser) object).getWrappedUser();
                return object;
            }
            
        }
    }
    
    @Override
    public Directory<UserGroup> getUserGroupDirectory() throws GuacamoleException {
        return new DecoratingDirectory<UserGroup>(super.getUserGroupDirectory()) {
            
            @Override
            protected UserGroup decorate(UserGroup object) {
                if (object instanceof FavoriteUserGroup)
                    return object;
                return new FavoriteUserGroup(object);
            }
            
            @Override
            protected UserGroup undecorate(UserGroup object) {
                if (object instanceof FavoriteUserGroup)
                    return ((FavoriteUserGroup) object).getWrappedUserGroup();
                return object;
            }
            
        }
    }

}
