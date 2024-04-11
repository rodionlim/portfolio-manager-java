package com.rodion.adelie.plugin.services.storage.rocksdb.segmented;

import com.rodion.adelie.plugin.services.exception.StorageException;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import com.rodion.adelie.plugin.services.storage.SegmentedKeyValueStorageTransaction;
import com.rodion.adelie.plugin.services.storage.rocksdb.RocksDBTransaction;
import com.rodion.adelie.plugin.services.storage.rocksdb.configuration.RocksDBConfiguration;
import com.rodion.adelie.services.kvstore.SegmentedKeyValueStorageTransactionValidatorDecorator;
import java.util.List;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.TransactionDB;
import org.rocksdb.WriteOptions;

/** TransactionDB RocksDB Columnar key value storage */
public class TransactionDBRocksDBColumnarKeyValueStorage extends RocksDBColumnarKeyValueStorage {

  private final TransactionDB db;

  /**
   * The constructor of TransactionDBRocksDBColumnarKeyValueStorage
   *
   * @param configuration the RocksDB configuration
   * @param segments the segments
   * @param ignorableSegments the ignorable segments
   * @throws StorageException the storage exception
   */
  public TransactionDBRocksDBColumnarKeyValueStorage(
      final RocksDBConfiguration configuration,
      final List<SegmentIdentifier> segments,
      final List<SegmentIdentifier> ignorableSegments)
      throws StorageException {
    super(configuration, segments, ignorableSegments);
    try {

      db =
          TransactionDB.open(
              options,
              txOptions,
              configuration.getDatabaseDir().toString(),
              columnDescriptors,
              columnHandles);
      initColumnHandles();

    } catch (final RocksDBException e) {
      throw parseRocksDBException(e, segments, ignorableSegments);
    }
  }

  @Override
  RocksDB getDB() {
    return db;
  }

  /**
   * Start a transaction
   *
   * @return the new transaction started
   * @throws StorageException the storage exception
   */
  @Override
  public SegmentedKeyValueStorageTransaction startTransaction() throws StorageException {
    throwIfClosed();
    final WriteOptions writeOptions = new WriteOptions();
    writeOptions.setIgnoreMissingColumnFamilies(true);
    return new SegmentedKeyValueStorageTransactionValidatorDecorator(
        new RocksDBTransaction(
            this::safeColumnHandle, db.beginTransaction(writeOptions), writeOptions),
        this.closed::get);
  }
}
