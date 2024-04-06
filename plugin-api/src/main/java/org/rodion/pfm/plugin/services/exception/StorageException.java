package org.rodion.pfm.plugin.services.exception;

/** Base exception class for problems encountered in the domain for storage. */
public class StorageException extends RuntimeException {

  /**
   * Constructs a new storage exception with the specified cause.
   *
   * @param cause saved for later retrieval by the {@link #getCause()} method). (A {@code null}
   *     value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public StorageException(final Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new storage exception with the specified detail message and cause.
   *
   * @param message the detail that may be retrieved later by Throwable.getMessage().
   * @param cause saved for later retrieval by the {@link #getCause()} method). (A {@code null}
   *     value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public StorageException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new storage exception with the specified detail message.
   *
   * @param message the detail that may be retrieved later by Throwable.getMessage().
   */
  public StorageException(final String message) {
    super(message);
  }
}
