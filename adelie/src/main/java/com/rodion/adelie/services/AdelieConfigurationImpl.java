package com.rodion.adelie.services;

import com.rodion.adelie.plugin.services.AdelieConfiguration;
import com.rodion.adelie.plugin.services.storage.DataStorageConfiguration;
import com.rodion.adelie.plugin.services.storage.DataStorageFormat;
import java.nio.file.Path;

/** A concrete implementation of AdelieConfiguration which is used in Adelie plugin framework. */
public class AdelieConfigurationImpl implements AdelieConfiguration {
  private Path storagePath;
  private Path dataPath;

  /**
   * Post creation initialization
   *
   * @param dataPath The Path representing data folder
   * @param storagePath The path representing storage folder
   */
  public void init(final Path dataPath, final Path storagePath) {
    this.dataPath = dataPath;
    this.storagePath = storagePath;
  }

  @Override
  public Path getStoragePath() {
    return storagePath;
  }

  @Override
  public Path getDataPath() {
    return dataPath;
  }

  @Override
  public DataStorageFormat getDatabaseFormat() {
    return null;
  }

  @Override
  public DataStorageConfiguration getDataStorageConfiguration() {
    return null;
  }
}
