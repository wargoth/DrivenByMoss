// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.synthstrom.deluge;

import de.mossgrabers.controller.synthstrom.deluge.controller.DelugeColorManager;
import de.mossgrabers.controller.synthstrom.deluge.controller.DelugeControlSurface;
import de.mossgrabers.controller.synthstrom.deluge.view.DelugeClipView;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.Views;


/**
 * Setup for the Synthstrom Deluge controller in Controller Mode.
 *
 * @author Jürgen Moßgraber
 */
public class DelugeControllerSetup extends AbstractControllerSetup<DelugeControlSurface, DelugeConfiguration>
{
    // Gold knob encoder CC numbers (71, 72, 74-78 - note: 73 is Tempo)
    private static final int [] GOLD_KNOB_CCS = { 71, 72, 74, 75, 76, 77, 78, -1 };

    // Upper/lower mode for gold knobs
    private boolean [] goldKnobUpperMode = new boolean [8];

    // Track button states for combos
    private boolean xEncoderPressed = false;
    private boolean shiftPressed = false;


    /**
     * Constructor.
     *
     * @param host The host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document settings
     */
    public DelugeControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new DelugeColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new DelugeConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());

        // Initialize all gold knobs to upper mode
        for (int i = 0; i < 8; i++)
            this.goldKnobUpperMode[i] = true;
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        super.init ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 100, 16, 8);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableMainDrumDevice (true);
        ms.setNumTracks (8);
        ms.setNumScenes (8);
        ms.setNumSends (8);
        ms.setNumFilterColumnEntries (8);
        ms.setNumResults (8);
        ms.setNumDeviceLayers (8);
        ms.setNumDrumPadLayers (16);
        ms.setNumMarkers (8);
        ms.setNumParams (8);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "8?????", "9?????", "B?????");

        final DelugeControlSurface surface = new DelugeControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);

        // Handle SysEx messages for buttons and sidebar pads
        input.setSysexCallback (this::handleSysEx);

        // Handle CC messages for encoders
        input.setMidiCallback ( (status, data1, data2) -> this.handleMidi (status, data1, data2));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final DelugeControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        // Register the Clip View (sequencer)
        viewManager.register (Views.SEQUENCER, new DelugeClipView (surface, this.model));

        // Set Clip View as default
        viewManager.setActive (Views.SEQUENCER);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        // Transport commands are handled via SysEx button events in handleSysEx
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        // Encoder commands are handled via handleMidi callback
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        final DelugeControlSurface surface = this.getSurface ();
        final ITransport transport = this.model.getTransport ();
        surface.setButtonLED (DelugeControlSurface.BUTTON_TAP_TEMPO, transport.isMetronomeOn ());
        surface.setButtonLED (DelugeControlSurface.BUTTON_PLAY, transport.isPlaying ());
        surface.setButtonLED (DelugeControlSurface.BUTTON_RECORD, transport.isRecording ());
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        // Layout for visual debugging would be defined here
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final DelugeControlSurface surface = this.getSurface ();

        // Show startup message
        surface.send7SegText ("MOSS");

        // Activate the clip view
        surface.getViewManager ().setActive (Views.SEQUENCER);

        // Update sidebar LEDs
        this.updateSidebarLEDs ();
    }


    /**
     * Handle incoming MIDI CC messages.
     *
     * @param status The MIDI status byte
     * @param data1 CC number
     * @param data2 CC value
     */
    private void handleMidi (final int status, final int data1, final int data2)
    {
        // Only handle CC messages (0xB0-0xBF)
        if ((status & 0xF0) != 0xB0)
            return;

        final int delta = data2 > 64 ? data2 - 64 : data2 - 64; // Relative encoding centered at 64
        final DelugeControlSurface surface = this.getSurface ();

        switch (data1)
        {
            case DelugeControlSurface.CC_HORIZONTAL:
                this.handleHorizontalEncoder (delta);
                break;

            case DelugeControlSurface.CC_VERTICAL:
                this.handleVerticalEncoder (delta);
                break;

            case DelugeControlSurface.CC_SELECT:
                this.handleSelectEncoder (delta);
                break;

            case DelugeControlSurface.CC_TEMPO:
                this.handleTempoEncoder (delta);
                break;

            default:
                // Check for gold knob encoders
                for (int i = 0; i < GOLD_KNOB_CCS.length; i++)
                {
                    if (data1 == GOLD_KNOB_CCS[i])
                    {
                        this.handleGoldKnob (i, delta);
                        return;
                    }
                }
                break;
        }
    }


    /**
     * Handle horizontal encoder (SCROLL◄►) - scroll steps or change resolution.
     * Press + Turn = Change resolution (zoom).
     * Turn only = Scroll steps.
     *
     * @param delta The relative change
     */
    private void handleHorizontalEncoder (final int delta)
    {
        // Press + Turn = Change resolution (zoom)
        if (this.xEncoderPressed)
        {
            final IView view = this.getSurface ().getViewManager ().getActive ();
            if (view instanceof DelugeClipView)
            {
                final DelugeClipView clipView = (DelugeClipView) view;
                final int currentRes = clipView.getResolutionIndex ();
                final int newRes = delta > 0 ? Math.min (7, currentRes + 1) : Math.max (0, currentRes - 1);
                clipView.setResolutionIndex (newRes);
            }
            return;
        }

        // Normal scroll
        if (delta > 0)
            this.model.getCursorClip ().scrollStepsPageForward ();
        else if (delta < 0)
            this.model.getCursorClip ().scrollStepsPageBackwards ();

        // Display current page
        final int page = this.model.getCursorClip ().getEditPage () + 1;
        this.getSurface ().send7SegText ("P" + page);
    }


    /**
     * Handle vertical encoder (SCROLL▼▲) - scroll notes/octave.
     *
     * @param delta The relative change
     */
    private void handleVerticalEncoder (final int delta)
    {
        final IView view = this.getSurface ().getViewManager ().getActive ();
        if (view instanceof TransposeView)
        {
            final TransposeView transposeView = (TransposeView) view;
            if (delta > 0)
                transposeView.onOctaveUp (ButtonEvent.DOWN);
            else if (delta < 0)
                transposeView.onOctaveDown (ButtonEvent.DOWN);
        }
    }


    /**
     * Handle select encoder - context-sensitive value adjustment.
     *
     * @param delta The relative change
     */
    private void handleSelectEncoder (final int delta)
    {
        // In clip view, adjust velocity for the selected note or change resolution
        final DelugeClipView clipView = (DelugeClipView) this.getSurface ().getViewManager ().get (Views.SEQUENCER);
        if (clipView != null)
        {
            // Change resolution when no note is selected
            if (delta > 0)
                clipView.setResolutionIndex (clipView.getResolutionIndex () + 1);
            else if (delta < 0)
                clipView.setResolutionIndex (clipView.getResolutionIndex () - 1);
        }
    }


    /**
     * Handle tempo encoder.
     *
     * @param delta The relative change
     */
    private void handleTempoEncoder (final int delta)
    {
        final ITransport transport = this.model.getTransport ();
        transport.changeTempo (delta > 0, this.getSurface ().isShiftPressed ());

        // Display tempo
        final double tempo = transport.getTempo ();
        this.getSurface ().send7SegText (String.format ("%.0f", Double.valueOf (tempo)));
    }


    /**
     * Handle gold knob encoder.
     *
     * @param knobIndex The knob index (0-7)
     * @param delta The relative change
     */
    private void handleGoldKnob (final int knobIndex, final int delta)
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final DelugeControlSurface surface = this.getSurface ();

        final boolean upper = this.goldKnobUpperMode[knobIndex];
        String paramName = "";

        switch (knobIndex)
        {
            case 0: // Volume / Pan
                if (upper)
                {
                    cursorTrack.changeVolume (delta > 0 ? 1 : -1);
                    paramName = "VOL";
                }
                else
                {
                    cursorTrack.changePan (delta > 0 ? 1 : -1);
                    paramName = "PAN";
                }
                break;

            case 1: // Cutoff / Resonance - use device parameters
            case 2: // Attack / Release
            case 3: // Delay Time / Amount
            case 4: // Sidechain / Reverb
            case 5: // Mod Rate / Mod Depth
            case 6: // Stutter / Custom 1
            case 7: // Custom 2 / Custom 3
                // Map to device parameter bank
                final IParameterBank parameterBank = cursorDevice.getParameterBank ();
                final int paramIndex = upper ? knobIndex : knobIndex + 8;
                if (paramIndex < parameterBank.getPageSize ())
                {
                    parameterBank.getItem (knobIndex).changeValue (delta > 0 ? 1 : -1);
                    paramName = parameterBank.getItem (knobIndex).getName (4);
                }
                break;
        }

        surface.send7SegText (paramName);
    }


    /**
     * Update the sidebar LED colors based on current state.
     */
    public void updateSidebarLEDs ()
    {
        final DelugeControlSurface surface = this.getSurface ();
        final DelugeClipView clipView = (DelugeClipView) surface.getViewManager ().get (Views.SEQUENCER);
        if (clipView != null)
            clipView.updateSidebarLEDs ();
    }


    /**
     * Handle incoming SysEx messages from the Deluge.
     *
     * @param data The SysEx data (without F0/F7)
     */
    private void handleSysEx (final String data)
    {
        // Parse hex string to bytes
        final byte [] bytes = new byte [data.length () / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            final int index = i * 2;
            bytes[i] = (byte) Integer.parseUnsignedInt (data.substring (index, index + 2), 16);
        }

        // Check for F0 start byte and Deluge manufacturer ID and controller mode
        // Bitwig includes F0 in the data, so:
        // bytes[0] = F0, bytes[1-3] = manufacturer, bytes[4] = controller mode, bytes[5] = command
        if (bytes.length < 7)
            return;
        if (bytes[0] != (byte) 0xF0 ||
            bytes[1] != (byte) DelugeControlSurface.DELUGE_SYSEX_ID[0] ||
            bytes[2] != (byte) DelugeControlSurface.DELUGE_SYSEX_ID[1] ||
            bytes[3] != (byte) DelugeControlSurface.DELUGE_SYSEX_ID[2] ||
            bytes[4] != (byte) DelugeControlSurface.CONTROLLER_MODE_ID)
            return;

        final int command = bytes[5] & 0xFF;
        switch (command)
        {
            case DelugeControlSurface.CMD_SIDEBAR_PAD_EVENT:
                this.handleSidebarPadEvent (bytes);
                break;

            case DelugeControlSurface.CMD_BUTTON_EVENT:
                this.handleButtonEvent (bytes);
                break;

            default:
                // Unknown command
                break;
        }
    }


    /**
     * Handle sidebar pad events.
     *
     * @param bytes The SysEx data
     */
    private void handleSidebarPadEvent (final byte [] bytes)
    {
        if (bytes.length < 9)
            return;

        // bytes[0]=F0, [1-3]=manufacturer, [4]=mode, [5]=cmd, [6]=sidebarCol, [7]=row, [8]=state
        final int sidebarCol = bytes[6] & 0xFF; // 0 = mute, 1 = audition
        final int row = bytes[7] & 0xFF;
        final int state = bytes[8] & 0xFF;
        final int velocity = state > 0 ? 127 : 0;

        final DelugeClipView clipView = (DelugeClipView) this.getSurface ().getViewManager ().get (Views.SEQUENCER);
        if (clipView == null)
            return;

        if (sidebarCol == 0)
            clipView.onMutePad (row, velocity);
        else
            clipView.onAuditionPad (row, velocity);
    }


    /**
     * Handle button events.
     *
     * @param bytes The SysEx data
     */
    private void handleButtonEvent (final byte [] bytes)
    {
        if (bytes.length < 8)
            return;

        // bytes[0]=F0, [1-3]=manufacturer, [4]=mode, [5]=cmd, [6]=buttonId, [7]=state
        final int buttonId = bytes[6] & 0xFF;
        final int state = bytes[7] & 0xFF;
        final boolean pressed = state > 0;


        final ITransport transport = this.model.getTransport ();
        final DelugeControlSurface surface = this.getSurface ();

        switch (buttonId)
        {
            case DelugeControlSurface.BUTTON_PLAY:
                if (pressed)
                {
                    if (transport.isPlaying ())
                        transport.stop ();
                    else
                        transport.play ();
                }
                // Update button LED
                surface.setButtonLED (DelugeControlSurface.BUTTON_PLAY, transport.isPlaying ());
                break;

            case DelugeControlSurface.BUTTON_RECORD:
                if (pressed)
                    transport.startRecording ();
                surface.setButtonLED (DelugeControlSurface.BUTTON_RECORD, transport.isRecording ());
                break;

            case DelugeControlSurface.BUTTON_TAP_TEMPO:
                if (pressed)
                {
                    if (this.shiftPressed)
                    {
                        // SHIFT + TAP_TEMPO = toggle metronome
                        transport.toggleMetronome ();
                        surface.setButtonLED (DelugeControlSurface.BUTTON_TAP_TEMPO, transport.isMetronomeOn ());
                    }
                    else
                    {
                        // TAP_TEMPO alone = tap tempo
                        transport.tapTempo ();
                    }
                }
                break;

            case DelugeControlSurface.BUTTON_SYNC_SCALING:
                // Sync scaling button - could be used for other functions
                break;

            case DelugeControlSurface.BUTTON_BACK:
                if (pressed)
                    this.model.getApplication ().undo ();
                break;

            case DelugeControlSurface.BUTTON_SHIFT:
                // Track shift button state for combos
                this.shiftPressed = pressed;
                break;

            // Gold knob buttons toggle upper/lower mode
            case 24: case 25: case 26: case 27: case 28: case 29: case 30: case 31:
                if (pressed)
                {
                    final int knobIndex = buttonId - 24;
                    this.goldKnobUpperMode[knobIndex] = !this.goldKnobUpperMode[knobIndex];
                    surface.send7SegText (this.goldKnobUpperMode[knobIndex] ? "UP" : "LO");
                }
                break;

            case DelugeControlSurface.BUTTON_X_ENC:
                // Track X encoder button for Press + Turn (resolution change)
                this.xEncoderPressed = pressed;
                break;

            default:
                // Other buttons not yet implemented
                break;
        }
    }
}
