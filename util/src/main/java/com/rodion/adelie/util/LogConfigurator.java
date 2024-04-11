package com.rodion.adelie.util;

import java.util.NoSuchElementException;
import org.slf4j.LoggerFactory;

/** The library independent logger configurator util. */
@SuppressWarnings("CatchAndPrintStackTrace")
public interface LogConfigurator {

  /**
   * Sets level to specified logger.
   *
   * @param parentLogger the logger name
   * @param level the level
   */
  static void setLevel(final String parentLogger, final String level) {
    try {
      // ensure we have at least one log context, to load configs
      LoggerFactory.getLogger(LogConfigurator.class);
      Log4j2ConfiguratorUtil.setAllLevels(parentLogger, level);
    } catch (NoClassDefFoundError | ClassCastException | NoSuchElementException e) {
      // This is expected when Log4j support is not in the classpath, so ignore
    }
  }

  /** Reconfigure. */
  static void reconfigure() {
    try {
      Log4j2ConfiguratorUtil.reconfigure();
    } catch (NoClassDefFoundError | ClassCastException | NoSuchElementException e) {
      // This is expected when Log4j support is not in the classpath, so ignore
    }
  }

  /** Shutdown. */
  static void shutdown() {
    try {
      Log4j2ConfiguratorUtil.shutdown();
    } catch (NoClassDefFoundError | ClassCastException | NoSuchElementException e) {
      // This is expected when Log4j support is not in the classpath, so ignore
    }
  }
}
