package com.rodion.pfm.plugin;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PfmPlugin {
  /**
   * Returns the name of the plugin. This name is used to trigger specific actions on individual
   * plugins.
   *
   * @return an {@link Optional} wrapping the unique name of the plugin.
   */
  default Optional<String> getName() {
    return Optional.of(this.getClass().getName());
  }

  /**
   * Called when the plugin is first registered with Portfolio Manager. Plugins are registered very
   * early in the Portfolio Manager life-cycle and should use this callback to register any command
   * line options required via the PicoCLIOptions service.
   *
   * <p>The <code>context</code> parameter should be stored in a field in the plugin. This is the
   * only time it will be provided to the plugin and is how the plugin will interact with Pfm.
   *
   * <p>Typically the plugin will not begin operation until the {@link #start()} method is called.
   *
   * @param context the context that provides access to Pfm services.
   */
  void register(PfmContext context);

  /**
   * Called once when portfolio manager has loaded configuration but before external services have
   * been started e.g. metrics and http
   */
  default void beforeExternalServices() {}

  /**
   * Called once Portfolio Manager has loaded configuration and has started external services but
   * before the main loop is up. The plugin should begin operation, including registering any event
   * listener with Pfm services and starting any background threads the plugin requires.
   */
  void start();

  /**
   * Called when the plugin is being reloaded. This method will be called through a dedicated JSON
   * RPC endpoint. If not overridden this method does nothing for convenience. The plugin should
   * only implement this method if it supports dynamic reloading.
   *
   * <p>The plugin should reload its configuration dynamically or do nothing if not applicable.
   *
   * @return a {@link CompletableFuture}
   */
  default CompletableFuture<Void> reloadConfiguration() {
    return CompletableFuture.completedFuture(null);
  }

  /**
   * Called when the plugin is being stopped. This method will be called as part of Portfolio
   * Manager shutting down but may also be called at other times to disable the plugin.
   *
   * <p>The plugin should remove any registered listeners and stop any background threads it
   * started.
   */
  void stop();
}
