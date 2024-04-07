package org.rodion.pfm.storage;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import org.rodion.pfm.plugin.services.storage.DataStorageFormat;
import org.rodion.pfm.plugin.services.storage.SegmentIdentifier;

public enum KeyValueSegmentIdentifier implements SegmentIdentifier {
  DEFAULT("default".getBytes(StandardCharsets.UTF_8)),
  MARKET_DATA(new byte[] {1}, true, true),
  BLOTTER(new byte[] {2}, true, true);

  private final byte[] id;
  private final EnumSet<DataStorageFormat> formats;
  private final boolean containsStaticData;
  private final boolean eligibleToHighSpecFlag;
  private final boolean staticDataGarbageCollectionEnabled;

  KeyValueSegmentIdentifier(final byte[] id) {
    this(id, EnumSet.allOf(DataStorageFormat.class));
  }

  KeyValueSegmentIdentifier(
      final byte[] id, final boolean containsStaticData, final boolean eligibleToHighSpecFlag) {
    this(
        id,
        EnumSet.allOf(DataStorageFormat.class),
        containsStaticData,
        eligibleToHighSpecFlag,
        false);
  }

  KeyValueSegmentIdentifier(final byte[] id, final EnumSet<DataStorageFormat> formats) {
    this(id, formats, false, false, false);
  }

  KeyValueSegmentIdentifier(
      final byte[] id,
      final EnumSet<DataStorageFormat> formats,
      final boolean containsStaticData,
      final boolean eligibleToHighSpecFlag,
      final boolean staticDataGarbageCollectionEnabled) {
    this.id = id;
    this.formats = formats;
    this.containsStaticData = containsStaticData;
    this.eligibleToHighSpecFlag = eligibleToHighSpecFlag;
    this.staticDataGarbageCollectionEnabled = staticDataGarbageCollectionEnabled;
  }

  @Override
  public String getName() {
    return name();
  }

  @Override
  public byte[] getId() {
    return id;
  }

  @Override
  public boolean containsStaticData() {
    return containsStaticData;
  }

  @Override
  public boolean isEligibleToHighSpecFlag() {
    return eligibleToHighSpecFlag;
  }

  @Override
  public boolean isStaticDataGarbageCollectionEnabled() {
    return staticDataGarbageCollectionEnabled;
  }

  @Override
  public boolean includeInDatabaseFormat(final DataStorageFormat format) {
    return formats.contains(format);
  }
}
