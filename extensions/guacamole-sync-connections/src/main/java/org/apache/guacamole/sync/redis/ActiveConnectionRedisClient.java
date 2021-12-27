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

package org.apache.guacamole.sync.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.guacamole.auth.sync.connection.SynchronizedActiveConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client that stores ActiveConnection objects in a Redis server.
 */
public class ActiveConnectionRedisClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveConnectionRedisClient.class);
    
    private final RedisClient redisClient;
    
    private final StatefulRedisConnection<String, SynchronizedActiveConnection> redisConnection;
    
    public ActiveConnectionRedisClient() {
        LOGGER.debug(">>>SYNC<<< Setting up the Redis client.");
        this.redisClient = RedisClient.create("redis://localhost:6379/0");
        this.redisConnection = this.redisClient.connect(new ActiveConnectionCodec());
    }
    
    public void closeConnection() {
        LOGGER.debug(">>>SYNC<<< Shutting down the Redis client.");
        this.redisConnection.close();
        redisClient.shutdown();
    }
    
    public void putActiveConnection(SynchronizedActiveConnection activeConnection) {
        LOGGER.debug(">>>SYNC<<< Putting ActiveConnection in Redis: {}", activeConnection.getIdentifier());
        RedisCommands<String, SynchronizedActiveConnection> putCommands = redisConnection.sync();
        putCommands.set(activeConnection.getIdentifier(), activeConnection);
    }
    
    public void updateActiveConnection(SynchronizedActiveConnection activeConnection) {
        LOGGER.debug(">>>SYNC<<< Updating ActiveConneciton in Redis: {}", activeConnection.getIdentifier());
        RedisCommands<String, SynchronizedActiveConnection> putCommands = redisConnection.sync();
        putCommands.del(activeConnection.getIdentifier());
        putCommands.set(activeConnection.getIdentifier(), activeConnection);
    }
    
    public void removeActiveConnection(String identifier) {
        LOGGER.debug(">>>SYNC<<< Removing ActiveConnection in Redis: {}", identifier);
        RedisCommands<String, SynchronizedActiveConnection> delCommands = redisConnection.sync();
        delCommands.del(identifier);
    }
    
    public SynchronizedActiveConnection getActiveConnection(String identifier) {
        LOGGER.debug(">>>SYNC<<< Retrieving ActiveConnection from Redis: {}", identifier);
        RedisCommands<String, SynchronizedActiveConnection> getCommands = redisConnection.sync();
        return getCommands.get(identifier);
    }
    
    public Set<String> getAllKeys() {
        RedisCommands<String, SynchronizedActiveConnection> getCommands = redisConnection.sync();
        return new HashSet<>(getCommands.keys("*"));
    }
    
    public Collection<SynchronizedActiveConnection> getActiveConnections(Collection<String> identifiers) {
        Collection<SynchronizedActiveConnection> activeConnections = new ArrayList<>();
        RedisCommands<String, SynchronizedActiveConnection> getCommands = redisConnection.sync();
        identifiers.forEach(identifier -> {
            activeConnections.add(getCommands.get(identifier));
        });
        return activeConnections;
    }
    
}
