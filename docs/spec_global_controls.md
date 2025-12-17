# Global Controls

Buttons, encoders, and display feedback shared across all views.

---

## Transport Buttons

| Button ID | Deluge Label | Bitwig Function |
|-----------|--------------|------------------|
| 0 | PLAY | Play / Stop |
| 1 | RECORD | Toggle record |
| 2 | TAP_TEMPO | Tap tempo (Shift+Tap: Toggle Metronome, LED=Metronome) |
| 3 | SYNC_SCALING | (Unassigned) |

---

## Navigation Buttons

| Button ID | Deluge Label | Bitwig Function |
|-----------|--------------|------------------|
| 7 | BACK | Undo |
| 8 | LOAD | Browser open |
| 9 | SAVE | Save project |
| 18 | SHIFT | Modifier (held) |

---

## View Buttons

| Button ID | Deluge Label | Bitwig Function |
|-----------|--------------|------------------|
| 15 | CLIP_VIEW | Enter Clip Editor |
| 16 | SESSION_VIEW | Clip Launcher (Song View) |
| 17 | AFFECT_ENTIRE | Arranger View |
| 10 | KEYBOARD | Toggle Keyboard mode |

---

## Instrument Buttons

| Button ID | Deluge Label | Bitwig Function |
|-----------|--------------|------------------|
| 11 | KIT | Create/Select Drum track |
| 12 | SYNTH | Create/Select Instrument track |
| 13 | MIDI | Create/Select MIDI track |
| 14 | CV | (Hardware CV output) |

---

## Navigation Encoders

| Encoder | CC | Deluge Label | Bitwig Function |
|---------|----|----|------------------|
| Horizontal | 79 | SCROLL◄► | Scene/Time navigation |
| Vertical | 80 | SCROLL▼▲ | Track navigation |
| Select | 81 | SELECT | Value adjustment / Browser |
| Tempo | 73 | TEMPO | Project tempo |

---

## Gold Knob Encoders (Parameter Affect Group)

| Encoder | CC | Upper Function | Lower Function |
|---------|-----|-----------------|-----------------|
| 1 | 71 | Track Volume | Track Pan |
| 2 | 72 | Filter Cutoff | Filter Resonance |
| 3 | 73 | Attack | Release |
| 4 | 74 | Delay Time | Delay Amount |
| 5 | 75 | Sidechain | Reverb |
| 6 | 76 | Mod Rate | Mod Depth |
| 7 | 77 | Stutter | Custom 1 |
| 8 | 78 | Custom 2 | Custom 3 |

### Context Sensitivity
- **Song View + Hold Pad**: Affect selected track
- **Clip View**: Affect current track's instrument
- **AFFECT_ENTIRE**: Affect all selected tracks

---

## Encoder Buttons (Push)

| Button ID | Deluge Label | Bitwig Function |
|-----------|--------------|------------------|
| 19 | SELECT_ENC | Enter browser / Confirm |
| 21 | X_ENC | Reset horizontal / Zoom (Press+Turn) |
| 22 | Y_ENC | Reset vertical |
| 23 | TEMPO_ENC | Reset tempo |
| 24-31 | GOLD_0-7 | Toggle upper/lower mode |
| 32-39 | EFFECT_0-7 | Select parameter bank |

---

## 7-Segment Display Feedback

| Event | Display |
|-------|---------|
| Pad press | Note/Clip name |
| Encoder turn | Parameter value |
| Tempo change | BPM value |
| View change | View abbreviation |

---

## Reference
- [controller_mode_midi_protocol.md](../controller_mode_midi_protocol.md) for button/encoder IDs
- [test_controller_mode.py](../test_controller_mode.py) for implementation examples

## Verified Implementation Details (Dec 2025)

### SysEx Protocol
The Deluge firmware sends controller mode events via SysEx. Bitwig's API provides the full SysEx message including the F0 start byte.

**Format:**
`F0 00 21 7B 01 [CMD] [DATA...] F7`

- **Manufacturer ID:** `00 21 7B` (Synthstrom)
- **Controller Mode ID:** `01`
- **Commands:**
  - `0x30` (48): Sidebar Pad Event (`[SIDEBAR_COL] [ROW] [STATE]`)
  - `0x40` (64): Button Event (`[BUTTON_ID] [STATE]`)

### Button Combinations
- **SHIFT + TAP_TEMPO**: Toggles Bitwig Metronome.
- **Press + Turn X Encoder**: Changes Grid Resolution (Zoom).

### Known Issues / Notes
- `SYNC_SCALING` button sends event but function is mapped to `SHIFT + TAP_TEMPO` for better ergonomics.
- Display updates show `B[ID]` for button presses during debug.
