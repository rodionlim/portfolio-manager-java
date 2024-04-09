package com.rodion.pfm.plugin.services.storage.rocksdb;

import com.rodion.pfm.plugin.services.exception.StorageException;
import com.rodion.pfm.plugin.services.storage.SegmentIdentifier;
import com.rodion.pfm.plugin.services.storage.SegmentedKeyValueStorageTransaction;
import java.util.function.Function;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.Transaction;
import org.rocksdb.WriteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The RocksDB transaction. */
public class RocksDBTransaction implements SegmentedKeyValueStorageTransaction {
  private static final Logger logger = LoggerFactory.getLogger(RocksDBTransaction.class);
  private static final String NO_SPACE_LEFT_ON_DEVICE = "No space left on device";

  private final Transaction innerTx;
  private final WriteOptions options;
  private final Function<SegmentIdentifier, ColumnFamilyHandle> columnFamilyMapper;

  /**
   * Instantiates a new RocksDb transaction.
   *
   * @param columnFamilyMapper mapper from segment identifier to column family handle
   * @param innerTx the inner tx
   * @param options the options
   */
  public RocksDBTransaction(
      final Function<SegmentIdentifier, ColumnFamilyHandle> columnFamilyMapper,
      final Transaction innerTx,
      final WriteOptions options) {
    this.columnFamilyMapper = columnFamilyMapper;
    this.innerTx = innerTx;
    this.options = options;
  }

  @Override
  public void put(final SegmentIdentifier segmentId, final byte[] key, final byte[] value) {
    try {
      innerTx.put(columnFamilyMapper.apply(segmentId), key, value);
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
    try {
      innerTx.delete(columnFamilyMapper.apply(segmentId), key);
    } catch (final RocksDBException e) {
      if (e.getMessage().contains(NO_SPACE_LEFT_ON_DEVICE)) {
        logger.error(e.getMessage());
        System.exit(0);
      }
      throw new StorageException(e);
    }
  }

  @Override
  public void commit() throws StorageException {
    try {
      innerTx.commit();
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

  @Override
  public void rollback() {
    try {
      innerTx.rollback();
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

  private void close() {
    innerTx.close();
    options.close();
  }
}
