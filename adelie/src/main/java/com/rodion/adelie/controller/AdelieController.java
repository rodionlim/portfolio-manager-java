package com.rodion.adelie.controller;

import com.rodion.adelie.pfm.storage.keyvalue.StorageProvider;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdelieController implements java.io.Closeable {
  private static final Logger logger = LoggerFactory.getLogger(AdelieController.class);
  private final StorageProvider storageProvider;
  private final List<Closeable> closeables;

  /**
   * Instantiates a new Adelie controller.
   *
   * @param storageProvider the storage provider
   */
  AdelieController(final List<Closeable> closeables, final StorageProvider storageProvider) {
    this.closeables = closeables;
    this.storageProvider = storageProvider;
  }

  /**
   * Get the storage provider
   *
   * @return the storage provider
   */
  public StorageProvider getStorageProvider() {
    return storageProvider;
  }

  @Override
  public void close() {
    closeables.forEach(this::tryClose);
  }

  private void tryClose(final Closeable closeable) {
    try {
      closeable.close();
    } catch (final IOException e) {
      logger.error("Unable to close resource.", e);
    }
  }
}
