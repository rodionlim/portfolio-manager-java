package com.rodion.pfm.component;

import com.rodion.pfm.PortfolioManager;
import com.rodion.pfm.cli.PfmCommand;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;

/**
 * A dagger module that know how to create the PfmCommand, which collects all configuration
 * settings.
 */
@Module
public class PfmCommandModule {
  @Provides
  @Singleton
  PfmCommand providePfmCommand(final PfmComponent pfmComponent) {
    final PfmCommand pfmCommand =
        new PfmCommand(pfmComponent, pfmComponent.getPfmPluginContextImpl());
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
