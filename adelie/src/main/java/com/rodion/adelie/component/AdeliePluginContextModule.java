package com.rodion.adelie.component;

import com.rodion.adelie.services.AdeliePluginContextImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/** A dagger module that know how to create the AdeliePluginContextImpl singleton. */
@Module
public class AdeliePluginContextModule {
  @Provides
  @Named("adeliePluginContext")
  @Singleton
  AdeliePluginContextImpl provideAdeliePluginContext() {
    return new AdeliePluginContextImpl();
  }
}
