package org.rodion.pfm.plugin.services;

import java.nio.file.Path;
import org.rodion.pfm.plugin.Unstable;
import org.rodion.pfm.plugin.services.storage.DataStorageConfiguration;
import org.rodion.pfm.plugin.services.storage.DataStorageFormat;

/** Generally useful configuration provided by Pfm. */
public interface PfmConfiguration extends PfmService {

  /**
   * Location of the working directory of the storage in the file system running the client.
   *
   * @return location of the storage in the file system of the client.
   */
  Path getStoragePath();

  /**
   * Location of the data directory in the file system running the client.
   *
   * @return location of the data directory in the file system of the client.
   */
  Path getDataPath();

  /**
   * Database format. This sets the list of segmentIdentifiers that should be initialized.
   *
   * @return Database format.
   */
  @Unstable
  @Deprecated
  DataStorageFormat getDatabaseFormat();

  /**
   * Database storage configuration.
   *
   * @return Database storage configuration.
   */
  @Unstable
  DataStorageConfiguration getDataStorageConfiguration();
}
