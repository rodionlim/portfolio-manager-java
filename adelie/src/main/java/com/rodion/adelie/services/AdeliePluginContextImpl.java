package com.rodion.adelie.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.rodion.adelie.plugin.AdelieContext;
import com.rodion.adelie.plugin.AdeliePlugin;
import com.rodion.adelie.plugin.services.AdelieService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Portfolio Manager plugin context implementation. */
public class AdeliePluginContextImpl implements AdelieContext {

  private enum Lifecycle {
    /** Uninitialized lifecycle. */
    UNINITIALIZED,
    /** Registering lifecycle. */
    REGISTERING,
    /** Registered lifecycle. */
    REGISTERED,
    /** Before main loop started lifecycle. */
    BEFORE_MAIN_LOOP_STARTED,
    /** Before main loop finished lifecycle. */
    BEFORE_MAIN_LOOP_FINISHED,
    /** Stopping lifecycle. */
    STOPPING,
    /** Stopped lifecycle. */
    STOPPED
  }

  private Lifecycle state = Lifecycle.UNINITIALIZED;

  public static final Logger logger = LoggerFactory.getLogger(AdeliePluginContextImpl.class);

  private final Map<Class<?>, ? super AdelieService> serviceRegistry = new HashMap<>();

  private final List<AdeliePlugin> plugins = new ArrayList<>();

  private final List<String> pluginVersions = new ArrayList<>();

  final List<String> lines = new ArrayList<>();

  /**
   * Add service.
   *
   * @param <T> the type parameter
   * @param serviceType the service type
   * @param service the service
   */
  @Override
  public <T extends AdelieService> void addService(final Class<T> serviceType, final T service) {
    checkArgument(serviceType.isInterface(), "Services must be Java interfaces.");
    checkArgument(
        serviceType.isInstance(service),
        "The service registered with a type must implement that type");
    serviceRegistry.put(serviceType, service);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends AdelieService> Optional<T> getService(final Class<T> serviceType) {
    return Optional.ofNullable((T) serviceRegistry.get(serviceType));
  }

  /**
   * Register plugins
   *
   * @param pluginsDir the plugins directory
   */
  public void registerPlugins(final Path pluginsDir) {
    lines.add("Plugins:");
    checkState(
        state == Lifecycle.UNINITIALIZED,
        "Portfolio Manager plugins have already been registered. Cannot register additional plugins.");

    final ClassLoader pluginLoader =
        pluginDirectoryLoader(pluginsDir).orElse(this.getClass().getClassLoader());

    state = Lifecycle.REGISTERING;

    final ServiceLoader<AdeliePlugin> serviceLoader =
        ServiceLoader.load(AdeliePlugin.class, pluginLoader);

    int pluginsCount = 0;
    for (final AdeliePlugin plugin : serviceLoader) {
      pluginsCount++;
      try {
        plugin.register(this); // allow plugin to access portfolio manager via context
        logger.info("Registered plugin of type {}.", plugin.getClass().getName());
        String pluginVersion = getPluginVersion(plugin);
        pluginVersions.add(pluginVersion);
        lines.add(String.format("%s (%s)", plugin.getClass().getSimpleName(), pluginVersion));
      } catch (final Exception e) {
        logger.error(
            "Error registering plugin of type "
                + plugin.getClass().getName()
                + ", start and stop will not be called.",
            e);
        lines.add(String.format("ERROR %s", plugin.getClass().getSimpleName()));
        continue;
      }
      plugins.add(plugin);
    }
    logger.debug("Plugin registration complete.");
    lines.add(
        String.format(
            "TOTAL = %d of %d plugins successfully loaded", plugins.size(), pluginsCount));
    lines.add(String.format("from %s", pluginsDir.toAbsolutePath()));

    state = Lifecycle.REGISTERED;
  }

  private Optional<ClassLoader> pluginDirectoryLoader(final Path pluginsDir) {
    if (pluginsDir != null && pluginsDir.toFile().isDirectory()) {
      logger.info("Searching for plugins in {}", pluginsDir.toAbsolutePath());
      try (final Stream<Path> pluginFilesList = Files.list(pluginsDir)) {
        final URL[] pluginJarURLs =
            pluginFilesList
                .filter(p -> p.getFileName().toString().endsWith(".jar"))
                .map(AdeliePluginContextImpl::pathToURIOrNull)
                .toArray(URL[]::new);
        return Optional.of(new URLClassLoader(pluginJarURLs, this.getClass().getClassLoader()));
      } catch (final MalformedURLException e) {
        logger.error("Error converting files to URLs, could not load plugins", e);
      } catch (final IOException e) {
        logger.error("Error enumerating plugins, could not load plugins", e);
      }
    } else {
      logger.info("Plugin directory does not exist, skipping registration. - {}", pluginsDir);
    }
    return Optional.empty();
  }

  private String getPluginVersion(final AdeliePlugin plugin) {
    final Package pluginPackage = plugin.getClass().getPackage();
    final String implTitle =
        Optional.ofNullable(pluginPackage.getImplementationTitle())
            .filter(Predicate.not(String::isBlank))
            .orElse(plugin.getClass().getSimpleName());
    final String implVersion =
        Optional.ofNullable(pluginPackage.getImplementationVersion())
            .filter(Predicate.not(String::isBlank))
            .orElse("<Unknown Version>");
    return implTitle + "/v" + implVersion;
  }

  /** Start plugins. */
  public void startPlugins() {
    checkState(
        state == Lifecycle.REGISTERED,
        "AdelieContext should be in state %s but it was in %s",
        Lifecycle.REGISTERED,
        state);
    state = Lifecycle.BEFORE_MAIN_LOOP_STARTED;
    final Iterator<AdeliePlugin> pluginsIterator = plugins.iterator();

    while (pluginsIterator.hasNext()) {
      final AdeliePlugin plugin = pluginsIterator.next();

      try {
        plugin.start();
        logger.info("Started plugin of type {}.", plugin.getClass().getName());
      } catch (final Exception e) {
        logger.error(
            "Error starting plugin of type "
                + plugin.getClass().getName()
                + ", stop will not be called.",
            e);
        pluginsIterator.remove();
      }
    }

    logger.info("Plugin startup complete.");
    state = Lifecycle.BEFORE_MAIN_LOOP_FINISHED;
  }

  /** Stop plugins. */
  public void stopPlugins() {
    checkState(
        state == Lifecycle.BEFORE_MAIN_LOOP_FINISHED,
        "AdelieContext should be in state %s but it was in %s",
        Lifecycle.BEFORE_MAIN_LOOP_FINISHED,
        state);
    state = Lifecycle.STOPPING;

    for (final AdeliePlugin plugin : plugins) {
      try {
        plugin.stop();
        logger.info("Stopped plugin of type {}.", plugin.getClass().getName());
      } catch (final Exception e) {
        logger.error("Error stopping plugin of type " + plugin.getClass().getName(), e);
      }
    }

    logger.info("Plugin shutdown complete.");
    state = Lifecycle.STOPPED;
  }

  private static URL pathToURIOrNull(final Path p) {
    try {
      return p.toUri().toURL();
    } catch (final MalformedURLException e) {
      return null;
    }
  }
}
