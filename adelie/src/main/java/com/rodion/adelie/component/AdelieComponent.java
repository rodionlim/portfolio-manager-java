package com.rodion.adelie.component;

import com.rodion.adelie.cli.AdelieCommand;
import com.rodion.adelie.services.AdeliePluginContextImpl;
import dagger.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;

/** An application context that knows how to provide dependencies based on Dagger setup. */
@Singleton
@Component(modules = {AdelieCommandModule.class, AdeliePluginContextModule.class})
public interface AdelieComponent {
  /**
   * the configured and parsed representation of the user issued command to run Portfolio Manager
   *
   * @return AdelieCommand
   */
  AdelieCommand getAdelieCommand();

  @Named("adelieCommandLogger")
  Logger getAdelieCommandLogger();

  @Named("adeliePluginContext")
  AdeliePluginContextImpl getAdeliePluginContextImpl();
}
