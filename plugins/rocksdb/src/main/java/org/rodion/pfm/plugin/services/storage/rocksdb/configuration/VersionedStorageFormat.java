package org.rodion.pfm.plugin.services.storage.rocksdb.configuration;

import org.rodion.pfm.plugin.services.storage.DataStorageFormat;

/** Represent a specific version of a data storage format */
public interface VersionedStorageFormat {
  /**
   * Get the data storage format
   *
   * @return the data storage format
   */
  DataStorageFormat getFormat();

  /**
   * Get the version of the data storage format
   *
   * @return the version of the data storage format
   */
  int getVersion();
}
