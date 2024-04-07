package org.rodion.pfm.plugin.services.storage.rocksdb.segmented;

import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.List;
import org.rodion.pfm.plugin.services.PfmConfiguration;
import org.rodion.pfm.plugin.services.exception.StorageException;
import org.rodion.pfm.plugin.services.storage.KeyValueStorage;
import org.rodion.pfm.plugin.services.storage.KeyValueStorageFactory;
import org.rodion.pfm.plugin.services.storage.SegmentIdentifier;
import org.rodion.pfm.plugin.services.storage.SegmentedKeyValueStorage;
import org.rodion.pfm.plugin.services.storage.rocksdb.configuration.RocksDBFactoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Rocks db key value storage factory creates segmented storage and uses a adapter to support
 * unsegmented key value storage.
 */
public class RocksDBKeyValueStorageFactory implements KeyValueStorageFactory {

  private static final Logger logger = LoggerFactory.getLogger(RocksDBKeyValueStorageFactory.class);

  private static final String NAME = "rocksdb";

  private final Supplier<RocksDBFactoryConfiguration> configuration;

  private final List<SegmentIdentifier> configuredSegments;

  private final List<SegmentIdentifier> ignorableSegments;

  /**
   * Instantiates a new RocksDb key value storage factory.
   *
   * @param configuration the configuration
   * @param configuredSegments the segments
   * @param ignorableSegments the ignorable segments
   */
  public RocksDBKeyValueStorageFactory(
      final Supplier<RocksDBFactoryConfiguration> configuration,
      final List<SegmentIdentifier> configuredSegments,
      final List<SegmentIdentifier> ignorableSegments) {
    this.configuration = configuration;
    this.configuredSegments = configuredSegments;
    this.ignorableSegments = ignorableSegments;
  }

  /**
   * Instantiates a new RocksDb key value storage factory.
   *
   * @param configuration the configuration
   * @param configuredSegments the segments
   */
  public RocksDBKeyValueStorageFactory(
      final Supplier<RocksDBFactoryConfiguration> configuration,
      final List<SegmentIdentifier> configuredSegments) {
    this(configuration, configuredSegments, List.of());
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public KeyValueStorage create(SegmentIdentifier segment, PfmConfiguration configuration)
      throws StorageException {
    return null;
  }

  @Override
  public SegmentedKeyValueStorage create(
      List<SegmentIdentifier> segments, PfmConfiguration configuration) throws StorageException {
    return null;
  }

  @Override
  public boolean isSegmentIsolationSupported() {
    return true;
  }

  @Override
  public void close() throws IOException {}
}
