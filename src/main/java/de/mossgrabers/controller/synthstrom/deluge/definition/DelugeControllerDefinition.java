// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.synthstrom.deluge.definition;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Synthstrom Deluge controller in Controller Mode.
 *
 * @author Jürgen Moßgraber
 */
public class DelugeControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("42387123-2884-4809-9065-D35F6FF13F21");


    /**
     * Constructor.
     */
    public DelugeControllerDefinition ()
    {
        super (EXTENSION_ID, "Deluge", "Synthstrom", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        // Deluge appears as "Deluge" or "Deluge Port 1" on different systems
        return this.createDeviceDiscoveryPairs ("Deluge");
    }


    /** {@inheritDoc} */
    @Override
    public UsbMatcher claimUSBDevice ()
    {
        // USB VID:PID for Deluge - may need adjustment
        return null;
    }
}
