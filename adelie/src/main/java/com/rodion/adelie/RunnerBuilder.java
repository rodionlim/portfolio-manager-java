package com.rodion.adelie;

import com.rodion.adelie.controller.AdelieController;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The builder for Runner class. */
public class RunnerBuilder {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilder.class);

  private AdelieController adelieController;
  private Path dataDir;

  /**
   * Add Adelie controller.
   *
   * @param adelieController the adelie controller
   * @return the runner builder
   */
  public RunnerBuilder adelieController(final AdelieController adelieController) {
    this.adelieController = adelieController;
    return this;
  }

  /**
   * Add Data dir.
   *
   * @param dataDir the data dir
   * @return the runner builder
   */
  public RunnerBuilder dataDir(final Path dataDir) {
    this.dataDir = dataDir;
    return this;
  }

  /**
   * Build Runner instance.
   *
   * @return the runner
   */
  public Runner build() {
    return new Runner(adelieController, dataDir);
  }
}
