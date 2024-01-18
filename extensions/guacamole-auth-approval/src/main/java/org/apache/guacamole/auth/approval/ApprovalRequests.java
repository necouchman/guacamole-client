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
package org.apache.guacamole.auth.approval;

import com.google.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for tracking all current approval requests.
 */
@Singleton
public class ApprovalRequests {
    
    /**
     * The map that stores the actual approval requests.
     */
    private final Map<UUID, ApprovalRequest> requests = new ConcurrentHashMap<>();
    
    /**
     * Add the given approval request to the map.
     * 
     * @param request
     *     The request to add to the map.
     * 
     * @return 
     *     The UUID of the request that was added to the map.
     */
    public UUID add(ApprovalRequest request) {
        requests.put(request.getUUID(), request);
        return request.getUUID();
    }
    
    /**
     * Get the request having the given UUID.
     * 
     * @param uuid
     *     The UUID of the request to retrieve.
     * 
     * @return 
     *     The request with the given UUID.
     */
    public ApprovalRequest get(UUID uuid) {
        return requests.get(uuid);
    }
    
    /**
     * Retrieve all of the requests in the map.
     * 
     * @return 
     *     An unmodifiable collection of requests in the map.
     */
    public Map<UUID, ApprovalRequest> getAll() {
        return Collections.unmodifiableMap(requests);
    }
    
    /**
     * Return the collection of all items in this map.
     * 
     * @return 
     *     An unmodifiable collection of all requests.
     */
    public Collection<ApprovalRequest> getCollection() {
        return Collections.unmodifiableCollection(requests.values());
    }
    
    /**
     * Delete the request having the given UUID, returning the UUID upon success
     * or null if the request is not found.
     * 
     * @param uuid
     *     The UUID of the request to delete.
     * 
     * @return 
     *     The UUID of the deleted request.
     */
    public UUID delete(UUID uuid) {
        ApprovalRequest request = requests.remove(uuid);
        if (request != null)
            return request.getUUID();
        return null;
    }
    
}
