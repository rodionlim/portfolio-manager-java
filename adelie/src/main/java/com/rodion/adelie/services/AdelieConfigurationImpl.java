package com.rodion.adelie.services;

import com.rodion.adelie.pfm.blotter.DataStorageConfiguration;
import com.rodion.adelie.plugin.services.AdelieConfiguration;
import com.rodion.adelie.plugin.services.storage.DataStorageFormat;
import java.nio.file.Path;

/** A concrete implementation of AdelieConfiguration which is used in Adelie plugin framework. */
public class AdelieConfigurationImpl implements AdelieConfiguration {
  private Path storagePath;
  private Path dataPath;
  private DataStorageConfiguration dataStorageConfiguration;

  /**
   * Post creation initialization
   *
   * @param dataPath The Path representing data folder
   * @param storagePath The path representing storage folder
   */
  public void init(
      final Path dataPath,
      final Path storagePath,
      final DataStorageConfiguration dataStorageConfiguration) {
    this.dataPath = dataPath;
    this.storagePath = storagePath;
    this.dataStorageConfiguration = dataStorageConfiguration;
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
  public com.rodion.adelie.plugin.services.storage.DataStorageConfiguration
      getDataStorageConfiguration() {
    return new DataStorageConfigurationImpl(dataStorageConfiguration);
  }

  /**
   * A concrete implementation of DataStorageConfiguration which is used in Adelie plugin framework.
   */
  public static class DataStorageConfigurationImpl
      implements com.rodion.adelie.plugin.services.storage.DataStorageConfiguration {

    private final DataStorageConfiguration dataStorageConfiguration;

    public DataStorageConfigurationImpl(final DataStorageConfiguration dataStorageConfiguration) {
      this.dataStorageConfiguration = dataStorageConfiguration;
    }

    @Override
    public DataStorageFormat getDatabaseFormat() {
      return dataStorageConfiguration.getDataStorageFormat();
    }
  }
}
