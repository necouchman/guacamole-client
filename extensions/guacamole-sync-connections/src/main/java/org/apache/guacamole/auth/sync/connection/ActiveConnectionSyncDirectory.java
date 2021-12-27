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

package org.apache.guacamole.auth.sync.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.ActiveConnection;
import org.apache.guacamole.net.auth.DecoratingDirectory;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.sync.redis.ActiveConnectionRedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A directory that stores ActiveConnection objects and synchronizes them across
 * multiple servers by storing them in a shared directory space.
 */
public class ActiveConnectionSyncDirectory extends DecoratingDirectory<ActiveConnection> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveConnectionSyncDirectory.class);
    
    private final ActiveConnectionRedisClient client;
    
    private final Directory<ActiveConnection> directory;
    
    /**
     * Executor service which runs the periodic active connection sync.
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    
    public ActiveConnectionSyncDirectory(Directory<ActiveConnection> directory) {
        super(directory);
        this.directory = directory;
        this.client = new ActiveConnectionRedisClient();
        
        // Start sync process, once per minute
        executor.scheduleAtFixedRate(new ActiveConnectionSyncTask(), 1, 1, TimeUnit.MINUTES);
        
    }
    
    @Override
    public void add(ActiveConnection activeConnection) throws GuacamoleException {
        LOGGER.debug(">>>SYNC<<< Adding active connection {}", activeConnection.getIdentifier());
        super.add(activeConnection);
        client.putActiveConnection(new SynchronizedActiveConnection(activeConnection));
    }
    
    @Override
    public ActiveConnection get(String identifier) {
        LOGGER.debug(">>>SYNC<<< Retrieving active connection {}", identifier);
        return client.getActiveConnection(identifier);
    }
    
    @Override
    public void update(ActiveConnection activeConnection) throws GuacamoleException {
        LOGGER.debug(">>>SYNC<<< Updating active connection {}", activeConnection.getIdentifier());
        super.update(activeConnection);
        client.updateActiveConnection(new SynchronizedActiveConnection(activeConnection));
    }
    
    @Override
    public Collection<ActiveConnection> getAll(Collection<String> identifiers) {
        LOGGER.debug(">>>SYNC<<< Retrieving {} active connections.", Integer.toString(identifiers.size()));
        Collection<SynchronizedActiveConnection> syncConns = client.getActiveConnections(identifiers);
        Collection<ActiveConnection> connections = new ArrayList<>();
        for (SynchronizedActiveConnection conn : syncConns) {
            connections.add(conn.getUndecorated());
        }
        return connections;
    }
    
    @Override
    public Set<String> getIdentifiers() {
        LOGGER.debug(">>>SYNC<<< Getting all identifiers.");
        return client.getAllKeys();
    }

    @Override
    protected ActiveConnection decorate(ActiveConnection object) throws GuacamoleException {
        if (object instanceof SynchronizedActiveConnection)
            return object;
        return new SynchronizedActiveConnection(object);
    }

    @Override
    protected ActiveConnection undecorate(ActiveConnection object) throws GuacamoleException {
        if (object instanceof SynchronizedActiveConnection)
            return ((SynchronizedActiveConnection)object).getUndecorated();
        return object;
    }
    
    private class ActiveConnectionSyncTask implements Runnable {
        
        @Override
        public void run() {
            try {
                LOGGER.debug(">>>SYNC<<< Synchronizing active connection directories.");
                Set<String> srcIds = directory.getIdentifiers();
                Set<String> dstIds = getIdentifiers();
                LOGGER.debug(">>>SYNC<<< Source connections: {}", Integer.toString(srcIds.size()));
                LOGGER.debug(">>>SYNC<<< Destination connections: {}", Integer.toString(dstIds.size()));
                for (String id : srcIds) {
                    if (!dstIds.contains(id))
                        client.putActiveConnection(new SynchronizedActiveConnection(directory.get(id)));
                }
                dstIds.stream().filter(id -> (!srcIds.contains(id))).forEachOrdered(id -> {
                    client.removeActiveConnection(id);
                });
            }
            catch (GuacamoleException e) {
                throw new RuntimeException(e);
            }
            
        }
        
    }

    public void shutdown() {
        executor.shutdownNow();
    }
    
}
