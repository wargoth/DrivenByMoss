// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.synthstrom.deluge;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.synthstrom.deluge.DelugeConfiguration;
import de.mossgrabers.controller.synthstrom.deluge.DelugeControllerSetup;
import de.mossgrabers.controller.synthstrom.deluge.controller.DelugeControlSurface;
import de.mossgrabers.controller.synthstrom.deluge.definition.DelugeControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for the Synthstrom Deluge extension.
 *
 * @author Jürgen Moßgraber
 */
public class DelugeControllerExtensionDefinition extends AbstractControllerExtensionDefinition<DelugeControlSurface, DelugeConfiguration>
{
    private static final DelugeControllerDefinition DEFINITION = new DelugeControllerDefinition ();


    /**
     * Constructor.
     */
    public DelugeControllerExtensionDefinition ()
    {
        super (DEFINITION);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<DelugeControlSurface, DelugeConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new DelugeControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
