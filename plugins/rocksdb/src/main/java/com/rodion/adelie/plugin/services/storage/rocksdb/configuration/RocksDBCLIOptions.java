package com.rodion.adelie.plugin.services.storage.rocksdb.configuration;

import com.google.common.base.MoreObjects;
import picocli.CommandLine;

/** The RocksDb cli options. */
public class RocksDBCLIOptions {

  /** The constant DEFAULT_MAX_OPEN_FILES. */
  public static final int DEFAULT_MAX_OPEN_FILES = 1024;

  /** The constant DEFAULT_CACHE_CAPACITY. */
  public static final long DEFAULT_CACHE_CAPACITY = 134217728;

  /** The constant DEFAULT_BACKGROUND_THREAD_COUNT. */
  public static final int DEFAULT_BACKGROUND_THREAD_COUNT = 4;

  /** The constant DEFAULT_IS_HIGH_SPEC. */
  public static final boolean DEFAULT_IS_HIGH_SPEC = false;

  /** The constant MAX_OPEN_FILES_FLAG. */
  public static final String MAX_OPEN_FILES_FLAG = "--Xplugin-rocksdb-max-open-files";

  /** The constant CACHE_CAPACITY_FLAG. */
  public static final String CACHE_CAPACITY_FLAG = "--Xplugin-rocksdb-cache-capacity";

  /** The constant BACKGROUND_THREAD_COUNT_FLAG. */
  public static final String BACKGROUND_THREAD_COUNT_FLAG =
      "--Xplugin-rocksdb-background-thread-count";

  /** The constant IS_HIGH_SPEC. */
  public static final String IS_HIGH_SPEC = "--Xplugin-rocksdb-high-spec-enabled";

  @CommandLine.Option(
      names = {MAX_OPEN_FILES_FLAG},
      hidden = true,
      defaultValue = "1024",
      paramLabel = "<INTEGER>",
      description = "Max number of files RocksDB will open (default: ${DEFAULT-VALUE})")
  int maxOpenFiles;

  /** The Cache capacity. */
  @CommandLine.Option(
      names = {CACHE_CAPACITY_FLAG},
      hidden = true,
      defaultValue = "134217728",
      paramLabel = "<LONG>",
      description = "Cache capacity of RocksDB (default: ${DEFAULT-VALUE})")
  long cacheCapacity;

  /** The Background thread count. */
  @CommandLine.Option(
      names = {BACKGROUND_THREAD_COUNT_FLAG},
      hidden = true,
      defaultValue = "4",
      paramLabel = "<INTEGER>",
      description = "Number of RocksDB background threads (default: ${DEFAULT-VALUE})")
  int backgroundThreadCount;

  /** The Is high spec. */
  @CommandLine.Option(
      names = {IS_HIGH_SPEC},
      hidden = true,
      paramLabel = "<BOOLEAN>",
      description =
          "Use this flag to boost Besu performance if you have a 16 GiB RAM hardware or more (default: ${DEFAULT-VALUE})")
  boolean isHighSpec;

  private RocksDBCLIOptions() {}

  /**
   * Create RocksDb cli options.
   *
   * @return the RocksDb cli options
   */
  public static RocksDBCLIOptions create() {
    return new RocksDBCLIOptions();
  }

  /**
   * To domain object rocks db factory configuration.
   *
   * @return the rocks db factory configuration
   */
  public RocksDBFactoryConfiguration toDomainObject() {
    return new RocksDBFactoryConfiguration(
        maxOpenFiles, backgroundThreadCount, cacheCapacity, isHighSpec);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("maxOpenFiles", maxOpenFiles)
        .add("cacheCapacity", cacheCapacity)
        .add("backgroundThreadCount", backgroundThreadCount)
        .add("isHighSpec", isHighSpec)
        .toString();
  }
}
