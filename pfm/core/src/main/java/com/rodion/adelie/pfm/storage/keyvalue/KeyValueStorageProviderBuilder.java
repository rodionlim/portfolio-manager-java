package com.rodion.adelie.pfm.storage.keyvalue;

import static com.google.common.base.Preconditions.checkNotNull;

import com.rodion.adelie.plugin.services.AdelieConfiguration;
import com.rodion.adelie.plugin.services.storage.KeyValueStorageFactory;

public class KeyValueStorageProviderBuilder {

  private KeyValueStorageFactory storageFactory;
  private AdelieConfiguration commonConfiguration;

  public KeyValueStorageProviderBuilder withStorageFactory(
      final KeyValueStorageFactory storageFactory) {
    this.storageFactory = storageFactory;
    return this;
  }

  public KeyValueStorageProviderBuilder withCommonConfiguration(
      final AdelieConfiguration commonConfiguration) {
    this.commonConfiguration = commonConfiguration;
    return this;
  }

  public KeyValueStorageProvider build() {
    checkNotNull(storageFactory, "Cannot build a storage provider without a storage factory.");
    checkNotNull(
        commonConfiguration,
        "Cannot build a storage provider without the plugin common configuration.");

    return new KeyValueStorageProvider(
        segments -> storageFactory.create(segments, commonConfiguration));
  }
}
