package org.rodion.pfm.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import org.rodion.pfm.plugin.PfmContext;
import org.rodion.pfm.plugin.PfmPlugin;
import org.rodion.pfm.plugin.services.PfmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PfmPluginContextImpl implements PfmContext {

  private enum Lifecycle {
    /** Uninitialized lifecycle. */
    UNINITIALIZED,
    /** Registering lifecycle. */
    REGISTERING
  }

  private Lifecycle state = Lifecycle.UNINITIALIZED;

  public static final Logger logger = LoggerFactory.getLogger(PfmPluginContextImpl.class);

  private final Map<Class<?>, ? super PfmService> serviceRegistry = new HashMap<>();

  private final List<PfmPlugin> plugins = new ArrayList<>();

  final List<String> lines = new ArrayList<>();

  /**
   * Add service.
   *
   * @param <T> the type parameter
   * @param serviceType the service type
   * @param service the service
   */
  @Override
  public <T extends PfmService> void addService(final Class<T> serviceType, final T service) {
    checkArgument(serviceType.isInterface(), "Services must be Java interfaces.");
    checkArgument(
        serviceType.isInstance(service),
        "The service registered with a type must implement that type");
    serviceRegistry.put(serviceType, service);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends PfmService> Optional<T> getService(final Class<T> serviceType) {
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

    final ServiceLoader<PfmPlugin> serviceLoader =
        ServiceLoader.load(PfmPlugin.class, pluginLoader);
  }

  private Optional<ClassLoader> pluginDirectoryLoader(final Path pluginsDir) {
    if (pluginsDir != null && pluginsDir.toFile().isDirectory()) {
      logger.info("Searching for plugins in {}", pluginsDir.toAbsolutePath());
      try (final Stream<Path> pluginFilesList = Files.list(pluginsDir)) {
        final URL[] pluginJarURLs =
            pluginFilesList
                .filter(p -> p.getFileName().toString().endsWith(".jar"))
                .map(PfmPluginContextImpl::pathToURIOrNull)
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

  private static URL pathToURIOrNull(final Path p) {
    try {
      return p.toUri().toURL();
    } catch (final MalformedURLException e) {
      return null;
    }
  }
}
