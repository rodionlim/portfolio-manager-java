package com.rodion.pfm.plugin.services.storage;

import com.rodion.pfm.plugin.Unstable;

/** Data storage configuration */
@Unstable
public interface DataStorageConfiguration {

  /**
   * Database format. This sets the list of segmentIdentifiers that should be initialized.
   *
   * @return Database format.
   */
  @Unstable
  DataStorageFormat getDatabaseFormat();
}
