package com.rodion.adelie.plugin.services.storage.rocksdb.segmented;

import com.rodion.adelie.plugin.services.exception.StorageException;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import com.rodion.adelie.plugin.services.storage.SegmentedKeyValueStorageTransaction;
import com.rodion.adelie.plugin.services.storage.rocksdb.RocksDBIterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tuweni.bytes.Bytes;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.OptimisticTransactionDB;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.Transaction;
import org.rocksdb.WriteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Rocks db snapshot transaction. */
public class RocksDBSnapshotTransaction
    implements SegmentedKeyValueStorageTransaction, AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(RocksDBSnapshotTransaction.class);
  private static final String NO_SPACE_LEFT_ON_DEVICE = "No space left on device";
  private final OptimisticTransactionDB db;
  private final Function<SegmentIdentifier, ColumnFamilyHandle> columnFamilyMapper;
  private final Transaction snapTx;
  private final RocksDBSnapshot snapshot;
  private final WriteOptions writeOptions;
  private final ReadOptions readOptions;
  private final AtomicBoolean isClosed = new AtomicBoolean(false);

  /**
   * Instantiates a new RocksDb snapshot transaction.
   *
   * @param db the db
   * @param columnFamilyMapper mapper from segment identifier to column family handle
   */
  RocksDBSnapshotTransaction(
      final OptimisticTransactionDB db,
      final Function<SegmentIdentifier, ColumnFamilyHandle> columnFamilyMapper) {
    this.db = db;
    this.columnFamilyMapper = columnFamilyMapper;
    this.snapshot = new RocksDBSnapshot(db);
    this.writeOptions = new WriteOptions();
    this.snapTx = db.beginTransaction(writeOptions);
    this.readOptions =
        new ReadOptions().setVerifyChecksums(false).setSnapshot(snapshot.markAndUseSnapshot());
  }

  private RocksDBSnapshotTransaction(
      final OptimisticTransactionDB db,
      final Function<SegmentIdentifier, ColumnFamilyHandle> columnFamilyMapper,
      final RocksDBSnapshot snapshot,
      final Transaction snapTx,
      final ReadOptions readOptions) {
    this.db = db;
    this.columnFamilyMapper = columnFamilyMapper;
    this.snapshot = snapshot;
    this.writeOptions = new WriteOptions();
    this.readOptions = readOptions;
    this.snapTx = snapTx;
  }

  /**
   * Get data against given key.
   *
   * @param segmentId the segment id
   * @param key the key
   * @return the optional data
   */
  public Optional<byte[]> get(final SegmentIdentifier segmentId, final byte[] key) {
    throwIfClosed();

    try {
      return Optional.ofNullable(snapTx.get(readOptions, columnFamilyMapper.apply(segmentId), key));
    } catch (final RocksDBException e) {
      throw new StorageException(e);
    }
  }

  @Override
  public void put(final SegmentIdentifier segmentId, final byte[] key, final byte[] value) {
    throwIfClosed();

    try {
      snapTx.put(columnFamilyMapper.apply(segmentId), key, value);
    } catch (final RocksDBException e) {
      if (e.getMessage().contains(NO_SPACE_LEFT_ON_DEVICE)) {
        logger.error(e.getMessage());
        System.exit(0);
      }
      throw new StorageException(e);
    }
  }

  @Override
  public void remove(final SegmentIdentifier segmentId, final byte[] key) {
    throwIfClosed();

    try {
      snapTx.delete(columnFamilyMapper.apply(segmentId), key);
    } catch (final RocksDBException e) {
      if (e.getMessage().contains(NO_SPACE_LEFT_ON_DEVICE)) {
        logger.error(e.getMessage());
        System.exit(0);
      }
      throw new StorageException(e);
    }
  }

  /**
   * get a RocksIterator that reads through the transaction to represent the current state.
   *
   * <p>be sure to close this iterator, like in a try-with-resources block, otherwise a native
   * memory leak might occur.
   *
   * @param segmentId id for the segment to iterate over.
   * @return RocksIterator
   */
  public RocksIterator getIterator(final SegmentIdentifier segmentId) {
    return snapTx.getIterator(readOptions, columnFamilyMapper.apply(segmentId));
  }

  /**
   * Stream.
   *
   * @param segmentId the segment id
   * @return the stream
   */
  public Stream<Pair<byte[], byte[]>> stream(final SegmentIdentifier segmentId) {
    throwIfClosed();

    final RocksIterator rocksIterator =
        db.newIterator(columnFamilyMapper.apply(segmentId), readOptions);
    rocksIterator.seekToFirst();
    return RocksDBIterator.create(rocksIterator).toStream();
  }

  /**
   * Stream keys.
   *
   * @param segmentId the segment id
   * @return the stream
   */
  public Stream<byte[]> streamKeys(final SegmentIdentifier segmentId) {
    throwIfClosed();

    final RocksIterator rocksIterator =
        db.newIterator(columnFamilyMapper.apply(segmentId), readOptions);
    rocksIterator.seekToFirst();
    return RocksDBIterator.create(rocksIterator).toStreamKeys();
  }

  /**
   * Returns a stream of key-value pairs starting from the specified key. This method is used to
   * retrieve a stream of data reading through the transaction, starting from the given key. If no
   * data is available from the specified key onwards, an empty stream is returned.
   *
   * @param segment The segment identifier whose keys we want to stream.
   * @param startKey The key from which the stream should start.
   * @return A stream of key-value pairs starting from the specified key.
   */
  public Stream<Pair<byte[], byte[]>> streamFromKey(
      final SegmentIdentifier segment, final byte[] startKey) {
    throwIfClosed();

    final RocksIterator rocksIterator =
        db.newIterator(columnFamilyMapper.apply(segment), readOptions);
    rocksIterator.seek(startKey);
    return RocksDBIterator.create(rocksIterator).toStream();
  }

  /**
   * Returns a stream of key-value pairs starting from the specified key, ending at the specified
   * key. This method is used to retrieve a stream of data reading through the transaction, starting
   * from the given key. If no data is available from the specified key onwards, an empty stream is
   * returned.
   *
   * @param segment The segment identifier whose keys we want to stream.
   * @param startKey The key from which the stream should start.
   * @param endKey The key at which the stream should stop.
   * @return A stream of key-value pairs starting from the specified key.
   */
  public Stream<Pair<byte[], byte[]>> streamFromKey(
      final SegmentIdentifier segment, final byte[] startKey, final byte[] endKey) {
    throwIfClosed();
    final Bytes endKeyBytes = Bytes.wrap(endKey);

    final RocksIterator rocksIterator =
        db.newIterator(columnFamilyMapper.apply(segment), readOptions);
    rocksIterator.seek(startKey);
    return RocksDBIterator.create(rocksIterator)
        .toStream()
        .takeWhile(e -> endKeyBytes.compareTo(Bytes.wrap(e.getKey())) >= 0);
  }

  @Override
  public void commit() throws StorageException {
    // no-op
  }

  @Override
  public void rollback() {
    throwIfClosed();

    try {
      snapTx.rollback();
    } catch (final RocksDBException e) {
      if (e.getMessage().contains(NO_SPACE_LEFT_ON_DEVICE)) {
        logger.error(e.getMessage());
        System.exit(0);
      }
      throw new StorageException(e);
    } finally {
      close();
    }
  }

  /**
   * Copy.
   *
   * @return the rocks db snapshot transaction
   */
  public RocksDBSnapshotTransaction copy() {
    throwIfClosed();
    try {
      var copyReadOptions = new ReadOptions().setSnapshot(snapshot.markAndUseSnapshot());
      var copySnapTx = db.beginTransaction(writeOptions);
      copySnapTx.rebuildFromWriteBatch(snapTx.getWriteBatch().getWriteBatch());
      return new RocksDBSnapshotTransaction(
          db, columnFamilyMapper, snapshot, copySnapTx, copyReadOptions);
    } catch (Exception ex) {
      logger.error("Failed to copy snapshot transaction", ex);
      snapshot.unMarkSnapshot();
      throw new StorageException(ex);
    }
  }

  @Override
  public void close() {
    snapTx.close();
    writeOptions.close();
    readOptions.close();
    snapshot.unMarkSnapshot();
    isClosed.set(true);
  }

  private void throwIfClosed() {
    if (isClosed.get()) {
      logger.error("Attempting to use a closed RocksDBSnapshotTransaction");
      throw new StorageException("Storage has already been closed");
    }
  }
}
