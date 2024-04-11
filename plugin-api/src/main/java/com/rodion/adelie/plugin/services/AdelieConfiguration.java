package com.rodion.adelie.plugin.services;

import com.rodion.adelie.plugin.Unstable;
import com.rodion.adelie.plugin.services.storage.DataStorageConfiguration;
import com.rodion.adelie.plugin.services.storage.DataStorageFormat;
import java.nio.file.Path;

/** Generally useful configuration provided by Adelie. */
public interface AdelieConfiguration extends AdelieService {

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
