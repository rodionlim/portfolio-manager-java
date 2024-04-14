package com.rodion.adelie.pfm.storage.keyvalue;

import com.rodion.adelie.pfm.blotter.BlotterStorage;
import com.rodion.adelie.plugin.services.storage.DataStorageFormat;
import com.rodion.adelie.plugin.services.storage.KeyValueStorage;

public class KeyValueStoragePrefixedKeyBlotterStorage implements BlotterStorage {

  private final KeyValueStorage keyValueStorage;

  public KeyValueStoragePrefixedKeyBlotterStorage(final KeyValueStorage keyValueStorage) {
    this.keyValueStorage = keyValueStorage;
  }

  @Override
  public DataStorageFormat getDataStorageFormat() {
    return DataStorageFormat.FOREST;
  }

  @Override
  public Updater updater() {
    return null;
  }

  @Override
  public void clear() {
    keyValueStorage.clear();
  }
}
