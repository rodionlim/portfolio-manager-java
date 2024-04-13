package com.rodion.adelie.plugin.services.storage.rocksdb.segmented;

import com.google.common.base.Supplier;
import com.rodion.adelie.plugin.services.AdelieConfiguration;
import com.rodion.adelie.plugin.services.exception.StorageException;
import com.rodion.adelie.plugin.services.storage.KeyValueStorage;
import com.rodion.adelie.plugin.services.storage.KeyValueStorageFactory;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import com.rodion.adelie.plugin.services.storage.SegmentedKeyValueStorage;
import com.rodion.adelie.plugin.services.storage.rocksdb.configuration.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Rocks db key value storage factory creates segmented storage and uses a adapter to support
 * unsegmented key value storage.
 */
public class RocksDBKeyValueStorageFactory implements KeyValueStorageFactory {

  private static final Logger logger = LoggerFactory.getLogger(RocksDBKeyValueStorageFactory.class);

  private static final EnumSet<BaseVersionedStorageFormat> SUPPORTED_VERSIONED_FORMATS =
      EnumSet.of(
          BaseVersionedStorageFormat.FOREST_WITH_VARIABLES,
          BaseVersionedStorageFormat.BONSAI_WITH_VARIABLES);

  private static final String NAME = "rocksdb";

  private DatabaseMetadata databaseMetadata;

  private RocksDBColumnarKeyValueStorage segmentedStorage;

  private RocksDBConfiguration rocksDBConfiguration;

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
  public KeyValueStorage create(SegmentIdentifier segment, AdelieConfiguration configuration)
      throws StorageException {
    return null;
  }

  @Override
  public SegmentedKeyValueStorage create(
      List<SegmentIdentifier> segments, AdelieConfiguration commonConfiguration)
      throws StorageException {
    if (requiresInit()) {
      init(commonConfiguration);
    }

    // safety check to see that segments all exist within configured segments
    if (!new HashSet<>(configuredSegments).containsAll(segments)) {
      throw new StorageException(
          "Attempted to create storage for segments that are not configured: "
              + segments.stream()
                  .filter(segment -> !configuredSegments.contains(segment))
                  .map(SegmentIdentifier::toString)
                  .collect(Collectors.joining(", ")));
    }

    if (segmentedStorage == null) {
      final List<SegmentIdentifier> segmentsForFormat =
          configuredSegments.stream()
              .filter(
                  segmentId ->
                      segmentId.includeInDatabaseFormat(
                          databaseMetadata.getVersionedStorageFormat().getFormat()))
              .toList();

      // It's probably a good idea for the creation logic to be entirely dependent on the database
      // version. Introducing intermediate booleans that represent database properties and
      // dispatching creation logic based on them is error-prone.
      switch (databaseMetadata.getVersionedStorageFormat().getFormat()) {
        case FOREST -> {
          logger.debug("FOREST mode detected, using TransactionDB.");
          segmentedStorage =
              new TransactionDBRocksDBColumnarKeyValueStorage(
                  rocksDBConfiguration, segmentsForFormat, ignorableSegments);
        }
        case BONSAI -> {
          logger.debug("BONSAI mode detected, Using OptimisticTransactionDB.");
          segmentedStorage =
              new OptimisticRocksDBColumnarKeyValueStorage(
                  rocksDBConfiguration, segmentsForFormat, ignorableSegments);
        }
      }
    }
    return segmentedStorage;
  }

  @Override
  public boolean isSegmentIsolationSupported() {
    return true;
  }

  @Override
  public void close() throws IOException {}

  private void init(final AdelieConfiguration commonConfiguration) {
    try {
      databaseMetadata = readDatabaseMetadata(commonConfiguration);
    } catch (final IOException e) {
      final String message =
          "Failed to retrieve the RocksDB database meta version: "
              + e.getMessage()
              + " could not be found. You may not have the appropriate permission to access the item.";
      throw new StorageException(message, e);
    }
    rocksDBConfiguration =
        RocksDBConfigurationBuilder.from(configuration.get())
            .databaseDir(storagePath(commonConfiguration))
            .build();
  }

  private boolean requiresInit() {
    return segmentedStorage == null;
  }

