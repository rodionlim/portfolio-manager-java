package com.rodion.pfm.component;

import com.rodion.pfm.cli.PfmCommand;
import com.rodion.pfm.services.PfmPluginContextImpl;
import dagger.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;

/** An application context that knows how to provide dependencies based on Dagger setup. */
@Singleton
@Component(modules = {PfmCommandModule.class, PfmPluginContextModule.class})
public interface PfmComponent {
  /**
   * the configured and parsed representation of the user issued command to run Portfolio Manager
   *
   * @return PfmCommand
   */
  PfmCommand getPfmCommand();

  @Named("pfmCommandLogger")
  Logger getPfmCommandLogger();

  @Named("pfmPluginContext")
  PfmPluginContextImpl getPfmPluginContextImpl();
}
