package com.rodion.adelie.plugin.services.storage.rocksdb.configuration;

import java.nio.file.Path;

/** The RocksDB configuration builder. */
public class RocksDBConfigurationBuilder {

  private Path databaseDir;
  private String label = "blockchain";
  private int maxOpenFiles = RocksDBCLIOptions.DEFAULT_MAX_OPEN_FILES;
  private long cacheCapacity = RocksDBCLIOptions.DEFAULT_CACHE_CAPACITY;
  private int backgroundThreadCount = RocksDBCLIOptions.DEFAULT_BACKGROUND_THREAD_COUNT;
  private boolean isHighSpec = RocksDBCLIOptions.DEFAULT_IS_HIGH_SPEC;

  /**
   * Database dir.
   *
   * @param databaseDir the database dir
   * @return the rocks db configuration builder
   */
  public RocksDBConfigurationBuilder databaseDir(final Path databaseDir) {
    this.databaseDir = databaseDir;
    return this;
  }

  /**
   * Max open files.
   *
   * @param maxOpenFiles the max open files
   * @return the rocks db configuration builder
   */
  public RocksDBConfigurationBuilder maxOpenFiles(final int maxOpenFiles) {
    this.maxOpenFiles = maxOpenFiles;
    return this;
  }

  /**
   * Label.
   *
   * @param label the label
   * @return the rocks db configuration builder
   */
  public RocksDBConfigurationBuilder label(final String label) {
    this.label = label;
    return this;
  }

  /**
   * Cache capacity.
   *
   * @param cacheCapacity the cache capacity
   * @return the rocks db configuration builder
   */
  public RocksDBConfigurationBuilder cacheCapacity(final long cacheCapacity) {
    this.cacheCapacity = cacheCapacity;
    return this;
  }

  /**
   * Background thread count.
   *
   * @param backgroundThreadCount the background thread count
   * @return the rocks db configuration builder
   */
  public RocksDBConfigurationBuilder backgroundThreadCount(final int backgroundThreadCount) {
    this.backgroundThreadCount = backgroundThreadCount;
    return this;
  }

  /**
   * Is high spec.
   *
   * @param isHighSpec the is high spec
   * @return the rocks db configuration builder
   */
  public RocksDBConfigurationBuilder isHighSpec(final boolean isHighSpec) {
    this.isHighSpec = isHighSpec;
    return this;
  }

  /**
   * From.
   *
   * @param configuration the configuration
   * @return the rocks db configuration builder
   */
  public static RocksDBConfigurationBuilder from(final RocksDBFactoryConfiguration configuration) {
    return new RocksDBConfigurationBuilder()
        .backgroundThreadCount(configuration.getBackgroundThreadCount())
        .cacheCapacity(configuration.getCacheCapacity())
        .maxOpenFiles(configuration.getMaxOpenFiles())
        .isHighSpec(configuration.isHighSpec());
  }

  /**
   * Build rocks db configuration.
   *
   * @return the rocks db configuration
   */
  public RocksDBConfiguration build() {
    return new RocksDBConfiguration(
        databaseDir, maxOpenFiles, backgroundThreadCount, cacheCapacity, label, isHighSpec);
  }
}
