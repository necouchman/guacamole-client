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

package org.apache.guacamole.vault.hashi;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.vault.VaultAuthenticationProviderModule;

/**
 * Guice module which configures injections specific to HashiCorp Vault
 * support.
 */
public class HashiCorpVaultAuthenticationProviderModule
        extends VaultAuthenticationProviderModule {

    /**
     * Creates a new HashiCorpVaultAuthenticationProviderModule which
     * configures dependency injection for the HashiCorp Vault authentication
     * provider and related services.
     *
     * @throws GuacamoleException
     *     If configuration details in guacamole.properties cannot be parsed.
     */
    public HashiCorpVaultAuthenticationProviderModule() throws GuacamoleException {}

    @Override
    protected void configureVault() {

        // Bind services specific to Hashi Corp Vault
        bind(HashiCorpVaultRecordService.class);
        bind(HashiCorpVaultAttributeService.class);
        bind(VaultAttributeService.class).to(HashiCorpVaultAttributeService.class);
        bind(VaultConfigurationService.class).to(HashiCorpVaultConfigurationService.class);
        bind(VaultSecretService.class).to(HashiCorpVaultSecretService.class);
        bind(VaultDirectoryService.class).to(HashiCorpVaultDirectoryService.class);

        // Bind factory for creating Hashi Corp Vault Clients
        install(new FactoryModuleBuilder()
                .implement(HashiCorpVaultClient.class, HashiCorpVaultClient.class)
                .build(HashiCorpVaultClientFactory.class));

        // Bind factory for creating Hashi Corp Vault Users
        install(new FactoryModuleBuilder()
                .implement(HashiCorpVaultUser.class, HashiCorpVaultUser.class)
                .build(HashiCorpVaultUserFactory.class));
    }

}
