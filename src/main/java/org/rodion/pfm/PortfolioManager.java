package org.rodion.pfm;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;

/** Portfolio Manager bootstrap class */
public final class PortfolioManager {
    /**
     * The main entrypoint to PFM application
     *
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        setupLogging();
        System.out.println("Hello world!");
    }

    /**
     * a Logger setup for handling any exceptions during the bootstrap process, to indicate to users
     * their CLI configuration had problems.
     */
    private static void setupLogging() {
        try {
            InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        } catch (Throwable t) {
            System.out.printf(
                    "Could not set netty log4j logger factory: %s - %s%n",
                    t.getClass().getSimpleName(), t.getMessage());
        }
    }
}