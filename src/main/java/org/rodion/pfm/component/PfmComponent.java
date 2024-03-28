package org.rodion.pfm.component;

import dagger.Component;
import org.rodion.pfm.cli.PfmCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
        PfmCommandModule.class
})
public interface PfmComponent {
    /**
     * the configured and parsed representation of the user issued command to run Portfolio Manager
     *
     * @return PfmCommand
     */
    PfmCommand getPfmCommand();
}
