package com.rodion.pfm.plugin.services.storage;

/** The interface Snapped key value storage. */
public interface SnappedKeyValueStorage extends SegmentedKeyValueStorage {

  /**
   * Gets snapshot transaction.
   *
   * @return the snapshot transaction
   */
  SegmentedKeyValueStorageTransaction getSnapshotTransaction();
}
