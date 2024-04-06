package org.rodion.pfm.component;

import dagger.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import org.rodion.pfm.cli.PfmCommand;
import org.rodion.pfm.services.PfmPluginContextImpl;
import org.slf4j.Logger;

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