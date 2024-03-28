package org.rodion.pfm;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import org.rodion.pfm.cli.PfmCommand;
import org.rodion.pfm.cli.logging.PfmLoggingConfigurationFactory;
import org.rodion.pfm.component.DaggerPfmComponent;

/** Portfolio Manager bootstrap class */
public final class PortfolioManager {
    /**
     * The main entrypoint to PFM application
     *
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        setupLogging();
        final PfmCommand pfmCommand = DaggerPfmComponent.create().getPfmCommand();
        System.out.println(pfmCommand);
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

        try {
            System.setProperty(
                    "log4j.configurationFactory", PfmLoggingConfigurationFactory.class.getName());
            System.setProperty("log4j.skipJansi", String.valueOf(false));
        } catch (Throwable t) {
            System.out.printf(
                    "Could not set logging system property: %s - %s%n",
                    t.getClass().getSimpleName(), t.getMessage());
        }
    }
}