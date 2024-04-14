package com.rodion.adelie.pfm.blotter;

import com.rodion.adelie.plugin.services.storage.DataStorageFormat;
import org.immutables.value.Value;

@Value.Immutable
@Value.Enclosing
public interface DataStorageConfiguration {

  DataStorageConfiguration DEFAULT_CONFIG =
      ImmutableDataStorageConfiguration.builder()
          .dataStorageFormat(DataStorageFormat.FOREST)
          .unstable(Unstable.DEFAULT)
          .build();

  DataStorageFormat getDataStorageFormat();

  @Value.Default
  default Unstable getUnstable() {
    return Unstable.DEFAULT;
  }

  @Value.Immutable
  interface Unstable {
    DataStorageConfiguration.Unstable DEFAULT =
        ImmutableDataStorageConfiguration.Unstable.builder().build();
  }
}
