package com.rodion.pfm.plugin.services.storage;

import com.rodion.pfm.plugin.Unstable;

/**
 * A namespace identifier for the storage instance segment, a grouping of data that should be kept
 * isolated from the data of other segments.
 */
@Unstable
public interface SegmentIdentifier {

  /**
   * Name for the segment consistent throughout the lifetime of the segment.
   *
   * @return unique name of the segment.
   */
  String getName();

  /**
   * Identifier for the segment consistent throughout the lifetime of the segment.
   *
   * @return unique id of the segment.
   */
  byte[] getId();

  /**
   * Not all segments are in all DB versions. This queries the segment to see if it is in the DB
   * format.
   *
   * @param format Version of the DB
   * @return true if the segment is in that DB format
   */
  default boolean includeInDatabaseFormat(final DataStorageFormat format) {
    return true;
  }

  /**
   * Define if this segment contains data that is never updated, but only added and optionally
   * deleted. Example is append only data like the blockchain. This information can be used by the
   * underlying implementation to apply specific optimization for this use case.
   *
   * @return true if the segment contains only static data
   */
  boolean containsStaticData();

  /**
   * This flag defines which segment is eligible for the high spec flag, so basically what column
   * family is involved with high spec flag
   *
   * @return true if the segment is involved with the high spec flag
   */
  boolean isEligibleToHighSpecFlag();

  /**
   * Enable garbage collection for static data. This should be enabled for static data which can be
   * deleted or pruned.
   *
   * @return true if enabled, false otherwise.
   */
  default boolean isStaticDataGarbageCollectionEnabled() {
    return false;
  }
}
