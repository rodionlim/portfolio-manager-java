package com.rodion.pfm.plugin.services.storage.rocksdb;

import com.rodion.pfm.plugin.services.exception.StorageException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/** The RocksDB segment identifier. */
public class RocksDBSegmentIdentifier {

  private final RocksDB db;

  private final AtomicReference<ColumnFamilyHandle> reference;

  /**
   * Instantiates a new RocksDb segment identifier.
   *
   * @param db the db
   * @param columnFamilyHandle the column family handle
   */
  public RocksDBSegmentIdentifier(final RocksDB db, final ColumnFamilyHandle columnFamilyHandle) {
    this.db = db;
    this.reference = new AtomicReference<>(columnFamilyHandle);
  }

  /** Reset. */
  public void reset() {
    reference.getAndUpdate(
        oldHandle -> {
          try {
            ColumnFamilyDescriptor descriptor =
                new ColumnFamilyDescriptor(
                    oldHandle.getName(), oldHandle.getDescriptor().getOptions());
            db.dropColumnFamily(oldHandle);
            ColumnFamilyHandle newHandle = db.createColumnFamily(descriptor);
            oldHandle.close();
            return newHandle;
          } catch (final RocksDBException e) {
            throw new StorageException(e);
          }
        });
  }

  /**
   * Get column family handle.
   *
   * @return the column family handle
   */
  public ColumnFamilyHandle get() {
    return reference.get();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RocksDBSegmentIdentifier that = (RocksDBSegmentIdentifier) o;
    return Objects.equals(reference.get(), that.reference.get());
  }

  @Override
  public int hashCode() {
    return reference.get().hashCode();
  }
}
