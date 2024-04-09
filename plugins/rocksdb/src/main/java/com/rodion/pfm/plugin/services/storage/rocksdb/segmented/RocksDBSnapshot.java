package com.rodion.pfm.plugin.services.storage.rocksdb.segmented;

import java.util.concurrent.atomic.AtomicInteger;
import org.rocksdb.OptimisticTransactionDB;
import org.rocksdb.Snapshot;

/**
 * Wraps and reference counts a Snapshot object from an OptimisticTransactionDB such that it can be
 * used as the basis of multiple RocksDBSnapshotTransaction's, and released once it is no longer in
 * use.
 */
class RocksDBSnapshot {

  private final OptimisticTransactionDB db;
  private final Snapshot dbSnapshot;
  private final AtomicInteger usages = new AtomicInteger(0);

  RocksDBSnapshot(final OptimisticTransactionDB db) {
    this.db = db;
    this.dbSnapshot = db.getSnapshot();
  }

  synchronized Snapshot markAndUseSnapshot() {
    usages.incrementAndGet();
    return dbSnapshot;
  }

  synchronized void unMarkSnapshot() {
    if (usages.decrementAndGet() < 1) {
      db.releaseSnapshot(dbSnapshot);
      dbSnapshot.close();
    }
  }
}
