package org.rodion.pfm.cli;

import org.rodion.pfm.component.PfmComponent;
import org.slf4j.Logger;

public class PfmCommand {

    private final Logger logger;

    public PfmCommand(final PfmComponent pfmComponent) {
        this.logger = pfmComponent.getPfmCommandLogger();
        logger.info("Loaded pfm command");
    }

    public int parse() {
        toCommandLine();
        return 0;
    }

    public void toCommandLine() {};
}
