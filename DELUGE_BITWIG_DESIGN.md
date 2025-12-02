# Deluge Control Script for Bitwig - High-Level Design Document

## 1. Executive Summary

This document outlines the design for a Bitwig Studio control script that implements the Synthstrom Deluge workflow. The goal is to provide Deluge users with a familiar interface when working in Bitwig, translating Deluge's hardware-centric concepts into Bitwig's DAW environment and vice versa.

---

## 2. Deluge Overview

The Synthstrom Deluge is an all-in-one portable music production device featuring:
- **16x8 RGB pad grid** for sequencing, performance, and navigation
- **Gold encoders** for parameter control
- **Hybrid workflow** combining session-based clip launching with linear arrangement
- **Integrated synthesis, sampling, and effects**
- **Advanced sequencing** with probability, conditional triggers, and polyphonic step sequencing

---

## 3. Core Deluge Concepts and Bitwig Mapping

### 3.1 Operational Modes

#### **Song View (Deluge) → Session View (Bitwig)**
- **Deluge**: Grid-based clip launcher where each pad represents a clip/track section
- **Bitwig Mapping**:
  - Map to Bitwig's Clip Launcher panel
  - Each pad = clip slot in the session view
  - Horizontal = scenes, Vertical = tracks
  - Support clip launching, recording, and stopping

#### **Arranger View (Deluge) → Arrange View (Bitwig)**
- **Deluge**: Linear timeline arrangement with zoom and navigation
- **Bitwig Mapping**:
  - Switch to Bitwig's Arrange panel
  - Grid represents timeline sections
  - Pads can represent bars/measures for quick navigation
  - Support punch-in recording and playback control

#### **Clip Types - Mapping Strategy**

| Deluge Clip Type | Bitwig Equivalent | Notes |
|------------------|-------------------|-------|
| Synth Clip | Instrument Clip with Polysynth | Map Deluge synth parameters to Bitwig device |
| Kit Clip | Drum Machine Clip | 16 pad drum layout, velocity layers |
| MIDI Clip | Instrument Clip | External MIDI routing |
| Audio Clip | Audio Clip | Sample playback and manipulation |
| CV Clip | Modulation/Automation Track | For modular integration |

### 3.2 Grid Interface Paradigms

#### **Deluge Grid (16x8 pads)**
- **Rows 1-2 (Top)**: Typically used for mute/launch functions, scenes
- **Rows 3-7 (Middle)**: Main sequencer/keyboard area
- **Row 8 (Bottom)**: Audition pads, scales, or performance controls

#### **Bitwig Mapping Strategies**

**Mode 1: Session Mode**
```
Rows 1-7: Clip launcher (8 tracks × 7 scenes)
Row 8: Transport controls, scene launch, track selection
Columns 15-16: Navigation, mode switching
```

**Mode 2: Sequencer Mode**
```
Grid: Step sequencer view (16 steps × 8 notes/drums)
With Shift: Velocity/probability editing
With Select: Note repeat/probability
```

**Mode 3: Keyboard Mode**
```
Grid: Chromatic/scale-based note input
Support for Deluge's scale modes
Velocity sensitivity from pad pressure
```

**Mode 4: Drum Mode**
```
4×4 or 2×8 pad layout for drums
Multiple pages for 16+ drum sounds
Velocity layers and choke groups
```

### 3.3 Sequencing Concepts

#### **Per-Step Parameters**
| Deluge Feature | Bitwig Implementation |
|----------------|----------------------|
| Step velocity | Note expression automation |
| Step probability | Note probability (Bitwig native) |
| Step condition | Note condition expressions |
| Step iteration | Repeat/cycle automation |
| Euclidean sequencing | Custom note distribution |
| Multiple note lengths | Individual note lengths in clip |
| Polyphonic steps | Multiple notes per step |
| Microtiming | Note timing offset |

#### **Pattern Length and Resolution**
- Deluge supports flexible pattern lengths (1-384 steps)
- Bitwig mapping: Use clip length and resolution
- Support odd time signatures and polyrhythms

### 3.4 Track and Clip Management

#### **Deluge Track Paradigm**
- Each instrument/kit/audio = one "track"
- Color-coded per track
- Can have multiple clips per track (sections)

#### **Bitwig Mapping**
```
Deluge Track → Bitwig Track
Deluge Clip/Section → Bitwig Clip Slot
Track colors preserved
Track types auto-detected (instrument vs audio)
```

### 3.5 Performance Features

