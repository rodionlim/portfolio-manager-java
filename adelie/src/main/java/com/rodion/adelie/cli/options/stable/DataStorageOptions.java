package com.rodion.adelie.cli.options.stable;

import com.rodion.adelie.cli.options.CLIOptions;
import com.rodion.adelie.cli.util.CommandLineUtils;
import com.rodion.adelie.pfm.blotter.DataStorageConfiguration;
import com.rodion.adelie.pfm.blotter.ImmutableDataStorageConfiguration;
import com.rodion.adelie.plugin.services.storage.DataStorageFormat;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;

/** The Data storage CLI options. */
public class DataStorageOptions implements CLIOptions<DataStorageConfiguration> {

  private static final String DATA_STORAGE_FORMAT = "--data-storage-format";

  // Use Forest DB
  @Option(
      names = {DATA_STORAGE_FORMAT},
      description =
          "Format to store data in.  Either FOREST or BONSAI (default: ${DEFAULT-VALUE}).",
      arity = "1")
  private DataStorageFormat dataStorageFormat = DataStorageFormat.FOREST;

  @CommandLine.ArgGroup(validate = false)
  private final DataStorageOptions.Unstable unstableOptions = new Unstable();

  /** The unstable options for data storage. */
  public static class Unstable {}

  /**
   * Create data storage options.
   *
   * @return the data storage options
   */
  public static DataStorageOptions create() {
    return new DataStorageOptions();
  }

  /**
   * Converts to options from the configuration
   *
   * @param domainObject to be reversed
   * @return the options that correspond to the configuration
   */
  public static DataStorageOptions fromConfig(final DataStorageConfiguration domainObject) {
    final DataStorageOptions dataStorageOptions = DataStorageOptions.create();
    dataStorageOptions.dataStorageFormat = domainObject.getDataStorageFormat();

    return dataStorageOptions;
  }

  @Override
  public DataStorageConfiguration toDomainObject() {
    return ImmutableDataStorageConfiguration.builder()
        .dataStorageFormat(dataStorageFormat)
        .unstable(ImmutableDataStorageConfiguration.Unstable.builder().build())
        .build();
  }

  @Override
  public List<String> getCLIOptions() {
    return CommandLineUtils.getCLIOptions(this, new DataStorageOptions());
  }

  /**
   * Normalize data storage format string.
   *
   * @return the normalized string
   */
  public String normalizeDataStorageFormat() {
    return StringUtils.capitalize(dataStorageFormat.toString().toLowerCase(Locale.ROOT));
  }
}
