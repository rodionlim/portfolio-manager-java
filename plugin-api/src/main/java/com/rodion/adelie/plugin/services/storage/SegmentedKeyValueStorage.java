package com.rodion.adelie.plugin.services.storage;

import com.rodion.adelie.plugin.services.exception.StorageException;
import java.io.Closeable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tuweni.bytes.Bytes;

/** Service provided by Adelie to facilitate persistent data storage. */
public interface SegmentedKeyValueStorage extends Closeable {

  /**
   * Get the value from the associated segment and key.
   *
   * @param segment the segment
   * @param key Index into persistent data repository.
   * @return The value persisted at the key index.
   * @throws StorageException the storage exception
   */
  Optional<byte[]> get(SegmentIdentifier segment, byte[] key) throws StorageException;

  /**
   * Find the key and corresponding value "nearest to" the specified key. Nearest is defined as
   * either matching the supplied key or the key lexicographically prior to it.
   *
   * @param segmentIdentifier segment to scan
   * @param key key for which we are searching for the nearest match.
   * @return Optional of NearestKeyValue-wrapped matched key and corresponding value.
   * @throws StorageException the storage exception
   */
  Optional<NearestKeyValue> getNearestTo(final SegmentIdentifier segmentIdentifier, Bytes key)
      throws StorageException;

  /**
   * Contains key.
   *
   * @param segment the segment
   * @param key the key
   * @return the boolean
   * @throws StorageException the storage exception
   */
  default boolean containsKey(final SegmentIdentifier segment, final byte[] key)
      throws StorageException {
    return get(segment, key).isPresent();
  }

  /**
   * Begins a transaction. Returns a transaction object that can be updated and committed.
   *
   * @return An object representing the transaction.
   * @throws StorageException the storage exception
   */
  SegmentedKeyValueStorageTransaction startTransaction() throws StorageException;

  /**
   * Returns a stream of all keys for the segment.
   *
   * @param segmentIdentifier The segment identifier whose keys we want to stream.
   * @return A stream of all keys in the specified segment.
   */
  Stream<Pair<byte[], byte[]>> stream(final SegmentIdentifier segmentIdentifier);

  /**
   * Returns a stream of key-value pairs starting from the specified key. This method is used to
   * retrieve a stream of data from the storage, starting from the given key. If no data is
   * available from the specified key onwards, an empty stream is returned.
   *
   * @param segmentIdentifier The segment identifier whose keys we want to stream.
   * @param startKey The key from which the stream should start.
   * @return A stream of key-value pairs starting from the specified key.
   */
  Stream<Pair<byte[], byte[]>> streamFromKey(
      final SegmentIdentifier segmentIdentifier, final byte[] startKey);

  /**
   * Returns a stream of key-value pairs starting from the specified key, ending at the specified
   * key. This method is used to retrieve a stream of data from the storage, starting from the given
   * key. If no data is available from the specified key onwards, an empty stream is returned.
   *
   * @param segmentIdentifier The segment identifier whose keys we want to stream.
   * @param startKey The key from which the stream should start.
   * @param endKey The key at which the stream should stop.
   * @return A stream of key-value pairs starting from the specified key.
   */
  Stream<Pair<byte[], byte[]>> streamFromKey(
      final SegmentIdentifier segmentIdentifier, final byte[] startKey, final byte[] endKey);

  /**
   * Stream keys.
   *
   * @param segmentIdentifier the segment identifier
   * @return the stream
   */
  Stream<byte[]> streamKeys(final SegmentIdentifier segmentIdentifier);

  /**
   * Delete the value corresponding to the given key in the given segment if a write lock can be
   * instantly acquired on the underlying storage. Do nothing otherwise.
   *
   * @param segmentIdentifier The segment identifier whose keys we want to stream.
   * @param key The key to delete.
   * @return false if the lock on the underlying storage could not be instantly acquired, true
   *     otherwise
   * @throws StorageException any problem encountered during the deletion attempt.
   */
  boolean tryDelete(SegmentIdentifier segmentIdentifier, byte[] key) throws StorageException;

  /**
   * Gets all keys that matches condition.
   *
   * @param segmentIdentifier the segment identifier
   * @param returnCondition the return condition
   * @return set of result
   */
  Set<byte[]> getAllKeysThat(
      SegmentIdentifier segmentIdentifier, Predicate<byte[]> returnCondition);

  /**
   * Gets all values from keys that matches condition.
   *
   * @param segmentIdentifier the segment identifier
   * @param returnCondition the return condition
   * @return the set of result
   */
  Set<byte[]> getAllValuesFromKeysThat(
      final SegmentIdentifier segmentIdentifier, Predicate<byte[]> returnCondition);

  /**
   * Clear.
   *
   * @param segmentIdentifier the segment identifier
   */
  void clear(SegmentIdentifier segmentIdentifier);

  /**
   * Whether the underlying storage is closed.
   *
   * @return boolean indicating whether the underlying storage is closed.
   */
  boolean isClosed();

  /**
   * record type used to wrap responses from getNearestTo, includes the matched key and the value.
   *
   * @param key the matched (nearest) key
   * @param value the corresponding value
   */
  record NearestKeyValue(Bytes key, Optional<byte[]> value) {

    /**
     * Convenience method to map the Optional value to Bytes.
     *
     * @return Optional of Bytes.
     */
    public Optional<Bytes> wrapBytes() {
      return value.map(Bytes::wrap);
    }
  }
}