#### **Clip/Scene Launching**
| Deluge Control | Bitwig Function | Implementation |
|----------------|-----------------|----------------|
| Pad press (Song View) | Launch clip | Immediate launch or quantized |
| Pad + Hold | Prepare clip | Cue launch |
| Pad double-tap | Stop clip | Stop individual clip |
| Horizontal + Press | Launch scene | Launch all clips in scene |
| Vertical + Press | Stop track | Stop all clips on track |

#### **Recording Modes**
| Deluge Mode | Bitwig Equivalent |
|-------------|-------------------|
| Record | Overdub record |
| Record + Tap Tempo | Count-in record |
| Resampling | Record to new audio track |
| Looping | Loop record with overdub |

#### **Performance Effects**
- **Stutter**: Map to Bitwig's Repeat/Stutter effects
- **Decimation**: Bit crusher or sample rate reduction
- **LPF/HPF**: Real-time filter control
- **Delay Feedback**: Live delay manipulation

---

## 4. Sound Design and Synthesis

### 4.1 Deluge Synthesis Engine → Bitwig Mapping

#### **Subtractive Synthesis**
```
Deluge Parameter          → Bitwig Equivalent
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
OSC1/OSC2 Type/Shape      → Polysynth oscillator type/waveform
OSC Pitch                 → Oscillator pitch
OSC Pulse Width           → PWM parameter
OSC Sync                  → Oscillator sync
Noise Level               → Noise oscillator mix

FILTER Type               → Filter type (LP/HP/BP/Notch)
FILTER Cutoff (Gold ◀▶)   → Filter frequency
FILTER Resonance          → Filter resonance
FILTER Morph              → Filter drive/saturation

ENV1 (Amp)                → Amplitude envelope
ENV2 (Mod)                → Modulation envelope
LFO1/LFO2                 → LFO 1/2

MODULATION MATRIX         → Bitwig modulation system
```

#### **Sample-Based Synthesis**
- Deluge sample playback → Bitwig Sampler
- Multi-sample support → Zone mapping
- Sample start/end points → Loop points
- Time-stretching modes → Bitwig stretch modes

### 4.2 Effects Chain Mapping

| Deluge Effect | Bitwig Effect | Notes |
|---------------|---------------|-------|
| HPF/LPF | EQ-5 or filters | Pre/post filter control |
| Delay | Delay | Sync, feedback, pingpong |
| Reverb | Reverb | Room size, dampening |
| Sidechain | Dynamics/Sidechain | Compressor with sidechain |
| Mod FX (Chorus/Flanger/Phaser) | Modulation devices | Type selection |
| Distortion/Saturation | Distortion | Drive, fold, shape |
| Bitcrusher | Bit-8 | Sample rate reduction |
| EQ | EQ-5 | 3-band minimum |
| Compressor | Dynamics | Threshold, ratio, attack/release |

### 4.3 Parameter Control Strategy

#### **Gold Encoders (8 encoders)**
- **Upper Gold Encoder**: Master parameter (filter cutoff, effect send, etc.)
- **Lower Gold Encoder**: Secondary parameter (resonance, feedback, etc.)
- **Select + Gold Encoders**: Page through different parameter sets

**Mapping Priority:**
1. **Most-Used Parameters**: Filter cutoff, resonance, envelope attack/decay
2. **Effect Parameters**: Delay time/feedback, reverb size/mix
3. **Track Parameters**: Volume, pan, sends
4. **Device Parameters**: Selected device parameters

#### **Shift Layers**
- **Shift + Encoder**: Fine control (smaller increments)
- **Select + Encoder**: Parameter selection
- **Shift + Select + Encoder**: MIDI CC mapping mode

---

## 5. Navigation and Mode System

### 5.1 Deluge Button Layout Mapping

#### **Main Navigation Buttons**
```
[SONG]     → Switch to Session View
[SYNTH]    → Create/Focus Instrument Track
[KIT]      → Create/Focus Drum Track
[MIDI]     → MIDI configuration mode
[CV]       → Modulation/Automation mode

[KEYBOARD] → Switch to keyboard input mode
[SCALE]    → Scale settings
[CROSS SCREEN] → Arranger View toggle
[TAP TEMPO] → Tap tempo + metronome

[▶︎ PLAY]  → Transport play
[RECORD]   → Arm record
[LEARN]    → MIDI learn mode
[SHIFT]    → Shift modifier
[SELECT]   → Select modifier
```

#### **Encoder Buttons**
```
[◀︎ ▶︎]     → Track selection, parameter page navigation
[∧ ∨]      → Scene navigation, zoom in arranger
```

### 5.2 Mode State Machine

