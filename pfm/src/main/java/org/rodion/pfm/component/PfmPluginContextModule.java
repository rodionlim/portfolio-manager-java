package org.rodion.pfm.component;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.rodion.pfm.services.PfmPluginContextImpl;

/** A dagger module that know how to create the PfmPluginContextImpl singleton. */
@Module
public class PfmPluginContextModule {
  @Provides
  @Named("pfmPluginContext")
  @Singleton
  PfmPluginContextImpl providePfmPluginContext() {
    return new PfmPluginContextImpl();
  }
}
