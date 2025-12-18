// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.synthstrom.deluge.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

/**
 * Color states to use for the Deluge pads.
 * Deluge uses RGB colors via SysEx or velocity-based palette colors via Note
 * messages.
 *
 * @author Jürgen Moßgraber
 */
public class DelugeColorManager extends ColorManager {
    /** ID for color when off. */
    /** ID for color when off. */
    public static final int DELUGE_COLOR_BLACK = 0;
    /** ID for color gray. */
    public static final int DELUGE_COLOR_GRAY = 0; // User finds 113 too bright, using BLACK/OFF for now
    /** ID for color red. */
    public static final int DELUGE_COLOR_RED = 5;
    /** ID for color orange. */
    public static final int DELUGE_COLOR_ORANGE = 20;
    /** ID for color yellow. */
    public static final int DELUGE_COLOR_YELLOW = 35;
    /** ID for color green. */
    public static final int DELUGE_COLOR_GREEN = 50;
    /** ID for color cyan. */
    public static final int DELUGE_COLOR_CYAN = 66;
    /** ID for color blue. */
    public static final int DELUGE_COLOR_BLUE = 82;
    /** ID for color purple. */
    public static final int DELUGE_COLOR_PURPLE = 90;
    /** ID for color magenta. */
    public static final int DELUGE_COLOR_MAGENTA = 0; // Disabled as per user request to hide lines
    /** ID for color white. */
    public static final int DELUGE_COLOR_WHITE = 127;
    /** ID for color dim red. */
    public static final int DELUGE_COLOR_RED_LO = 1;
    /** ID for color dim green. */
    public static final int DELUGE_COLOR_GREEN_LO = 49;
    /** ID for color dim blue. */
    public static final int DELUGE_COLOR_BLUE_LO = 81;
    /** ID for color dim white (grey). */
    public static final int DELUGE_COLOR_WHITE_LO = 113;

    // Aliases for compatibility
    public static final int DELUGE_COLOR_GREY_LO = DELUGE_COLOR_WHITE_LO;
    public static final int DELUGE_COLOR_GREY_MD = DELUGE_COLOR_WHITE_LO;
    public static final int DELUGE_COLOR_LIME = DELUGE_COLOR_GREEN; // Approx
    public static final int DELUGE_COLOR_PINK = DELUGE_COLOR_MAGENTA;
    public static final int DELUGE_COLOR_ROSE = DELUGE_COLOR_MAGENTA;

    // Custom colors created with ColorEx.fromRGB
    private static final ColorEx LIME = ColorEx.fromRGB(128, 255, 0);
    private static final ColorEx MAGENTA = ColorEx.fromRGB(255, 0, 255);