```
DelugeControlScript Modes:
├── Session Mode (Default)
│   ├── Clip Launch
│   ├── Scene Launch
│   └── Track Control
├── Arranger Mode
│   ├── Timeline Navigation
│   ├── Punch Record
│   └── Region Control
├── Sequencer Mode
│   ├── Step Edit
│   ├── Note Edit
│   ├── Velocity Edit
│   └── Probability Edit
├── Keyboard Mode
│   ├── Chromatic
│   ├── In-Scale
│   └── Chord Mode
├── Drum Mode
│   ├── 16-Pad Layout
│   └── Velocity Layers
├── Mixer Mode
│   ├── Volume Control
│   ├── Pan Control
│   └── Send Control
└── Device Mode
    ├── Parameter Control
    └── Preset Browser
```

### 5.3 Visual Feedback

#### **Pad Colors**
```
Deluge Convention → Bitwig Implementation
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Track colors      → Preserve Bitwig track colors
Clip status:
  - Dim           → Empty slot
  - Bright color  → Clip stopped
  - Pulsing       → Clip playing
  - Flashing      → Clip recording
  - Red flash     → Clip queued to stop

Note status:
  - White         → Active step
  - Amber/Yellow  → Probable step (< 100%)
  - Off           → Inactive step
  - Bright white  → Current playback position
```

#### **Screen Integration**
- Deluge OLED display → Could show Bitwig track names, parameters
- When controller mode is available, sync display data
- Show current mode, tempo, time signature

---

## 6. Advanced Features

### 6.1 Probability and Conditional Sequencing

**Deluge Feature**: Each note can have:
- Probability percentage (0-100%)
- Iteration count (play every N iterations)
- Fill mode (play only on fills)

**Bitwig Implementation**:
```java
// Use Bitwig's note expression for probability
clip.setStepProbability(step, probability);
clip.setStepCondition(step, condition);

// Custom implementation for iteration counting
// Store in note metadata or use custom device
```

### 6.2 Sampling Workflow

| Deluge Sampling Feature | Bitwig Mapping |
|-------------------------|----------------|
| Record sample from input | Record audio to new audio track/clip |
| Resample internal audio | Record from Bitwig's output |
| Slice to kit | Convert audio to sliced drum rack |
| Time-stretch | Bitwig's time-stretch modes |
| Waveform editing | Audio event editing |

### 6.3 Automation

**Deluge**: Per-clip parameter automation with automation lanes
**Bitwig**: Clip automation + track automation

**Mapping Strategy**:
- Gold encoder movements → Clip automation
- Touch + turn → Record automation
- Automation lanes → Visual representation in Bitwig

### 6.4 Mod Matrix

**Deluge Modulation System**:
- Sources: LFO1, LFO2, ENV1, ENV2, Velocity, Aftertouch, MPE
- Destinations: Any synthesis parameter
- Depth control

**Bitwig Implementation**:
- Map to Bitwig's native modulation system
- Use Bitwig modulators (LFO, ADSR, etc.)
- Create modulation routing through script
- Support MPE dimensions

---

## 7. Bitwig-Specific Enhancements

### 7.1 Leveraging Bitwig's Unique Features

#### **Clip Launcher Scenes**
- Deluge doesn't have explicit "scenes" in Song View
- Use Bitwig scenes for larger arrangement sections
- Map horizontal encoder navigation to scene selection

#### **Note FX and Device FX**
- Deluge effects are per-track
- Bitwig allows note-level and device chains
- Expose note FX in keyboard/sequencer modes

#### **Nested Device Chains**
- More complex than Deluge's single-chain approach
- Device navigation through encoders
- Shift + Select to enter/exit chains

#### **Browser Integration**
- Quick device/preset loading via LEARN button
- Sample browser for audio clips
- Filter browser by categories

### 7.2 Clip and Scene Management

**Enhanced Operations**:
```
SHIFT + Pad (Session)    → Duplicate clip
SELECT + Pad (Session)   → Delete clip
SHIFT + SELECT + Pad     → Clip properties (length, loop)
Encoder ◀︎ + ▶︎ + Pad     → Move clip between tracks
```

---

## 8. Implementation Architecture

### 8.1 Package Structure

