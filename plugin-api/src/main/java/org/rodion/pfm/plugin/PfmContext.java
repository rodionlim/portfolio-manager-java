package org.rodion.pfm.plugin;

import org.rodion.pfm.plugin.services.PfmService;

import java.util.Optional;

/** Allows plugins to access Portfolio Manager services. */
public interface PfmContext {
    /**
     * Add service.
     *
     * @param <T> the type parameter
     * @param serviceType the service type
     * @param service the service
     */
    <T extends PfmService> void addService(final Class<T> serviceType, final T service);

    /**
     * Get the requested service, if it is available. There are a number of reasons that a service may
     * not be available:
     *
     * <ul>
     *   <li>The service may not have started yet. Most services are not available before the {@link
     *       PfmPlugin#start()} method is called
     *   <li>The service is not supported by this version of Portfolio Manager
     *   <li>The service may not be applicable to the current configuration. For example some services
     *       may only be available when a proof of authority network is in use
     * </ul>
     *
     * <p>Since plugins are automatically loaded, unless the user has specifically requested
     * functionality provided by the plugin, no error should be raised if required services are
     * unavailable.
     *
     * @param serviceType the class defining the requested service.
     * @param <T> the service type
     * @return an optional containing the instance of the requested service, or empty if the service
     *     is unavailable
     */
    <T extends PfmService> Optional<T> getService(Class<T> serviceType);
}
