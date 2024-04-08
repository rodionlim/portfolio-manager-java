package com.rodion.pfm.plugin.services.storage.rocksdb;

import com.rodion.pfm.plugin.services.storage.rocksdb.exception.InvalidConfigurationException;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The RocksDB util. */
public class RocksDBUtil {
  private static final Logger logger = LoggerFactory.getLogger(RocksDBUtil.class);

  private RocksDBUtil() {}

  /** Load native library. */
  public static void loadNativeLibrary() {
    try {
      RocksDB.loadLibrary();
    } catch (final ExceptionInInitializerError e) {
      if (e.getCause() instanceof UnsupportedOperationException) {
        logger.info("Unable to load RocksDB library", e);
        throw new InvalidConfigurationException(
            "Unsupported platform detected. On Windows, ensure you have 64bit Java installed.");
      } else {
        throw e;
      }
    }
  }
}
