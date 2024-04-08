package com.rodion.pfm.services;

import com.rodion.pfm.plugin.services.PicoCLIOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/** The Pico cli options service implementation to specify plugins. */
public class PicoCLIOptionsImpl implements PicoCLIOptions {

  private static final Logger logger = LoggerFactory.getLogger(PicoCLIOptionsImpl.class);

  private final CommandLine commandLine;

  /**
   * Instantiates a new Pico cli options.
   *
   * @param commandLine the command line
   */
  public PicoCLIOptionsImpl(final CommandLine commandLine) {
    this.commandLine = commandLine;
  }

  @Override
  public void addPicoCLIOptions(String namespace, Object optionObject) {
    final String pluginPrefix = "--plugin-" + namespace + "-";
    final String unstablePrefix = "--Xplugin-" + namespace + "-";
    final CommandLine.Model.CommandSpec mixin =
        CommandLine.Model.CommandSpec.forAnnotatedObject(optionObject);
    boolean badOptionName = false;

    for (final CommandLine.Model.OptionSpec optionSpec : mixin.options()) {
      for (final String optionName : optionSpec.names()) {
        if (!optionName.startsWith(pluginPrefix) && !optionName.startsWith(unstablePrefix)) {
          badOptionName = true;
          logger.error(
              "Plugin option {} did not have the expected prefix of {}", optionName, pluginPrefix);
        }
      }
    }
    if (badOptionName) {
      throw new RuntimeException("Error loading CLI options");
    } else {
      commandLine.getCommandSpec().addMixin("Plugin " + namespace, mixin);
    }
  }
}
