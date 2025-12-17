// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.synthstrom.deluge.view;

import de.mossgrabers.controller.synthstrom.deluge.DelugeConfiguration;
import de.mossgrabers.controller.synthstrom.deluge.controller.DelugeColorManager;
import de.mossgrabers.controller.synthstrom.deluge.controller.DelugeControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.sequencer.AbstractNoteSequencerView;

import java.util.List;


/**
 * The Clip View (sequencer) for the Deluge.
 * Matches the Deluge's native clip view workflow:
 * - Rows = notes (pitches for synth, drum pads for kit)
 * - Columns = steps
 * - Tap to toggle note on/off
 * - Hold start pad + press end pad to set note length
 * - MUTE sidebar mutes rows
 * - AUDITION sidebar plays notes
 *
 * @author Jürgen Moßgraber
 */
public class DelugeClipView extends AbstractNoteSequencerView<DelugeControlSurface, DelugeConfiguration> implements IScrollableView
{
    // Track mute state for each row (8 rows)
    private final boolean [] rowMuted = new boolean [8];

    // For tracking long press + release to create note lengths
    private NotePosition noteEditPosition;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DelugeClipView (final DelugeControlSurface surface, final IModel model)
    {
        // 16 columns, 8 sequencer rows (full grid)
        super ("Clip", surface, model, 16, 8, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        // Set default octave to middle range (2) to ensure notes are visible
        // (default 0 is often too low for typical clips)
        if (this.scales.getOctave () == 0)
            this.scales.setOctave (2);

        this.updateSidebarLEDs ();
        this.updateClipDisplay ();
    }


    /**
     * Update the 7-segment display with current clip info.
     */
    private void updateClipDisplay ()
    {
        final INoteClip clip = this.getClip ();
        if (clip.doesExist ())
        {
            // Show "CLIP" when a clip exists
            this.surface.send7SegText ("CLIP");
        }
        else
        {
            this.surface.send7SegText ("----");
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (!this.isActive ())
            return DelugeColorManager.DELUGE_COLOR_BLACK;

        // Scene buttons (sidebar) for resolution selection
        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return DelugeColorManager.DELUGE_COLOR_BLACK;

        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        // Bottom scene button is selected resolution indicator
        return scene == 7 - this.getResolutionIndex ()
            ? DelugeColorManager.DELUGE_COLOR_YELLOW
            : DelugeColorManager.DELUGE_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
    {
        // Toggle note on button release, allowing for long press detection
        if (velocity != 0)
        {
            // Button pressed - store for potential long press
            this.noteEditPosition = null;
            return;
        }

        // Button released
        if (this.noteEditPosition != null)
        {
            // A long press was detected, edit the note
            this.editNote (this.getClip (), this.noteEditPosition, false);
        }
        else
        {
            // Regular tap - toggle the note
            super.handleSequencerArea (index, x, y, velocity);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity)
    {
        // Handle Deluge-style note length creation:
        // Hold start pad + press end pad to set note length
        final int offset = row * clip.getNumSteps ();
        final NotePosition np = new NotePosition (notePosition);
        final int currentStep = np.getStep ();
        final int note = np.getNote ();

        for (int s = 0; s < currentStep; s++)
        {
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, offset + s));
            if (button != null && button.isLongPressed ())
            {
                np.setStep (s);
                button.setConsumed ();

                // Calculate note length from start to current position
                final int length = currentStep - s + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());

                final StepState state = note < 0 ? StepState.OFF : clip.getStep (np).getState ();
                if (state == StepState.START)
                {
                    // Existing note - update its duration
                    clip.updateStepDuration (np, duration);
                }
                else
                {
                    // New note - create with the specified length
                    clip.setStep (np, velocity, duration);
                }
                return true;
            }
        }

        // Handle mute toggle (if MUTE button is held)
        if (this.isButtonCombination (ButtonID.MUTE))
        {
            final IStepInfo stepInfo = clip.getStep (notePosition);
            if (stepInfo.getState () == StepState.START)
                clip.updateStepMuteState (notePosition, !stepInfo.isMuted ());
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - this.surface.getPadGrid ().getStartNote ();
        final int y = index / 16;
        if (y >= this.numSequencerRows)
            return;

        // Store position for possible note length extension or velocity editing
        final int x = index % 16;
        this.noteEditPosition = new NotePosition (
            this.configuration.getMidiEditChannel (),
            x,
            this.keyManager.map (y)
        );
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid gridPad = this.surface.getPadGrid ();
        if (!this.isActive ())
        {
            gridPad.turnOff ();
            return;
        }

        final INoteClip clip = this.getClip ();
        if (!clip.doesExist ())
        {
            gridPad.turnOff ();
            return;
        }

        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.clipCols : -1;
        final List<NotePosition> editNotes = this.getEditNotes ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, 0);
        final int numSteps = Math.min (this.clipCols, clip.getNumSteps ());

        // Draw sequencer rows (bottom 7 rows)
        for (int x = 0; x < numSteps; x++)
        {
            notePosition.setStep (x);
            for (int y = 0; y < this.numSequencerRows; y++)
            {
                final int map = this.keyManager.map (y);
                notePosition.setNote (map);
                // Ensure note is within valid MIDI range (0-127) and step is valid
                final IStepInfo stepInfo = (map < 0 || map > 127) ? null : clip.getStep (notePosition);
                // Draw y=0 at Row 7 (Bottom), y=7 at Row 0 (Top) - Row 0 is Top in PadGrid
                gridPad.lightEx (x, 7 - y, this.getStepColor (stepInfo, x == hiStep, notePosition.getChannel (), x, y, map, editNotes));
            }
        }

        // Fill remaining columns (if clip is shorter than 16 steps)
        for (int x = numSteps; x < this.clipCols; x++)
        {
            for (int y = 0; y < this.numSequencerRows; y++)
                gridPad.lightEx (x, 7 - y, DelugeColorManager.DELUGE_COLOR_BLACK);
        }

        // Loop indicator removed as it conflicts with note grid
        /*
        // Draw loop/page indicator row (top row)
        final int lengthOfOnePad = this.getLengthOfOnePage (this.clipCols);
        final double loopStart = clip.getLoopStart ();
        final int loopStartPad = (int) Math.ceil (loopStart / lengthOfOnePad);
        final int loopEndPad = (int) Math.ceil ((loopStart + clip.getLoopLength ()) / lengthOfOnePad);
        final int currentPage = step / this.clipCols;
        for (int pad = 0; pad < this.clipCols; pad++)
            gridPad.lightEx (pad, 0, this.getPageColor (loopStartPad, loopEndPad, currentPage, clip.getEditPage (), pad));
        */

        // Update sidebar LEDs after grid update
        this.updateSidebarLEDs ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final INoteClip clip = this.getClip ();
        final int seqOctave = this.scales.getOctave ();

        scrollStates.setCanScrollLeft (clip.canScrollStepsBackwards ());
        scrollStates.setCanScrollRight (clip.canScrollStepsForwards ());
        scrollStates.setCanScrollUp (seqOctave < Scales.OCTAVE_RANGE);
        scrollStates.setCanScrollDown (seqOctave > -Scales.OCTAVE_RANGE);
    }


    /**
     * Set the resolution index.
     *
     * @param index The resolution index (0-7)
     */
    public void setResolutionIndex (final int index)
    {
        final int clampedIndex = Math.max (0, Math.min (7, index));
        this.getClip ().setStepLength (Resolution.getValueAt (clampedIndex));
        this.surface.getDisplay ().notify (Resolution.getNameAt (clampedIndex));
    }


    /**
     * Handle mute sidebar pad press.
     *
     * @param row The row (0-7)
     * @param velocity The velocity
     */
    public void onMutePad (final int row, final int velocity)
    {
        if (velocity == 0)
            return;

        // Toggle mute for this row
        this.rowMuted[row] = !this.rowMuted[row];

        // Update LED
        this.updateMutePadLED (row);

        // Display feedback
        this.surface.getDisplay ().notify ("Row " + (row + 1) + (this.rowMuted[row] ? " Muted" : " Unmuted"));
    }


    /**
     * Handle audition sidebar pad press.
     *
     * @param row The row (0-7)
     * @param velocity The velocity
     */
    public void onAuditionPad (final int row, final int velocity)
    {
        // Map row to note and play it
        final int note = this.keyManager.map (row);
        if (note >= 0)
        {
            if (velocity > 0)
            {
                // Note on
                this.surface.getMidiOutput ().sendNoteEx (
                    this.configuration.getMidiEditChannel (),
                    note,
                    this.configuration.isAccentActive ()
                        ? this.configuration.getFixedAccentValue ()
                        : velocity
                );

                // Light up audition pad while playing
                this.surface.setSidebarPadColor (1, row, 0, 127, 0); // Green while playing
            }
            else
            {
                // Note off
                this.surface.getMidiOutput ().sendNoteEx (
                    this.configuration.getMidiEditChannel (),
                    note,
                    0
                );

                // Restore audition pad color
                this.updateAuditionPadLED (row);
            }
        }
    }


    /**
     * Update all sidebar LEDs.
     */
    public void updateSidebarLEDs ()
    {
        for (int row = 0; row < 8; row++)
        {
            this.updateMutePadLED (row);
            this.updateAuditionPadLED (row);
        }
    }


    /**
     * Update the mute pad LED for a row.
     *
     * @param row The row (0-7)
     */
    private void updateMutePadLED (final int row)
    {
        // Muted = Yellow, Unmuted = Green
        if (this.rowMuted[row])
            this.surface.setSidebarPadColor (0, row, 127, 100, 0); // Yellow
        else
            this.surface.setSidebarPadColor (0, row, 0, 127, 0); // Green
    }


    /**
     * Update the audition pad LED for a row.
     *
     * @param row The row (0-7)
     */
    private void updateAuditionPadLED (final int row)
    {
        // Highlight root note
        final int note = this.keyManager.map (row);
        final int rootNote = this.scales.getScaleOffset ();

        if (note >= 0 && note % 12 == rootNote)
        {
            // Root note - Cyan
            this.surface.setSidebarPadColor (1, row, 0, 127, 127);
        }
        else if (note >= 0 && note % 12 == 0)
        {
            // Octave note (C) - Blue
            this.surface.setSidebarPadColor (1, row, 0, 0, 127);
        }
        else
        {
            // Regular note - Dim white
            this.surface.setSidebarPadColor (1, row, 40, 40, 40);
        }
    }



    /** {@inheritDoc} */
    @Override
    protected String getStepColor (final IStepInfo stepInfo, final boolean isHighlighted, final int channel, final int x, final int y, final int note, final List<NotePosition> editNotes)
    {
        if (isHighlighted)
            return COLOR_STEP_SELECTED;

        return super.getStepColor (stepInfo, isHighlighted, channel, x, y, note, editNotes);
    }
}
