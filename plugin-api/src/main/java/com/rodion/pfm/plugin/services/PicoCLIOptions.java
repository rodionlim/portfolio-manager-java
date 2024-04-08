package com.rodion.pfm.plugin.services;

/**
 * A service that plugins can use to add CLI options and commands to the PfmCommand. The PicoCLI
 * library annotations will be inspected and the object will be passed into a
 * picocli.CommandLine.addMixin call.
 *
 * <p>This service will be available during the registration callbacks.
 */
public interface PicoCLIOptions extends PfmService {

  /**
   * During the registration callback plugins can register CLI options that should be added to
   * Portfolio Manager's CLI startup.
   *
   * @param namespace A namespace prefix. All registered options must start with this prefix
   * @param optionObject The instance of the object to be inspected. PicoCLI will reflect the fields
   *     of this object to extract the CLI options.
   */
  void addPicoCLIOptions(String namespace, Object optionObject);
}
