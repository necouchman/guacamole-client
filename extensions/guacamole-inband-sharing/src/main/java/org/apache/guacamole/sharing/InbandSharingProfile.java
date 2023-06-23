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

package org.apache.guacamole.sharing;

import java.util.Arrays;
import java.util.List;
import org.apache.guacamole.form.BooleanField;
import org.apache.guacamole.form.Form;
import org.apache.guacamole.net.auth.DelegatingSharingProfile;
import org.apache.guacamole.net.auth.SharingProfile;

/**
 * A SharingProfile that delegates underlying storage to another provider.
 */
public class InbandSharingProfile extends DelegatingSharingProfile {
    
    /**
     * The attribute that stores whether or not a connection should be shared
     * automatically as soon as it is started.
     */
    public static final String AUTOMATIC_SHARING_ATTRIBUTE_NAME = "inband-automatic-sharing";
    
    /**
     * The list of all attributes associated with this sharing profile.
     */
    public static final List<String> INBAND_SHARING_ATTRIBUTES = Arrays.asList(AUTOMATIC_SHARING_ATTRIBUTE_NAME);
    
    /**
     * A form that contains the fields used to configure the inband sharing.
     */
    public static final Form INBAND_SHARING_FORM = new Form("inband-sharing-form",
            Arrays.asList(
                    new BooleanField(AUTOMATIC_SHARING_ATTRIBUTE_NAME, "true")
            )
    );
    
    /**
     * Create a new SharingProfile object that wraps the provided profile.
     * 
     * @param sharingProfile 
     *     The SharingProfile to wrap.
     */
    public InbandSharingProfile(SharingProfile sharingProfile) {
        super(sharingProfile);
    }
    
    /**
     * Return the underlying SharingProfile that this profile is decorating.
     * 
     * @return 
     *     The original SharingProfile that this profile decorates.
     */
    public SharingProfile getUndecorated() {
        return getDelegateSharingProfile();
    }
    
}
