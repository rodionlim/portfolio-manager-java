package com.rodion.adelie.pfm.storage.keyvalue;

import com.rodion.adelie.pfm.blotter.BlotterKeyValueStorage;
import com.rodion.adelie.plugin.services.storage.DataStorageConfiguration;
import com.rodion.adelie.plugin.services.storage.KeyValueStorage;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import com.rodion.adelie.plugin.services.storage.SegmentedKeyValueStorage;
import com.rodion.adelie.services.kvstore.SegmentedKeyValueStorageAdapter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyValueStorageProvider implements StorageProvider {
  private static final Logger LOG = LoggerFactory.getLogger(StorageProvider.class);

  protected final Function<List<SegmentIdentifier>, SegmentedKeyValueStorage>
      segmentedStorageCreator;
  protected final Map<List<SegmentIdentifier>, SegmentedKeyValueStorage> storageInstances =
      new HashMap<>();

  public KeyValueStorageProvider(
      final Function<List<SegmentIdentifier>, SegmentedKeyValueStorage> segmentedStorageCreator) {
    this.segmentedStorageCreator = segmentedStorageCreator;
  }

  @Override
  public BlotterKeyValueStorage createBlotterStorage(
      DataStorageConfiguration dataStorageConfiguration) {
    return new ForestBlotterKeyValueStorage(
        getStorageBySegmentIdentifier(KeyValueSegmentIdentifier.BLOTTER));
  }

  @Override
  public KeyValueStorage getStorageBySegmentIdentifier(final SegmentIdentifier segment) {
    return new SegmentedKeyValueStorageAdapter(
        segment, storageInstances.computeIfAbsent(List.of(segment), segmentedStorageCreator));
  }

  @Override
  public SegmentedKeyValueStorage getStorageBySegmentIdentifiers(
      final List<SegmentIdentifier> segments) {
    return segmentedStorageCreator.apply(segments);
  }

  @Override
  public void close() throws IOException {
    storageInstances.entrySet().stream()
        .filter(storage -> storage instanceof AutoCloseable)
        .forEach(
            storage -> {
              try {
                storage.getValue().close();
              } catch (final IOException e) {
                LOG.atWarn()
                    .setMessage("Failed to close storage instance {}")
                    .addArgument(
                        storage.getKey().stream()
                            .map(SegmentIdentifier::getName)
                            .collect(Collectors.joining(",")))
                    .setCause(e)
                    .log();
              }
            });
  }
}
