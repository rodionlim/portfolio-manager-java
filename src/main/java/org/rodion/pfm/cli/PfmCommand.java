package org.rodion.pfm.cli;

import static org.rodion.pfm.cli.DefaultCommandValues.MANDATORY_PATH_FORMAT_HELP;
import static org.rodion.pfm.cli.DefaultCommandValues.getDefaultPfmDataPath;

import java.io.InputStream;
import java.nio.file.Path;
import org.rodion.pfm.component.PfmComponent;
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
public class PfmCommand {

  private final Logger logger;

  private CommandLine commandLine;

  @CommandLine.Option(
      names = {"--data-path"},
      paramLabel = MANDATORY_PATH_FORMAT_HELP,
      description = "The path to Portfolio Manager data directory (default: ${DEFAULT-VALUE})")
  final Path dataPath = getDefaultPfmDataPath(this);

  public PfmCommand(final PfmComponent pfmComponent) {
    this.logger = pfmComponent.getPfmCommandLogger();
    logger.info("Loaded pfm command");
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

    return 0;
  }

  public void toCommandLine() {
    commandLine = new CommandLine(this, CommandLine.defaultFactory());
  }
}
