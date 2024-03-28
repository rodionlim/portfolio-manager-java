package org.rodion.pfm.component;

import dagger.Module;
import dagger.Provides;
import org.rodion.pfm.cli.PfmCommand;

import javax.inject.Singleton;

@Module
public class PfmCommandModule {
    @Provides
    @Singleton
    PfmCommand providePfmCommand() {
        final PfmCommand pfmCommand = new PfmCommand();
        pfmCommand.toCommandLine();
        return pfmCommand;
    }
}
