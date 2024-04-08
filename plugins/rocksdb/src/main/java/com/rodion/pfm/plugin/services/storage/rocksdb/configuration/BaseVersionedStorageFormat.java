package com.rodion.pfm.plugin.services.storage.rocksdb.configuration;

import com.rodion.pfm.plugin.services.storage.DataStorageConfiguration;
import com.rodion.pfm.plugin.services.storage.DataStorageFormat;

/** Base versioned data storage format */
public enum BaseVersionedStorageFormat implements VersionedStorageFormat {
  /** Original Forest version, not used since replace by FOREST_WITH_VARIABLES */
  FOREST_ORIGINAL(DataStorageFormat.FOREST, 1),
  /**
   * Current Forest version, with blockchain variables in a dedicated column family, in order to
   * make BlobDB more effective
   */
  FOREST_WITH_VARIABLES(DataStorageFormat.FOREST, 2),

  /**
   * Current Bonsai version, with blockchain variables in a dedicated column family, in order to
   * make BlobDB more effective
   */
  BONSAI_WITH_VARIABLES(DataStorageFormat.BONSAI, 2);

  private final DataStorageFormat format;
  private final int version;

  BaseVersionedStorageFormat(final DataStorageFormat format, final int version) {
    this.format = format;
    this.version = version;
  }

  /**
   * Return the default version for new db for a specific format
   *
   * @param configuration data storage configuration
   * @return the version to use for new db
   */
  public static BaseVersionedStorageFormat defaultForNewDB(
      final DataStorageConfiguration configuration) {
    return switch (configuration.getDatabaseFormat()) {
      case FOREST -> FOREST_WITH_VARIABLES;
      case BONSAI -> BONSAI_WITH_VARIABLES;
    };
  }

  @Override
  public DataStorageFormat getFormat() {
    return format;
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return "BaseVersionedStorageFormat{" + "format=" + format + ", version=" + version + '}';
  }
}
