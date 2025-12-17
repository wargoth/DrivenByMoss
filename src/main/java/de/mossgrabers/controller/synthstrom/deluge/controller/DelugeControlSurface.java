// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.synthstrom.deluge.controller;

import de.mossgrabers.controller.synthstrom.deluge.DelugeConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The Deluge control surface for Controller Mode.
 *
 * @author Jürgen Moßgraber
 */
public class DelugeControlSurface extends AbstractControlSurface<DelugeConfiguration>
{
    // Deluge SysEx constants
    /** Synthstrom manufacturer ID: 0x00 0x21 0x7B. */
    public static final int []  DELUGE_SYSEX_ID           = { 0x00, 0x21, 0x7B };
    /** Controller mode sub-ID. */
    public static final int     CONTROLLER_MODE_ID        = 0x01;

    // SysEx commands (from controller_mode_midi_protocol.md)
    /** Set display text. */
    public static final int     CMD_SET_DISPLAY_TEXT      = 0x10;
    /** Set 7-segment display. */
    public static final int     CMD_SET_7SEG              = 0x11;
    /** Set OLED pixels. */
    public static final int     CMD_SET_OLED_PIXELS       = 0x12;
    /** Set LED color (single pad). */
    public static final int     CMD_SET_LED_COLOR         = 0x20;
    /** Set all LEDs. */
    public static final int     CMD_SET_ALL_LEDS          = 0x21;
    /** Batch LED update. */
    public static final int     CMD_BATCH_LED_UPDATE      = 0x22;
    /** Sidebar pad event. */
    public static final int     CMD_SIDEBAR_PAD_EVENT     = 0x30;
    /** Sidebar LED control. */
    public static final int     CMD_SIDEBAR_LED_CONTROL   = 0x31;
    /** Button event. */
    public static final int     CMD_BUTTON_EVENT          = 0x40;
    /** Button LED control. */
    public static final int     CMD_BUTTON_LED_CONTROL    = 0x41;

    // Grid dimensions
    /** Main grid width. */
    public static final int     GRID_WIDTH                = 16;
    /** Main grid height. */
    public static final int     GRID_HEIGHT               = 8;
    /** Sidebar width. */
    public static final int     SIDEBAR_WIDTH             = 2;

    // Button IDs (from controller_mode_midi_protocol.md)
    /** Button: Play. */
    public static final int     BUTTON_PLAY               = 0;
    /** Button: Record. */
    public static final int     BUTTON_RECORD             = 1;
    /** Button: Tap Tempo. */
    public static final int     BUTTON_TAP_TEMPO          = 2;
    /** Button: Sync Scaling. */
    public static final int     BUTTON_SYNC_SCALING       = 3;
    /** Button: Learn. */
    public static final int     BUTTON_LEARN              = 4;
    /** Button: Scale Mode. */
    public static final int     BUTTON_SCALE_MODE         = 5;
    /** Button: Cross Screen Edit. */
    public static final int     BUTTON_CROSS_SCREEN_EDIT  = 6;
    /** Button: Back. */
    public static final int     BUTTON_BACK               = 7;
    /** Button: Load. */
    public static final int     BUTTON_LOAD               = 8;
    /** Button: Save. */
    public static final int     BUTTON_SAVE               = 9;
    /** Button: Keyboard. */
    public static final int     BUTTON_KEYBOARD           = 10;
    /** Button: Kit. */
    public static final int     BUTTON_KIT                = 11;
    /** Button: Synth. */
    public static final int     BUTTON_SYNTH              = 12;
    /** Button: MIDI. */
    public static final int     BUTTON_MIDI               = 13;
    /** Button: CV. */
    public static final int     BUTTON_CV                 = 14;
    /** Button: Clip View. */
    public static final int     BUTTON_CLIP_VIEW          = 15;
    /** Button: Session View. */
    public static final int     BUTTON_SESSION_VIEW       = 16;
    /** Button: Affect Entire. */
    public static final int     BUTTON_AFFECT_ENTIRE      = 17;
    /** Button: Shift. */
    public static final int     BUTTON_SHIFT              = 18;
    /** Button: Select Encoder. */
    public static final int     BUTTON_SELECT_ENC         = 19;
    /** Button: Triplets. */
    public static final int     BUTTON_TRIPLETS           = 20;
    /** Button: X Encoder. */
    public static final int     BUTTON_X_ENC              = 21;
    /** Button: Y Encoder. */
    public static final int     BUTTON_Y_ENC              = 22;
    /** Button: Tempo Encoder. */
    public static final int     BUTTON_TEMPO_ENC          = 23;

    // Encoder CC numbers
    /** CC: Horizontal encoder. */
    public static final int     CC_HORIZONTAL             = 79;
    /** CC: Vertical encoder. */
    public static final int     CC_VERTICAL               = 80;
    /** CC: Select encoder. */
    public static final int     CC_SELECT                 = 81;
    /** CC: Tempo encoder. */
    public static final int     CC_TEMPO                  = 73;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public DelugeControlSurface (final IHost host, final ColorManager colorManager, final DelugeConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new DelugePadGrid (colorManager, output), 800, 600);
    }


    /**
     * Send text to the 7-segment display.
     *
     * @param text The text to display (max 4 characters)
     */
    public void send7SegText (final String text)
    {
        final String truncated = text.length () > 4 ? text.substring (0, 4) : text;
        final byte [] data = new byte [7 + truncated.length ()];
        data[0] = (byte) 0xF0;
        data[1] = (byte) DELUGE_SYSEX_ID[0];
        data[2] = (byte) DELUGE_SYSEX_ID[1];
        data[3] = (byte) DELUGE_SYSEX_ID[2];
        data[4] = (byte) CONTROLLER_MODE_ID;
        data[5] = (byte) CMD_SET_7SEG;
        for (int i = 0; i < truncated.length (); i++)
            data[6 + i] = (byte) truncated.charAt (i);
        data[6 + truncated.length ()] = (byte) 0xF7;
        this.output.sendSysex (data);
    }


    /**
     * Set a button LED state.
     *
     * @param buttonId The button ID (0-39)
     * @param on True to turn on, false to turn off
     */
    public void setButtonLED (final int buttonId, final boolean on)
    {
        this.output.sendSysex (new byte []
        {
            (byte) 0xF0,
            (byte) DELUGE_SYSEX_ID[0],
            (byte) DELUGE_SYSEX_ID[1],
            (byte) DELUGE_SYSEX_ID[2],
            (byte) CONTROLLER_MODE_ID,
            (byte) CMD_BUTTON_LED_CONTROL,
            (byte) buttonId,
            (byte) (on ? 1 : 0),
            (byte) 0xF7
        });
    }


    /**
     * Set a sidebar pad LED color via SysEx.
     *
     * @param sidebarCol 0 = left sidebar (mute), 1 = right sidebar (audition)
     * @param row The row (0-7)
     * @param r Red component (0-127)
     * @param g Green component (0-127)
     * @param b Blue component (0-127)
     */
    public void setSidebarPadColor (final int sidebarCol, final int row, final int r, final int g, final int b)
    {
        this.output.sendSysex (new byte []
        {
            (byte) 0xF0,
            (byte) DELUGE_SYSEX_ID[0],
            (byte) DELUGE_SYSEX_ID[1],
            (byte) DELUGE_SYSEX_ID[2],
            (byte) CONTROLLER_MODE_ID,
            (byte) CMD_SIDEBAR_LED_CONTROL,
            (byte) sidebarCol,
            (byte) row,
            (byte) r,
            (byte) g,
            (byte) b,
            (byte) 0xF7
        });
    }
}
