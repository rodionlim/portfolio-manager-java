package com.rodion.adelie.services.kvstore;

import com.rodion.adelie.plugin.services.exception.StorageException;
import com.rodion.adelie.plugin.services.storage.*;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class will adapt a SegmentedKeyValueStorage to a KeyValueStorage instance. */
public class SegmentedKeyValueStorageAdapter implements KeyValueStorage {

  private static final Logger logger =
      LoggerFactory.getLogger(SegmentedKeyValueStorageAdapter.class);
  private final SegmentIdentifier segmentIdentifier;

  /** The storage to wrap. */
  protected final SegmentedKeyValueStorage storage;

  /**
   * Instantiates a new Segmented key value storage adapter for a single segment.
   *
   * @param segmentIdentifier the segmentIdentifier to wrap as a KeyValueStorage
   * @param storage the storage
   */
  public SegmentedKeyValueStorageAdapter(
      final SegmentIdentifier segmentIdentifier, final SegmentedKeyValueStorage storage) {
    this.segmentIdentifier = segmentIdentifier;
    this.storage = storage;
  }

  SegmentedKeyValueStorage getSegmentedStore() {
    return this.storage;
  }

  @Override
  public void clear() {
    throwIfClosed();
    storage.clear(segmentIdentifier);
  }

  @Override
  public boolean containsKey(final byte[] key) throws StorageException {
    throwIfClosed();
    return storage.containsKey(segmentIdentifier, key);
  }

  @Override
  public Optional<byte[]> get(final byte[] key) throws StorageException {
    throwIfClosed();
    return storage.get(segmentIdentifier, key);
  }

  @Override
  public Set<byte[]> getAllKeysThat(final Predicate<byte[]> returnCondition) {
    throwIfClosed();
    return storage.getAllKeysThat(segmentIdentifier, returnCondition);
  }

  @Override
  public Set<byte[]> getAllValuesFromKeysThat(final Predicate<byte[]> returnCondition) {
    throwIfClosed();
    return storage.getAllValuesFromKeysThat(segmentIdentifier, returnCondition);
  }

  @Override
  public Stream<Pair<byte[], byte[]>> stream() {
    throwIfClosed();
    return storage.stream(segmentIdentifier);
  }

  @Override
  public Stream<Pair<byte[], byte[]>> streamFromKey(final byte[] startKeyHash)
      throws StorageException {
    return storage.streamFromKey(segmentIdentifier, startKeyHash);
  }

  @Override
  public Stream<Pair<byte[], byte[]>> streamFromKey(final byte[] startKey, final byte[] endKey) {
    return storage.streamFromKey(segmentIdentifier, startKey, endKey);
  }

  @Override
  public Stream<byte[]> streamKeys() {
    throwIfClosed();
    return storage.streamKeys(segmentIdentifier);
  }

  @Override
  public boolean tryDelete(final byte[] key) {
    throwIfClosed();
    return storage.tryDelete(segmentIdentifier, key);
  }

  @Override
  public void close() throws IOException {
    storage.close();
  }

  @Override
  public KeyValueStorageTransaction startTransaction() throws StorageException {
    return new KeyValueStorageTransactionAdapter(segmentIdentifier, storage);
  }

  @Override
  public boolean isClosed() {
    return storage.isClosed();
  }

  private void throwIfClosed() {
    if (storage.isClosed()) {
      logger.error("Attempting to use a closed Storage instance.");
      throw new StorageException("Storage has been closed");
    }
  }

  /** This class will adapt a SegmentedKeyValueStorageTransaction to a KeyValueStorageTransaction */
  public static class KeyValueStorageTransactionAdapter implements KeyValueStorageTransaction {
    private final SegmentedKeyValueStorageTransaction segmentedTransaction;
    private final SegmentIdentifier segmentIdentifier;

    /**
     * Instantiates a new Key value storage transaction adapter.
     *
     * @param segmentIdentifier the segmentIdentifier to use for the wrapped transaction
     * @param storage the storage
     */
    public KeyValueStorageTransactionAdapter(
        final SegmentIdentifier segmentIdentifier, final SegmentedKeyValueStorage storage) {
      this.segmentedTransaction = storage.startTransaction();
      this.segmentIdentifier = segmentIdentifier;
    }

    @Override
    public void put(final byte[] key, final byte[] value) {
      segmentedTransaction.put(segmentIdentifier, key, value);
    }

    @Override
    public void remove(final byte[] key) {
      segmentedTransaction.remove(segmentIdentifier, key);
    }

    @Override
    public void commit() throws StorageException {
      segmentedTransaction.commit();
    }

    @Override
    public void rollback() {
      segmentedTransaction.rollback();
    }
  }
}
