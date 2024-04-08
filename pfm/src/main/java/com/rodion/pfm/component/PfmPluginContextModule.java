package com.rodion.pfm.component;

import com.rodion.pfm.services.PfmPluginContextImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

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
