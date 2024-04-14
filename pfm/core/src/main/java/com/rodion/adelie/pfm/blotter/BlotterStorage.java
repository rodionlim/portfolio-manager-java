package com.rodion.adelie.pfm.blotter;

import com.rodion.adelie.plugin.services.storage.DataStorageFormat;

public interface BlotterStorage {

  DataStorageFormat getDataStorageFormat();

  Updater updater();

  void clear();

  interface Updater {
    void commit();
  }
}
