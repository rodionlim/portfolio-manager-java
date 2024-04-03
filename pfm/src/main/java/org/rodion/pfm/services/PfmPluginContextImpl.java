package org.rodion.pfm.services;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.*;
import org.rodion.pfm.plugin.PfmContext;
import org.rodion.pfm.plugin.PfmPlugin;
import org.rodion.pfm.plugin.services.PfmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PfmPluginContextImpl implements PfmContext {

  public static final Logger logger = LoggerFactory.getLogger(PfmPluginContextImpl.class);

  private final Map<Class<?>, ? super PfmService> serviceRegistry = new HashMap<>();

  private final List<PfmPlugin> plugins = new ArrayList<>();

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
}
