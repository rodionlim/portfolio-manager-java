package com.rodion.adelie.services;

import com.rodion.adelie.plugin.services.StorageService;
import com.rodion.adelie.plugin.services.storage.KeyValueStorageFactory;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import com.rodion.adelie.storage.KeyValueSegmentIdentifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
