package com.rodion.adelie.plugin.services;

import com.rodion.adelie.plugin.Unstable;
import com.rodion.adelie.plugin.services.storage.KeyValueStorageFactory;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import java.util.List;
import java.util.Optional;

/** This service allows plugins to register as an available storage engine. */
@Unstable
public interface StorageService extends AdelieService {

  /**
   * Registers a factory as available for creating key-value storage instances.
   *
   * @param factory creates instances providing key-value storage.
   */
  void registerKeyValueStorage(KeyValueStorageFactory factory);

  /**
   * Retrieves the identifiers for the isolation segments that could be requested during operation.
   *
   * @return full set of possible segments required from the storage service.
   */
  List<SegmentIdentifier> getAllSegmentIdentifiers();

  /**
   * Retrieves a registered factory corresponding to the supplied factory name
   *
   * @param name The name of the factory to retrieve
   * @return an optional containing the instance of the registered factory, or empty if the factory
   *     hasn't been registered.
   */
  Optional<KeyValueStorageFactory> getByName(String name);
}
