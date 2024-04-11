package com.rodion.adelie.cli.options.stable;

import java.util.Locale;
import java.util.Set;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/** The Logging level CLI option. */
public class LoggingLevelOption {

  /**
   * Create logging level option.
   *
   * @return the logging level option
   */
  public static LoggingLevelOption create() {
    return new LoggingLevelOption();
  }

  private static final Set<String> ACCEPTED_VALUES =
      Set.of("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL");

  /** The Picocli CommandSpec. Visible for testing. Injected by Picocli framework at runtime. */
  @Spec CommandSpec spec;

  private String logLevel;

  /**
   * Sets log level.
   *
   * @param logLevel the log level
   */
  @CommandLine.Option(
      names = {"--logging", "-l"},
      paramLabel = "<LOG VERBOSITY LEVEL>",
      description = "Logging verbosity levels: OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL")
  public void setLogLevel(final String logLevel) {
    if ("FATAL".equalsIgnoreCase(logLevel)) {
      System.out.println("FATAL level is deprecated");
      this.logLevel = "ERROR";
    } else if (ACCEPTED_VALUES.contains(logLevel.toUpperCase(Locale.ROOT))) {
      this.logLevel = logLevel.toUpperCase(Locale.ROOT);
    } else {
      throw new CommandLine.ParameterException(
          spec.commandLine(), "Unknown logging value: " + logLevel);
    }
  }

  /**
   * Gets log level.
   *
   * @return the log level
   */
  public String getLogLevel() {
    return logLevel;
  }
}