```
de.mossgrabers.controller.synthstrom.deluge/
├── DelugeControllerDefinition.java      # Controller registration
├── DelugeControllerExtension.java       # Main extension class
├── DelugeConfiguration.java             # User settings
├── DelugeControlSurface.java            # Hardware abstraction
├── controller/
│   ├── DelugeControlSurfaceFactory.java
│   ├── DelugeColors.java                # RGB color definitions
│   ├── DelugePadGrid.java               # Grid abstraction
│   ├── DelugeEncoders.java              # Encoder handling
│   └── DelugeButtons.java               # Button handling
├── mode/
│   ├── SessionMode.java                 # Clip launcher mode
│   ├── ArrangerMode.java                # Arranger view mode
│   ├── SequencerMode.java               # Step sequencer mode
│   ├── KeyboardMode.java                # Note input mode
│   ├── DrumMode.java                    # Drum programming
│   ├── MixerMode.java                   # Mixer controls
│   ├── DeviceMode.java                  # Device parameters
│   └── AbstractDelugeMode.java          # Base mode class
├── view/
│   ├── ClipView.java                    # Main clip view
│   ├── SessionView.java                 # Session launcher
│   ├── SequencerView.java               # Step sequencer
│   ├── KeyboardView.java                # Keyboard input
│   ├── DrumView.java                    # Drum pads
│   └── AbstractDelugeView.java          # Base view class
└── command/
    ├── trigger/
    │   ├── SongButtonCommand.java
    │   ├── SynthButtonCommand.java
    │   ├── KitButtonCommand.java
    │   ├── CrossScreenCommand.java
    │   └── ...
    └── continuous/
        ├── GoldEncoderCommand.java
        └── KnobValueCommand.java
```

### 8.2 Mode Management System

```java
public class DelugeControllerExtension {
    private ModeManager modeManager;
    private ViewManager viewManager;

    // Primary modes
    private SessionMode sessionMode;
    private ArrangerMode arrangerMode;
    private SequencerMode sequencerMode;
    private KeyboardMode keyboardMode;
    private DrumMode drumMode;
    private MixerMode mixerMode;
    private DeviceMode deviceMode;

    // Mode stack for shift/select overlays
    private Stack<IMode> modeStack;
}
```

### 8.3 Grid Abstraction

```java
public class DelugePadGrid {
    private static final int COLUMNS = 16;
    private static final int ROWS = 8;

    // Pad state management
    public void setPadColor(int x, int y, ColorEx color);
    public void setPadPulsing(int x, int y, boolean pulse);
    public void setPadFlashing(int x, int y, boolean flash);

    // Grid regions (for different mode layouts)
    public PadRegion getMainGrid();      // Full 16×8
    public PadRegion getSequencerGrid(); // 16×8 steps
    public PadRegion getSessionGrid();   // 8×7 (reserving row for controls)
    public PadRegion getKeyboardGrid();  // Custom scale layouts
}
```

### 8.4 Configuration Options

```java
public class DelugeConfiguration extends AbstractConfiguration {
    // Session View Settings
    - Launch quantization (None, Bar, Beat)
    - Clip stop mode (Immediate, Quantized)
    - Scene launch mode (Queued, Immediate)

    // Sequencer Settings
    - Default note length
    - Velocity curve
    - Probability display mode

    // Keyboard Settings
    - Default scale
    - Root note
    - Chromatic vs in-scale

    // Display Settings
    - Pad brightness
    - Color scheme (Match Deluge vs Bitwig colors)
    - Feedback mode (OLED integration when available)

    // Transport Settings
    - Tap tempo averaging
    - Metronome routing
    - Pre-count bars

    // Hardware Settings
    - Firmware version detection
    - Controller mode support (future)
    - MIDI channel configuration
}
```

---

## 9. Technical Considerations

### 9.1 MIDI Communication

**Current Phase**: Standard MIDI mode
- Note input/output
- CC messages for encoders
- Program changes for preset selection

**Future Phase**: Deluge Controller Mode (pending implementation)
- Bi-directional communication
- OLED display control
- Advanced pad feedback
- SysEx configuration

### 9.2 Performance Optimization

- **Efficient pad updates**: Batch updates, only send changes
- **Lazy loading**: Don't query all tracks/clips constantly
- **Caching**: Cache clip colors, states for quick updates
- **Threading**: Non-blocking UI updates

### 9.3 Bitwig API Usage

Key API components:
```java
- CursorTrack          // Track navigation and selection
- ClipLauncherSlotBank // Session view management
- DrumPadBank          // Drum device integration
- CursorDevice         // Device parameter control
- Transport            // Playback and recording control
- Application          // View switching and panel control
- SceneBank            // Scene management
```

---

## 10. Development Phases

