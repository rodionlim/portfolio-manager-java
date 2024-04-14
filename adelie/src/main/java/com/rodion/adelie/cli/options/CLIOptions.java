package com.rodion.adelie.cli.options;

import java.util.List;

/**
 * This interface represents logic that translates between CLI options and a domain object.
 *
 * @param <T> A class to be constructed from CLI arguments.
 */
public interface CLIOptions<T> {

  /**
   * Transform CLI options into a domain object.
   *
   * @return A domain object representing these CLI options.
   */
  T toDomainObject();

  /**
   * Return The list of CLI options corresponding to this class.
   *
   * @return The list of CLI options corresponding to this class.
   */
  List<String> getCLIOptions();
}