  private DatabaseMetadata readDatabaseMetadata(final AdelieConfiguration commonConfiguration)
      throws IOException {
    final Path dataDir = commonConfiguration.getDataPath();
    final boolean dataDirExists = dataDir.toFile().exists();
    final boolean databaseExists = commonConfiguration.getStoragePath().toFile().exists();
    final boolean metadataExists = DatabaseMetadata.isPresent(dataDir);
    DatabaseMetadata metadata;
    if (databaseExists && !metadataExists) {
      throw new StorageException(
          "Database exists but metadata file not found, without it there is no safe way to open the database");
    }
    if (metadataExists) {
      metadata = DatabaseMetadata.lookUpFrom(dataDir);

      if (!metadata
          .getVersionedStorageFormat()
          .getFormat()
          .equals(commonConfiguration.getDataStorageConfiguration().getDatabaseFormat())) {
        handleFormatMismatch(commonConfiguration, dataDir, metadata);
      }

      final var runtimeVersion =
          BaseVersionedStorageFormat.defaultForNewDB(
              commonConfiguration.getDataStorageConfiguration());

      if (metadata.getVersionedStorageFormat().getVersion() > runtimeVersion.getVersion()) {
        final var maybeDowngradedMetadata =
            handleVersionDowngrade(dataDir, metadata, runtimeVersion);
        if (maybeDowngradedMetadata.isPresent()) {
          metadata = maybeDowngradedMetadata.get();
          metadata.writeToDirectory(dataDir);
        }
      }

      logger.info("Existing database at {}. Metadata {}. Processing WAL...", dataDir, metadata);
    } else {

      metadata = DatabaseMetadata.defaultForNewDb(commonConfiguration);
      logger.info(
          "No existing database at {}. Using default metadata for new db {}", dataDir, metadata);
      if (!dataDirExists) {
        Files.createDirectories(dataDir);
      }
      metadata.writeToDirectory(dataDir);
    }

    if (!isSupportedVersionedFormat(metadata.getVersionedStorageFormat())) {
      final String message = "Unsupported RocksDB metadata: " + metadata;
      logger.error(message);
      throw new StorageException(message);
    }

    return metadata;
  }

  /**
   * Storage path.
   *
   * @param commonConfiguration the common configuration
   * @return the path
   */
  protected Path storagePath(final AdelieConfiguration commonConfiguration) {
    return commonConfiguration.getStoragePath();
  }

  private static void handleFormatMismatch(
      final AdelieConfiguration commonConfiguration,
      final Path dataDir,
      final DatabaseMetadata existingMetadata) {
    String error =
        String.format(
            "Database format mismatch: DB at %s is %s but config expects %s. "
                + "Please check your config.",
            dataDir,
            existingMetadata.getVersionedStorageFormat().getFormat().name(),
            commonConfiguration.getDataStorageConfiguration().getDatabaseFormat());

    throw new StorageException(error);
  }

  private Optional<DatabaseMetadata> handleVersionDowngrade(
      final Path dataDir,
      final DatabaseMetadata existingMetadata,
      final BaseVersionedStorageFormat runtimeVersion) {
    // here we put the code, or the messages, to perform an automated, or manual, downgrade of the
    // database, if supported, otherwise we just prevent Portfolio Manager from starting since it
    // will not
    // recognize the newer version.
    // In case we do an automated downgrade, then we also need to update the metadata on disk to
    // reflect the change to the runtime version, and return it.

    // Adelie supports both formats of receipts so no downgrade is needed
    if (runtimeVersion == BaseVersionedStorageFormat.BONSAI_WITH_VARIABLES
        || runtimeVersion == BaseVersionedStorageFormat.FOREST_WITH_VARIABLES) {
      logger.warn(
          "Database contains compacted receipts but receipt compaction is not enabled, new receipts  will "
              + "be not stored in the compacted format. If you want to remove compacted receipts from the "
              + "database it is necessary to resync Adelie. Adelie can support both compacted and non-compacted receipts.");
      return Optional.empty();
    }

    // for the moment there are supported automated downgrades, so we just fail.
    String error =
        String.format(
            "Database unsafe downgrade detect: DB at %s is %s with version %s but version %s is expected. "
                + "Please check your config and review release notes for supported downgrade procedures.",
            dataDir,
            existingMetadata.getVersionedStorageFormat().getFormat().name(),
            existingMetadata.getVersionedStorageFormat().getVersion(),
            runtimeVersion.getVersion());

    throw new StorageException(error);
  }

  private boolean isSupportedVersionedFormat(final VersionedStorageFormat versionedStorageFormat) {
    return SUPPORTED_VERSIONED_FORMATS.stream()
        .anyMatch(
            vsf ->
                vsf.getFormat().equals(versionedStorageFormat.getFormat())
                    && vsf.getVersion() == versionedStorageFormat.getVersion());
  }
}