### Phase 1: Foundation (Core Functionality)
- [ ] Project structure and build setup
- [ ] Basic MIDI communication
- [ ] Pad grid abstraction
- [ ] Session mode with clip launching
- [ ] Transport controls
- [ ] Track selection and navigation
- [ ] Basic encoder mapping

### Phase 2: Sequencing and Input
- [ ] Step sequencer mode
- [ ] Keyboard mode with scale support
- [ ] Drum mode (16-pad layout)
- [ ] Note probability implementation
- [ ] Velocity editing
- [ ] Note length editing

### Phase 3: Sound Design
- [ ] Device mode for parameter control
- [ ] Effect mapping
- [ ] Synthesis parameter control
- [ ] Preset browser integration
- [ ] Modulation routing

### Phase 4: Performance Features
- [ ] Scene launching
- [ ] Performance effects (stutter, etc.)
- [ ] Arranger mode implementation
- [ ] Recording modes (overdub, punch-in)
- [ ] Clip duplication and management

### Phase 5: Advanced Features
- [ ] Conditional sequencing
- [ ] Automation recording/editing
- [ ] Sampling workflow integration
- [ ] Mixer mode (volume, pan, sends)
- [ ] User customization settings

### Phase 6: Controller Mode Integration (Future)
- [ ] OLED display support
- [ ] Enhanced feedback
- [ ] SysEx communication
- [ ] Advanced state synchronization

---

## 11. User Experience Goals

### 11.1 Familiarity
- Deluge users should feel at home immediately
- Button and pad behaviors match Deluge expectations
- Visual feedback mirrors Deluge conventions

### 11.2 Efficiency
- Minimal mode switching required
- Most common operations accessible quickly
- Smart defaults based on context

### 11.3 Flexibility
- Support both Deluge-style and Bitwig-native workflows
- Configurable to match user preferences
- Extensible for power users

### 11.4 Discoverability
- Clear visual feedback for available functions
- Mode indicators
- Helpful defaults for new users

---

## 12. Testing Strategy

### 12.1 Unit Tests
- Pad grid calculations
- Color conversions
- Mode state transitions
- Button command routing

### 12.2 Integration Tests
- Bitwig API interaction
- MIDI message handling
- Multi-track operations
- Session view synchronization

### 12.3 User Acceptance Testing
- Deluge user workflow scenarios
- Performance (latency, responsiveness)
- Visual feedback accuracy
- Edge cases (empty projects, complex routing)

---

## 13. Documentation Requirements

### 13.1 User Manual
- Quick start guide
- Mode reference
- Button/encoder mapping charts
- Workflow examples
- Troubleshooting

### 13.2 Developer Documentation
- Architecture overview
- API reference
- Extension points
- Contributing guidelines

---

## 14. Open Questions and Future Considerations

### 14.1 Questions for Resolution
1. **Controller Mode Protocol**: Awaiting official Deluge controller mode details
2. **Display Integration**: How much control over OLED display?
3. **Firmware Compatibility**: Which Deluge firmware versions to support?
4. **Advanced Grid Features**: Support for Deluge's RGB pad animations?

### 14.2 Potential Enhancements
- MPE support (Deluge has aftertouch/pressure)
- Grid controller mode (use as generic controller)
- Template/project presets
- MIDI learn mode for custom mappings
- Integration with Bitwig's Grid device
- CV output mapping (if supported)

### 14.3 Community Feedback Integration
- User-requested features
- Workflow improvements
- Bug reports and edge cases
- Platform-specific considerations

---

## 15. Success Metrics

### 15.1 Functional Completeness
- ✓ All major Deluge workflows supported
- ✓ Core operations work intuitively
- ✓ Feature parity with existing DrivenByMoss controllers

### 15.2 Performance
- < 10ms latency for pad/button presses
- Smooth visual feedback (no flickering)
- Stable during intensive use

### 15.3 User Adoption
- Positive community feedback
- Active usage and contributions
- Integration into Deluge user workflows

---

## 16. Conclusion

This design document provides a comprehensive roadmap for implementing a Deluge control script for Bitwig Studio. The approach respects the Deluge's unique workflow while leveraging Bitwig's powerful features, creating a bridge between hardware sequencer and modern DAW.

The implementation will follow DrivenByMoss framework conventions, ensuring consistency with other control scripts while adapting to the Deluge's specific paradigms.

**Next Steps:**
1. Review and validate this design with stakeholders
2. Await Deluge controller mode specifications
3. Begin Phase 1 implementation
4. Iterate based on testing and feedback

---

**Document Version**: 1.0
**Date**: December 1, 2025
**Author**: Claude (AI Assistant)
**Status**: Draft for Review
