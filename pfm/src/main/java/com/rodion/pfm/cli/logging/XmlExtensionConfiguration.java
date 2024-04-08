package com.rodion.pfm.cli.logging;

import java.io.IOException;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Xml extension configuration for Logging framework. */
public class XmlExtensionConfiguration extends XmlConfiguration {

  /**
   * Instantiates a new Xml extension configuration.
   *
   * @param loggerContext the logger context
   * @param configSource the Configuration Source
   */
  public XmlExtensionConfiguration(
      final LoggerContext loggerContext, final ConfigurationSource configSource) {
    super(loggerContext, configSource);
  }

  @Override
  protected void doConfigure() {
    super.doConfigure();

    createConsoleAppender();
  }

  @Override
  public Configuration reconfigure() {
    final Configuration refreshedParent = super.reconfigure();

    if (refreshedParent != null
        && AbstractConfiguration.class.isAssignableFrom(refreshedParent.getClass())) {

      try {
        final XmlExtensionConfiguration refreshed =
            new XmlExtensionConfiguration(
                refreshedParent.getLoggerContext(),
                refreshedParent.getConfigurationSource().resetInputStream());
        createConsoleAppender();
        return refreshed;
      } catch (final IOException e) {
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
            .error("Failed to reload the Log4j2 Xml configuration file", e);
      }
    }

    LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
        .warn("Cannot programmatically reconfigure loggers");
    return refreshedParent;
  }

  private String dim(final String string) {
    return String.format("%%style{%s}{DIM}", string);
  }

  private String colorize(final String string) {
    return String.format("%%highlight{%s}{TRACE=normal}", string);
  }

  private final String SEP = dim(" | ");

  private void createConsoleAppender() {
    if (customLog4jConfigFilePresent()) {
      return;
    }

    final PatternLayout patternLayout =
        PatternLayout.newBuilder()
            .withConfiguration(this)
            .withPattern(
                String.join(
                    SEP,
                    dim("%d{yyyy-MM-dd HH:mm:ss.SSSZZZ}"),
                    dim("%t"),
                    colorize("%-5level"),
                    dim("%c{1}"),
                    colorize("%msg%n%throwable")))
            .build();
    final ConsoleAppender consoleAppender =
        ConsoleAppender.newBuilder().setName("Console").setLayout(patternLayout).build();
    consoleAppender.start();
    this.getRootLogger().addAppender(consoleAppender, null, null);
  }

  private static boolean noColorSet() {
    return System.getenv("NO_COLOR") != null;
  }

  private boolean customLog4jConfigFilePresent() {
    return Stream.of("LOG4J_CONFIGURATION_FILE", "log4j.configurationFile")
        .flatMap(
            configFileKey ->
                Stream.of(System.getenv(configFileKey), System.getProperty(configFileKey)))
        .flatMap(Stream::ofNullable)
        .findFirst()
        .isPresent();
  }
}
