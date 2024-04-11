package com.rodion.adelie.cli;

import static com.rodion.adelie.cli.DefaultCommandValues.getDefaultAdelieDataPath;

import com.google.common.annotations.VisibleForTesting;
import com.rodion.adelie.cli.options.stable.LoggingLevelOption;
import com.rodion.adelie.cli.subcommands.MarketDataSubCommand;
import com.rodion.adelie.component.AdelieComponent;
import com.rodion.adelie.plugin.services.PicoCLIOptions;
import com.rodion.adelie.plugin.services.StorageService;
import com.rodion.adelie.plugin.services.storage.rocksdb.RocksDBPlugin;
import com.rodion.adelie.services.AdeliePluginContextImpl;
import com.rodion.adelie.services.MarketDataServiceImpl;
import com.rodion.adelie.services.PicoCLIOptionsImpl;
import com.rodion.adelie.services.StorageServiceImpl;
import com.rodion.adelie.util.LogConfigurator;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import org.slf4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExecutionStrategy;

/** Represents the main Portfolio Manager CLI command that runs the Adelie full node. */
@SuppressWarnings("FieldCanBeLocal") // because Picocli injected fields report false positives
@Command(
    description = "This command runs the Portfolio Manager client full node.",
    abbreviateSynopsis = true,
    name = "adelie",
    mixinStandardHelpOptions = true,
    header = "@|bold,fg(cyan) Usage:|@",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,fg(cyan) Description:|@%n%n",
    optionListHeading = "%n@|bold,fg(cyan) Options:|@%n",
    footerHeading = "%nAdelie is licensed under the Apache License 2.0%n",
    footer = {
      "%n%n@|fg(cyan) To get started quickly, just choose a profile to run with suggested defaults:|@",
      "%n@|fg(cyan) for Mainnet|@ --profile=[full|light]",
    })
public class AdelieCommand implements DefaultCommandValues, Runnable {

  private final Logger logger;

  private final LoggingLevelOption loggingLevelOption = LoggingLevelOption.create();

  private CommandLine commandLine;

  private final AdelieComponent adelieComponent;

  private final AdeliePluginContextImpl adeliePluginContext;

  private final MarketDataServiceImpl marketDataService;

  private final StorageServiceImpl storageService;

  private RocksDBPlugin rocksDBPlugin;

  @CommandLine.Option(
      names = {"--data-path"},
      paramLabel = MANDATORY_PATH_FORMAT_HELP,
      description = "The path to Portfolio Manager data directory (default: ${DEFAULT-VALUE})")
  final Path dataPath = getDefaultAdelieDataPath(this);

  /**
   * Portfolio Manager command constructor.
   *
   * @param adelieComponent AdelieComponent which acts as our application context
   * @param adeliePluginContext instance of AdeliePluginContextImpl
   */
  public AdelieCommand(
      final AdelieComponent adelieComponent, final AdeliePluginContextImpl adeliePluginContext) {
    this(
        adelieComponent,
        adeliePluginContext,
        new MarketDataServiceImpl(),
        new StorageServiceImpl());
  }

  /**
   * Overloaded Portfolio Manager command constructor visible for testing.
   *
   * @param adelieComponent AdelieComponent which acts as our application context
   * @param adeliePluginContext instance of AdeliePluginContextImpl
   * @param marketDataServiceImpl instance of MarketDataServiceImpl
   */
  @VisibleForTesting
  protected AdelieCommand(
      final AdelieComponent adelieComponent,
      final AdeliePluginContextImpl adeliePluginContext,
      final MarketDataServiceImpl marketDataServiceImpl,
      final StorageServiceImpl storageServiceImpl) {
    this.logger = adelieComponent.getAdelieCommandLogger();
    this.adelieComponent = adelieComponent;
    this.adeliePluginContext = adeliePluginContext;
    this.marketDataService = marketDataServiceImpl;
    this.storageService = storageServiceImpl;

    logger.info("Successfully loaded Adélie portfolio manager command");
  }

  /**
   * Parse Portfolio Manager command line arguments. Visible for testing.
   *
   * @param resultHandler execution strategy. See PicoCLI. Typical argument is RunLast.
   * @param in Standard input stream
   * @param args arguments to Adelie command
   * @return success or failure exit code.
   */
  public int parse(
      final IExecutionStrategy resultHandler, final InputStream in, final String... args) {

    toCommandLine();

    // use terminal width for usage message
    commandLine.getCommandSpec().usageMessage().autoWidth(true);

    handleStableOptions();
    addSubCommands(in);
    preparePlugins();

    return parse(resultHandler, args);
  }

  private void preparePlugins() {
    adeliePluginContext.addService(PicoCLIOptions.class, new PicoCLIOptionsImpl(commandLine));
    adeliePluginContext.addService(StorageService.class, storageService);

    rocksDBPlugin = new RocksDBPlugin();
    rocksDBPlugin.register(adeliePluginContext);

    adeliePluginContext.registerPlugins(pluginsDir());
  }

  private int parse(final IExecutionStrategy resultHandler, final String... args) {
    return commandLine.setExecutionStrategy(resultHandler).execute(args);
  }

  @Override
  public void run() {
    try {
      configureLogging(true);
      logger.info("Starting Adélie");
    } catch (final Exception e) {
      logger.error("Failed to start Adélie", e);
      throw new CommandLine.ParameterException(this.commandLine, e.getMessage(), e);
    }
  }

  public void toCommandLine() {
    commandLine = new CommandLine(this, CommandLine.defaultFactory());
  }

  private void addSubCommands(final InputStream in) {
    commandLine.addSubcommand(
        MarketDataSubCommand.COMMAND_NAME, new MarketDataSubCommand(commandLine.getOut()));
  }

  private Path pluginsDir() {
    final String pluginsDir = System.getProperty("adelie.plugins.dir");
    if (pluginsDir == null) {
      return new File(System.getProperty("adelie.home", "."), "plugins").toPath();
    } else {
      return new File(pluginsDir).toPath();
    }
  }

  /**
   * Configure logging framework for Adelie
   *
   * @param announce sets to true to print the logging level on standard output
   */
  public void configureLogging(final boolean announce) {
    // To change the configuration if color was enabled/disabled
    LogConfigurator.reconfigure();
    // set log level per CLI flags
    final String logLevel = loggingLevelOption.getLogLevel();
    if (logLevel != null) {
      if (announce) {
        System.out.println("Setting logging level to " + logLevel);
      }
      LogConfigurator.setLevel("", logLevel);
    }
  }

  private void handleStableOptions() {
    commandLine.addMixin("Logging level", loggingLevelOption);
  }
}
