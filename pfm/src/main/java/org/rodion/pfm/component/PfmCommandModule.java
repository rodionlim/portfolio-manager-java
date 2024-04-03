package org.rodion.pfm.component;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.rodion.pfm.PortfolioManager;
import org.rodion.pfm.cli.PfmCommand;
import org.slf4j.Logger;

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
