package com.rodion.adelie.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import picocli.CommandLine;

/** The interface Default command values. */
public interface DefaultCommandValues {

  /** The constant MANDATORY_PATH_FORMAT_HELP. */
  String MANDATORY_PATH_FORMAT_HELP = "<PATH>";

  /** The constant ADELIE_HOME_PROPERTY_NAME. */
  String ADELIE_HOME_PROPERTY_NAME = "adelie.home";

  /** The constant DEFAULT_DATA_DIR_PATH. */
  String DEFAULT_DATA_DIR_PATH = "./build/data";

  /**
   * Gets default portfolio manager data path.
   *
   * @param command the command
   * @return the default portfolio manager data path
   */
  static Path getDefaultAdelieDataPath(final Object command) {
    // this property is retrieved from Gradle tasks or Adelie running shell script.
    final String adelieHomeProperty = System.getProperty(ADELIE_HOME_PROPERTY_NAME);
    final Path adelieHome;

    // If prop is found, then use it
    if (adelieHomeProperty != null) {
      try {
        adelieHome = Paths.get(adelieHomeProperty);
      } catch (final InvalidPathException e) {
        throw new CommandLine.ParameterException(
            new CommandLine(command),
            String.format(
                "Unable to define default data directory from %s property.", adelieHomeProperty),
            e);
      }
    } else {
      // otherwise use a default path.
      // That may only be used when NOT run from distribution script and Gradle as they all define
      // the property.
      try {
        final String path = new File(DEFAULT_DATA_DIR_PATH).getCanonicalPath();
        adelieHome = Paths.get(path);
      } catch (final IOException e) {
        throw new CommandLine.ParameterException(
            new CommandLine(command), "Unable to create default data directory.");
      }
    }

    // Try to create it, then verify if the provided path is not already existing and is not a
    // directory. Otherwise, if it doesn't exist or exists but is already a directory,
    // Runner will use it to store data.
    try {
      Files.createDirectories(adelieHome);
    } catch (final FileAlreadyExistsException e) {
      // Only thrown if it exists but is not a directory
      throw new CommandLine.ParameterException(
          new CommandLine(command),
          String.format("%s: already exists and is not a directory.", adelieHome.toAbsolutePath()),
          e);
    } catch (final Exception e) {
      throw new CommandLine.ParameterException(
          new CommandLine(command),
          String.format("Error creating directory %s.", adelieHome.toAbsolutePath()),
          e);
    }
    return adelieHome;
  }
}
