package org.rodion.pfm.plugin.services.storage.rocksdb.configuration;

/** The RocksDb factory configuration. */
public class RocksDBFactoryConfiguration {

  private final int maxOpenFiles;
  private final int backgroundThreadCount;
  private final long cacheCapacity;
  private final boolean isHighSpec;

  /**
   * Instantiates a new RocksDb factory configuration.
   *
   * @param maxOpenFiles the max open files
   * @param backgroundThreadCount the background thread count
   * @param cacheCapacity the cache capacity
   * @param isHighSpec the is high spec
   */
  public RocksDBFactoryConfiguration(
      final int maxOpenFiles,
      final int backgroundThreadCount,
      final long cacheCapacity,
      final boolean isHighSpec) {
    this.backgroundThreadCount = backgroundThreadCount;
    this.maxOpenFiles = maxOpenFiles;
    this.cacheCapacity = cacheCapacity;
    this.isHighSpec = isHighSpec;
  }

  /**
   * Gets max open files.
   *
   * @return the max open files
   */
  public int getMaxOpenFiles() {
    return maxOpenFiles;
  }

  /**
   * Gets background thread count.
   *
   * @return the background thread count
   */
  public int getBackgroundThreadCount() {
    return backgroundThreadCount;
  }

  /**
   * Gets cache capacity.
   *
   * @return the cache capacity
   */
  public long getCacheCapacity() {
    return cacheCapacity;
  }

  /**
   * Is high spec.
   *
   * @return the boolean
   */
  public boolean isHighSpec() {
    return isHighSpec;
  }
}
