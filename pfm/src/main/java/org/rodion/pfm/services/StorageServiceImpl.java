package org.rodion.pfm.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.rodion.pfm.plugin.services.StorageService;
import org.rodion.pfm.plugin.services.storage.KeyValueStorageFactory;
import org.rodion.pfm.plugin.services.storage.SegmentIdentifier;
import org.rodion.pfm.storage.KeyValueSegmentIdentifier;

/** The Storage service implementation. */
public class StorageServiceImpl implements StorageService {

  private final List<SegmentIdentifier> segments;
  private final Map<String, KeyValueStorageFactory> factories;

  /** Instantiates a new Storage service. */
  public StorageServiceImpl() {
    this.segments = List.of(KeyValueSegmentIdentifier.values());
    this.factories = new ConcurrentHashMap<>();
  }

  @Override
  public void registerKeyValueStorage(final KeyValueStorageFactory factory) {
    factories.put(factory.getName(), factory);
  }

  @Override
  public List<SegmentIdentifier> getAllSegmentIdentifiers() {
    return segments;
  }

  @Override
  public Optional<KeyValueStorageFactory> getByName(final String name) {
    return Optional.ofNullable(factories.get(name));
  }
}
