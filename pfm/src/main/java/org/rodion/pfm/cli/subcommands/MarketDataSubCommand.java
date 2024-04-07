package org.rodion.pfm.cli.subcommands;

import static org.rodion.pfm.cli.subcommands.MarketDataSubCommand.COMMAND_NAME;

import java.io.PrintWriter;
import picocli.CommandLine;

/** Market Data subcommand */
@CommandLine.Command(
    name = COMMAND_NAME,
    aliases = {"md"},
    description = "Fetch market data related queries",
    mixinStandardHelpOptions = true)
public class MarketDataSubCommand implements Runnable {

  /** The constant COMMAND_NAME. */
  public static final String COMMAND_NAME = "marketdata";

  private final PrintWriter out;

  /**
   * Instantiates a new MarketData sub command.
   *
   * @param out the PrintWriter where validation results will be reported.
   */
  public MarketDataSubCommand(final PrintWriter out) {
    this.out = out;
  }

  @Override
  public void run() {
    out.println("Market data invoked");
  }
}
