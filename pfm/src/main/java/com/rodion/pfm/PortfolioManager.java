package com.rodion.pfm;

import com.rodion.pfm.cli.PfmCommand;
import com.rodion.pfm.cli.logging.PfmLoggingConfigurationFactory;
import com.rodion.pfm.component.DaggerPfmComponent;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.RunLast;

/** Portfolio Manager bootstrap class */
public final class PortfolioManager {
  /**
   * The main entrypoint to Portfolio Manager application
   *
   * @param args command line arguments.
   */
  public static void main(final String... args) {
    setupLogging();
    final PfmCommand pfmCommand = DaggerPfmComponent.create().getPfmCommand();
    int exitCode = pfmCommand.parse(new RunLast(), System.in, args);
    System.exit(exitCode);
  }

  /**
   * a Logger setup for handling any exceptions during the bootstrap process, to indicate to users
   * their CLI configuration had problems.
   */
  private static void setupLogging() {
    try {
      InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
    } catch (Throwable t) {
      System.out.printf(
          "Could not set netty log4j logger factory: %s - %s%n",
          t.getClass().getSimpleName(), t.getMessage());
    }

    try {
      System.setProperty(
          "log4j.configurationFactory", PfmLoggingConfigurationFactory.class.getName());
      System.setProperty("log4j.skipJansi", String.valueOf(false));
    } catch (Throwable t) {
      System.out.printf(
          "Could not set logging system property: %s - %s%n",
          t.getClass().getSimpleName(), t.getMessage());
    }
  }

  /**
   * Returns the first logger to be created. This is used to set the default uncaught exception
   *
   * @return Logger
   */
  public static Logger getFirstLogger() {
    final Logger logger = LoggerFactory.getLogger(PortfolioManager.class);
    Thread.setDefaultUncaughtExceptionHandler(slf4jExceptionHandler(logger));
    Thread.currentThread().setUncaughtExceptionHandler(slf4jExceptionHandler(logger));

    return logger;
  }

  private static Thread.UncaughtExceptionHandler slf4jExceptionHandler(final Logger logger) {
    return (thread, error) -> {
      if (logger.isErrorEnabled()) {
        logger.error(String.format("Uncaught exception in thread \"%s\"", thread.getName()), error);
      }
    };
  }
}
