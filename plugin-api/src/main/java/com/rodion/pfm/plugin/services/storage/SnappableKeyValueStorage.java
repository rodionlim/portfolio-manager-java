package com.rodion.pfm.plugin.services.storage;

/** The interface Snappable key value storage. */
public interface SnappableKeyValueStorage extends SegmentedKeyValueStorage {

  /**
   * Take snapshot.
   *
   * @return the snapped key value storage
   */
  SnappedKeyValueStorage takeSnapshot();
}
