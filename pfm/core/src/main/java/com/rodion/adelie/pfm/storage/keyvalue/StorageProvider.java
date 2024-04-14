package com.rodion.adelie.pfm.storage.keyvalue;

import com.rodion.adelie.pfm.blotter.BlotterStorage;
import com.rodion.adelie.plugin.services.storage.DataStorageConfiguration;
import com.rodion.adelie.plugin.services.storage.KeyValueStorage;
import com.rodion.adelie.plugin.services.storage.SegmentIdentifier;
import com.rodion.adelie.plugin.services.storage.SegmentedKeyValueStorage;
import java.io.Closeable;
import java.util.List;

public interface StorageProvider extends Closeable {

  BlotterStorage createBlotterStorage(DataStorageConfiguration dataStorageConfiguration);

  KeyValueStorage getStorageBySegmentIdentifier(SegmentIdentifier segment);

  SegmentedKeyValueStorage getStorageBySegmentIdentifiers(List<SegmentIdentifier> segment);
}
