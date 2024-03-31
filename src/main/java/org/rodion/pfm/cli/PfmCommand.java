package org.rodion.pfm.cli;

import java.io.InputStream;
import org.rodion.pfm.component.PfmComponent;
import org.slf4j.Logger;
import picocli.CommandLine.IExecutionStrategy;

public class PfmCommand {

  private final Logger logger;

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
    return 0;
  }

  public void toCommandLine() {}
  ;
}