    /**
     * Constructor.
     */
    public DelugeColorManager() {
        this.registerColorIndex(Scales.SCALE_COLOR_OFF, DELUGE_COLOR_BLACK);
        this.registerColorIndex(Scales.SCALE_COLOR_OCTAVE, DELUGE_COLOR_BLACK);
        this.registerColorIndex(Scales.SCALE_COLOR_NOTE, DELUGE_COLOR_BLACK);
        this.registerColorIndex(Scales.SCALE_COLOR_OUT_OF_SCALE, DELUGE_COLOR_BLACK);

        this.registerColorIndex(AbstractFeatureGroup.BUTTON_COLOR_OFF, DELUGE_COLOR_BLACK);
        this.registerColorIndex(AbstractFeatureGroup.BUTTON_COLOR_ON, DELUGE_COLOR_WHITE);

        this.registerColorIndex(AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT, DELUGE_COLOR_WHITE);
        this.registerColorIndex(AbstractSequencerView.COLOR_STEP_HILITE_CONTENT, DELUGE_COLOR_WHITE);
        this.registerColorIndex(AbstractSequencerView.COLOR_STEP_MUTED, DELUGE_COLOR_GREY_MD);
        this.registerColorIndex(AbstractSequencerView.COLOR_STEP_MUTED_CONT, DELUGE_COLOR_GREY_LO);
        this.registerColorIndex(AbstractSequencerView.COLOR_STEP_SELECTED, DELUGE_COLOR_WHITE);

        this.registerColorIndex(AbstractSequencerView.COLOR_NO_CONTENT, DELUGE_COLOR_BLACK);

        this.registerColorIndex(AbstractSequencerView.COLOR_NO_CONTENT_4, DELUGE_COLOR_BLACK);
        this.registerColorIndex(AbstractSequencerView.COLOR_CONTENT, DELUGE_COLOR_BLUE);
        this.registerColorIndex(AbstractSequencerView.COLOR_CONTENT_CONT, DELUGE_COLOR_BLUE_LO);
        this.registerColorIndex(AbstractSequencerView.COLOR_PAGE, DELUGE_COLOR_WHITE);
        this.registerColorIndex(AbstractSequencerView.COLOR_ACTIVE_PAGE, DELUGE_COLOR_GREEN);
        this.registerColorIndex(AbstractSequencerView.COLOR_SELECTED_PAGE, DELUGE_COLOR_BLUE);
        this.registerColorIndex(AbstractSequencerView.COLOR_RESOLUTION, DELUGE_COLOR_ORANGE);
        this.registerColorIndex(AbstractSequencerView.COLOR_RESOLUTION_SELECTED, DELUGE_COLOR_WHITE);
        this.registerColorIndex(AbstractSequencerView.COLOR_RESOLUTION_OFF, DELUGE_COLOR_BLACK);
        this.registerColorIndex(AbstractSequencerView.COLOR_TRANSPOSE, DELUGE_COLOR_WHITE);
        this.registerColorIndex(AbstractSequencerView.COLOR_TRANSPOSE_SELECTED, DELUGE_COLOR_YELLOW);

        this.registerColorIndex(AbstractDrumView.COLOR_PAD_OFF, DELUGE_COLOR_BLACK);
        this.registerColorIndex(AbstractDrumView.COLOR_PAD_RECORD, DELUGE_COLOR_RED);
        this.registerColorIndex(AbstractDrumView.COLOR_PAD_PLAY, DELUGE_COLOR_GREEN);
        this.registerColorIndex(AbstractDrumView.COLOR_PAD_SELECTED, DELUGE_COLOR_BLUE);
        this.registerColorIndex(AbstractDrumView.COLOR_PAD_MUTED, DELUGE_COLOR_ORANGE);
        this.registerColorIndex(AbstractDrumView.COLOR_PAD_HAS_CONTENT, DELUGE_COLOR_YELLOW);
        this.registerColorIndex(AbstractDrumView.COLOR_PAD_NO_CONTENT, DELUGE_COLOR_GREY_LO);

        this.registerColorIndex(AbstractPlayView.COLOR_OFF, DELUGE_COLOR_BLACK);
        this.registerColorIndex(AbstractPlayView.COLOR_PLAY, DELUGE_COLOR_GREEN);
        this.registerColorIndex(AbstractPlayView.COLOR_RECORD, DELUGE_COLOR_RED);

        this.registerColorIndex(IPadGrid.GRID_OFF, DELUGE_COLOR_BLACK);

        // DAW colors (track and clip colors from the DAW)
        this.registerColorIndex(DAWColor.DAW_COLOR_DARK_GRAY, DELUGE_COLOR_GREY_LO);
        this.registerColorIndex(DAWColor.DAW_COLOR_GRAY, DELUGE_COLOR_GREY_MD);
        this.registerColorIndex(DAWColor.DAW_COLOR_GRAY_HALF, DELUGE_COLOR_GREY_MD);
        this.registerColorIndex(DAWColor.DAW_COLOR_LIGHT_GRAY, DELUGE_COLOR_WHITE);
        this.registerColorIndex(DAWColor.DAW_COLOR_SILVER, DELUGE_COLOR_WHITE);
        this.registerColorIndex(DAWColor.DAW_COLOR_DARK_BROWN, DELUGE_COLOR_ORANGE);
        this.registerColorIndex(DAWColor.DAW_COLOR_BROWN, DELUGE_COLOR_ORANGE);
        this.registerColorIndex(DAWColor.DAW_COLOR_DARK_BLUE, DELUGE_COLOR_BLUE_LO);
        this.registerColorIndex(DAWColor.DAW_COLOR_PURPLE_BLUE, DELUGE_COLOR_PURPLE);
        this.registerColorIndex(DAWColor.DAW_COLOR_PURPLE, DELUGE_COLOR_PURPLE);
        this.registerColorIndex(DAWColor.DAW_COLOR_PINK, DELUGE_COLOR_PINK);
        this.registerColorIndex(DAWColor.DAW_COLOR_RED, DELUGE_COLOR_RED);
        this.registerColorIndex(DAWColor.DAW_COLOR_ORANGE, DELUGE_COLOR_ORANGE);
        this.registerColorIndex(DAWColor.DAW_COLOR_LIGHT_ORANGE, DELUGE_COLOR_YELLOW);
        this.registerColorIndex(DAWColor.DAW_COLOR_MOSS_GREEN, DELUGE_COLOR_CYAN);
        this.registerColorIndex(DAWColor.DAW_COLOR_GREEN, DELUGE_COLOR_GREEN);
        this.registerColorIndex(DAWColor.DAW_COLOR_COLD_GREEN, DELUGE_COLOR_GREEN_LO);
        this.registerColorIndex(DAWColor.DAW_COLOR_BLUE, DELUGE_COLOR_BLUE);
        this.registerColorIndex(DAWColor.DAW_COLOR_LIGHT_PURPLE, DELUGE_COLOR_PURPLE);
        this.registerColorIndex(DAWColor.DAW_COLOR_LIGHT_PINK, DELUGE_COLOR_PINK);
        this.registerColorIndex(DAWColor.DAW_COLOR_ROSE, DELUGE_COLOR_ROSE);
        this.registerColorIndex(DAWColor.DAW_COLOR_REDDISH_BROWN, DELUGE_COLOR_ORANGE);
        this.registerColorIndex(DAWColor.DAW_COLOR_LIGHT_BROWN, DELUGE_COLOR_YELLOW);
        this.registerColorIndex(DAWColor.DAW_COLOR_LIGHT_GREEN, DELUGE_COLOR_LIME);
        this.registerColorIndex(DAWColor.DAW_COLOR_BLUISH_GREEN, DELUGE_COLOR_CYAN);
        this.registerColorIndex(DAWColor.DAW_COLOR_GREEN_BLUE, DELUGE_COLOR_CYAN);
        this.registerColorIndex(DAWColor.DAW_COLOR_LIGHT_BLUE, DELUGE_COLOR_BLUE);

        // Register colors for indices 0-127 mapping to themselves
        for (int i = 0; i < 128; i++)
            this.registerColor(i, ColorEx.BLACK);
    }

