package com.rodion.adelie.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import com.rodion.adelie.component.AdelieComponent;
import com.rodion.adelie.pfm.storage.keyvalue.StorageProvider;
import java.io.Closeable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Adelie controller builder that builds Adelie Controller. */
public class AdelieControllerBuilder {
  private static final Logger logger = LoggerFactory.getLogger(AdelieControllerBuilder.class);

  /** The Data directory. */
  protected Path dataDirectory;

  /** The Storage provider. */
  protected StorageProvider storageProvider;

  /** the Dagger configured context that can provide dependencies */
  protected Optional<AdelieComponent> adelieComponent = Optional.empty();

  /**
   * Provide an AdelieComponent which can be used to get other dependencies
   *
   * @param adelieComponent application context that can be used to get other dependencies
   * @return the adelie controller builder
   */
  public AdelieControllerBuilder adelieComponent(final AdelieComponent adelieComponent) {
    this.adelieComponent = Optional.ofNullable(adelieComponent);
    return this;
  }

  /**
   * Data directory adelie controller builder.
   *
   * @param dataDirectory the data directory
   * @return the adelie controller builder
   */
  public AdelieControllerBuilder dataDirectory(final Path dataDirectory) {
    this.dataDirectory = dataDirectory;
    return this;
  }

  /**
   * Storage provider adelie controller builder.
   *
   * @param storageProvider the storage provider
   * @return the adelie controller builder
   */
  public AdelieControllerBuilder storageProvider(final StorageProvider storageProvider) {
    this.storageProvider = storageProvider;
    return this;
  }

  /**
   * Build adelie controller.
   *
   * @return the adelie controller
   */
  public AdelieController build() {
    checkNotNull(dataDirectory, "Missing data directory");
    checkNotNull(storageProvider, "Missing storage provider");
    final List<Closeable> closeables = new ArrayList<>();
    return new AdelieController(closeables, storageProvider);
  }
}
