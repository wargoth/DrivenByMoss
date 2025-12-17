// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.synthstrom.deluge.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The pad grid implementation for the Deluge.
 * 16 columns x 8 rows grid.
 *
 * @author Jürgen Moßgraber
 */
public class DelugePadGrid extends PadGridImpl
{
    /**
     * Constructor.
     *
     * @param colorManager The color manager
     * @param output The MIDI output
     */
    public DelugePadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        // 16 columns x 8 rows, starting at note 0 (128 pads = notes 0-127)
        super (colorManager, output, 8, 16, 0);
    }
}
