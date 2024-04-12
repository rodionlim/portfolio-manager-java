package com.rodion.adelie.component;

import com.rodion.adelie.Adelie;
import com.rodion.adelie.RunnerBuilder;
import com.rodion.adelie.cli.AdelieCommand;
import com.rodion.adelie.controller.AdelieControllerBuilder;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;

/**
 * A dagger module that know how to create the AdelieCommand, which collects all configuration
 * settings.
 */
@Module
public class AdelieCommandModule {
  @Provides
  @Singleton
  AdelieCommand provideAdelieCommand(final AdelieComponent adelieComponent) {
    final AdelieCommand adelieCommand =
        new AdelieCommand(
            adelieComponent,
            new RunnerBuilder(),
            new AdelieControllerBuilder(),
            adelieComponent.getAdeliePluginContextImpl());
    adelieCommand.toCommandLine();
    return adelieCommand;
  }

  @Provides
  @Named("adelieCommandLogger")
  @Singleton
  Logger provideAdelieCommandLogger() {
    return Adelie.getFirstLogger();
  }
}
