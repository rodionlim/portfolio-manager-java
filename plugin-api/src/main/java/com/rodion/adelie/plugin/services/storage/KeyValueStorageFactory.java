package com.rodion.adelie.plugin.services.storage;

import com.rodion.adelie.plugin.Unstable;
import com.rodion.adelie.plugin.services.AdelieConfiguration;
import com.rodion.adelie.plugin.services.exception.StorageException;
import java.io.Closeable;
import java.util.List;

/** Factory for creating key-value storage instances. */
@Unstable
public interface KeyValueStorageFactory extends Closeable {

  /**
   * Retrieves the identity of the key-value storage factory.
   *
   * @return the storage identifier, used when selecting the appropriate storage service.
   */
  String getName();

  /**
   * Creates a new key-value storage instance, appropriate for the given segment.
   *
   * <p>When segment isolation is not supported, the create will still be called with each of the
   * required segments, where the same storage instance should be returned.
   *
   * <p>New segments may be introduced in future releases and should result in a new empty
   * key-space. Segments created with the identifier of an existing segment should have the same
   * data as that existing segment.
   *
   * @param segment identity of the isolation segment, an identifier for the data set the storage
   *     will contain.
   * @param configuration common configuration available to plugins, in a populated state.
   * @return the storage instance reserved for the given segment.
   * @exception StorageException problem encountered when creating storage for the segment.
   */
  KeyValueStorage create(SegmentIdentifier segment, AdelieConfiguration configuration)
      throws StorageException;

  /**
   * Creates a new segmented key-value storage instance, appropriate for the given segment.
   *
   * <p>New segments may be introduced in future releases and should result in a new empty
   * key-space. Segments created with the identifier of an existing segment should have the same
   * data as that existing segment.
   *
   * @param segments list of segments identifiers that comprise the created segmented storage.
   * @param configuration common configuration available to plugins, in a populated state.
   * @return the storage instance reserved for the given segment.
   * @exception StorageException problem encountered when creating storage for the segment.
   */
  SegmentedKeyValueStorage create(
      List<SegmentIdentifier> segments, AdelieConfiguration configuration) throws StorageException;

  /**
   * Whether storage segment isolation is supported by the factory created instances.
   *
   * <p>As supporting segment isolation is similar to a separating keys into distinct namespaces,
   * where operations only affect within that segment i.e. the same key from two segments point to
   * separate values.
   *
   * @return <code>true</code> when the created storage instances are isolated from each other,
   *     <code>false</code> when keys of different segments can collide with each other.
   */
  boolean isSegmentIsolationSupported();
}
