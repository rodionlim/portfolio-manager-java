package org.rodion.pfm.component;

import dagger.Module;
import dagger.Provides;
import org.rodion.pfm.PortfolioManager;
import org.rodion.pfm.cli.PfmCommand;
import org.slf4j.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class PfmCommandModule {
    @Provides
    @Singleton
    PfmCommand providePfmCommand(final PfmComponent pfmComponent) {
        final PfmCommand pfmCommand = new PfmCommand(pfmComponent);
        pfmCommand.toCommandLine();
        return pfmCommand;
    }

    @Provides
    @Named("pfmCommandLogger")
    @Singleton
    Logger providePfmCommandLogger() {
        return PortfolioManager.getFirstLogger();
    }
}
