package com.rodion.adelie.plugin.services.storage.rocksdb.exception;

/** The Invalid configuration exception. */
public class InvalidConfigurationException extends IllegalArgumentException {
  /**
   * Instantiates a new Invalid configuration exception.
   *
   * @param message the message
   */
  public InvalidConfigurationException(final String message) {
    super(message);
  }
}