    /**
     * Get the ColorEx for a given color index.
     *
     * @param colorIndex The color index
     * @return The ColorEx instance
     */
    public ColorEx getColorEx(final int colorIndex) {
        switch (colorIndex) {
            case DELUGE_COLOR_BLACK:
                return ColorEx.BLACK;
            case DELUGE_COLOR_WHITE_LO:
                return ColorEx.GRAY;
            case DELUGE_COLOR_WHITE:
                return ColorEx.WHITE;
            case DELUGE_COLOR_RED:
                return ColorEx.RED;
            case DELUGE_COLOR_RED_LO:
                return ColorEx.DARK_RED;
            case DELUGE_COLOR_ORANGE:
                return ColorEx.ORANGE;
            case DELUGE_COLOR_YELLOW:
                return ColorEx.YELLOW;
            case DELUGE_COLOR_GREEN:
                return ColorEx.GREEN;
            case DELUGE_COLOR_GREEN_LO:
                return ColorEx.DARK_GREEN;
            case DELUGE_COLOR_CYAN:
                return ColorEx.CYAN;
            case DELUGE_COLOR_BLUE:
                return ColorEx.BLUE;
            case DELUGE_COLOR_BLUE_LO:
                return ColorEx.DARK_BLUE;
            case DELUGE_COLOR_PURPLE:
                return ColorEx.PURPLE;

            default:
                return ColorEx.BLACK;
        }
    }
}
