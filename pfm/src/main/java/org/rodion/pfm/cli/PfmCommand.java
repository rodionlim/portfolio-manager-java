package org.rodion.pfm.cli;

import static org.rodion.pfm.cli.DefaultCommandValues.getDefaultPfmDataPath;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import org.rodion.pfm.cli.subcommands.MarketDataSubCommand;
import org.rodion.pfm.component.PfmComponent;
import org.rodion.pfm.plugin.services.PicoCLIOptions;
import org.rodion.pfm.plugin.services.storage.rocksdb.RocksDBPlugin;
import org.rodion.pfm.services.MarketDataServiceImpl;
import org.rodion.pfm.services.PfmPluginContextImpl;
import org.rodion.pfm.services.PicoCLIOptionsImpl;
import org.slf4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExecutionStrategy;

@Command(
    description = "This command runs the Portfolio Manager client full node.",
    abbreviateSynopsis = true,
    name = "pfm",
    mixinStandardHelpOptions = true,
    header = "@|bold,fg(cyan) Usage:|@",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,fg(cyan) Description:|@%n%n",
    optionListHeading = "%n@|bold,fg(cyan) Options:|@%n",
    footerHeading = "%nPfm is licensed under the Apache License 2.0%n",
    footer = {
      "%n%n@|fg(cyan) To get started quickly, just choose a profile to run with suggested defaults:|@",
      "%n@|fg(cyan) for Mainnet|@ --profile=[full|light]",
    })
public class PfmCommand implements DefaultCommandValues, Runnable {

  private final Logger logger;

  private CommandLine commandLine;

  private final PfmComponent pfmComponent;

  private final PfmPluginContextImpl pfmPluginContext;

  private final MarketDataServiceImpl marketDataService;

  private RocksDBPlugin rocksDBPlugin;

  @CommandLine.Option(
      names = {"--data-path"},
      paramLabel = MANDATORY_PATH_FORMAT_HELP,
      description = "The path to Portfolio Manager data directory (default: ${DEFAULT-VALUE})")
  final Path dataPath = getDefaultPfmDataPath(this);

  /**
   * Portfolio Manager command constructor.
   *
   * @param pfmComponent PfmComponent which acts as our application context
   * @param pfmPluginContext instance of PfmPluginContextImpl
   */
  public PfmCommand(final PfmComponent pfmComponent, final PfmPluginContextImpl pfmPluginContext) {
    this(pfmComponent, pfmPluginContext, new MarketDataServiceImpl());
  }

  /**
   * Overloaded Portfolio Manager command constructor visible for testing.
   *
   * @param pfmComponent PfmComponent which acts as our application context
   * @param pfmPluginContext instance of PfmPluginContextImpl
   * @param marketDataServiceImpl instance of MarketDataServiceImpl
   */
  @VisibleForTesting
  protected PfmCommand(
      final PfmComponent pfmComponent,
      final PfmPluginContextImpl pfmPluginContext,
      final MarketDataServiceImpl marketDataServiceImpl) {
    this.logger = pfmComponent.getPfmCommandLogger();
    this.pfmComponent = pfmComponent;
    this.pfmPluginContext = pfmPluginContext;
    this.marketDataService = marketDataServiceImpl;
    logger.info("successfully loaded portfolio manager command");
  }

  /**
   * Parse Portfolio Manager command line arguments. Visible for testing.
   *
   * @param resultHandler execution strategy. See PicoCLI. Typical argument is RunLast.
   * @param in Standard input stream
   * @param args arguments to Pfm command
   * @return success or failure exit code.
   */
  public int parse(
      final IExecutionStrategy resultHandler, final InputStream in, final String... args) {

    toCommandLine();

    // use terminal width for usage message
    commandLine.getCommandSpec().usageMessage().autoWidth(true);

    addSubCommands(in);
    preparePlugins();

    return parse(resultHandler, args);
  }

  private void preparePlugins() {
    pfmPluginContext.addService(PicoCLIOptions.class, new PicoCLIOptionsImpl(commandLine));

    rocksDBPlugin = new RocksDBPlugin();
    rocksDBPlugin.register(pfmPluginContext);

    pfmPluginContext.registerPlugins(pluginsDir());
  }

  private int parse(final IExecutionStrategy resultHandler, final String... args) {
    return commandLine.setExecutionStrategy(resultHandler).execute(args);
  }

  @Override
  public void run() {}

  public void toCommandLine() {
    commandLine = new CommandLine(this, CommandLine.defaultFactory());
  }

  private void addSubCommands(final InputStream in) {
    commandLine.addSubcommand(
        MarketDataSubCommand.COMMAND_NAME, new MarketDataSubCommand(commandLine.getOut()));
  }

  private Path pluginsDir() {
    final String pluginsDir = System.getProperty("pfm.plugins.dir");
    if (pluginsDir == null) {
      return new File(System.getProperty("pfm.home", "."), "plugins").toPath();
    } else {
      return new File(pluginsDir).toPath();
    }
  }
}
