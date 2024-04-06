package org.rodion.pfm.plugin.services.storage.rocksdb;

import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.rodion.pfm.plugin.PfmContext;
import org.rodion.pfm.plugin.PfmPlugin;
import org.rodion.pfm.plugin.services.PicoCLIOptions;
import org.rodion.pfm.plugin.services.StorageService;
import org.rodion.pfm.plugin.services.storage.rocksdb.configuration.RocksDBCLIOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDBPlugin implements PfmPlugin {

  private static final Logger logger = LoggerFactory.getLogger(RocksDBPlugin.class);

  private static final String NAME = "rocksdb";

  private PfmContext context;

  private final RocksDBCLIOptions options;

  public RocksDBPlugin() {
    options = RocksDBCLIOptions.create();
  }

  @Override
  public void register(PfmContext context) {
    logger.debug("Registering plugin");
    this.context = context;

    Optional<PicoCLIOptions> cmdlineOptions = context.getService(PicoCLIOptions.class);

    if (cmdlineOptions.isEmpty()) {
      throw new IllegalStateException(
          "Expecting a PicoCLI options to register CLI options with, but none found.");
    }

    cmdlineOptions.get().addPicoCLIOptions(NAME, options);
    createFactoriesAndRegisterWithStorageService();

    logger.debug("Plugin registered");
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}

  private void createAndRegister(final StorageService service) {
    throw new NotImplementedException();
  }

  private void createFactoriesAndRegisterWithStorageService() {
    context
        .getService(StorageService.class)
        .ifPresentOrElse(
            this::createAndRegister,
            () ->
                logger.error("Failed to register KeyValueFactory due to missing StorageService."));
  }
}
