package com.rodion.adelie.pfm.storage.keyvalue;

import static com.rodion.adelie.plugin.services.storage.DataStorageFormat.FOREST;

import com.rodion.adelie.plugin.services.storage.DataStorageFormat;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

// Generic key value segments in Portfolio Manager
public enum KeyValueSegmentIdentifier implements SegmentIdentifier {
  DEFAULT("default".getBytes(StandardCharsets.UTF_8)),
  MARKET_DATA(new byte[] {1}, false, false),
  BLOTTER(new byte[] {2}, false, false);

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
