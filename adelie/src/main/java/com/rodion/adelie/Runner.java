package com.rodion.adelie;

import com.rodion.adelie.controller.AdelieController;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Runner controls various Adelie services lifecycle. */
public class Runner implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(Runner.class);

  private final AdelieController adelieController;
  private final Path dataDir;

  /**
   * Instantiates a new Runner.
   *
   * @param adelieController the adelie controller
   * @param dataDir the data dir
   */
  Runner(final AdelieController adelieController, final Path dataDir) {
    this.dataDir = dataDir;
    this.adelieController = adelieController;
  }

  public void startPfmMainLoop() {
    try {
      logger.info("Starting Pfm (Portfolio Manager) main loop");
      logger.info("Pfm main loop is up.");
    } catch (final Exception ex) {
      logger.error("unable to start main loop", ex);
      throw new IllegalStateException("Startup failed", ex);
    }
  }

  public void stop() {
    adelieController.close();
  }

  @Override
  public void close() throws Exception {
    stop();
  }
}
