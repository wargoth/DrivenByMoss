# Deluge Bitwig Controller Installation & Usage

## Installation

1. **Build the Extension**:
   ```bash
   cd /home/wargoth/sources/DrivenByMoss
   mvn package
   ```

2. **Install**:
   Unzip the build artifact to your Bitwig Extensions folder:
   ```bash
   unzip -o /home/wargoth/sources/DrivenByMoss/target/DrivenByMoss-*-Bitwig.zip -d ~/Bitwig\ Studio/Extensions/
   ```

## Configuration in Bitwig

1. **Restart Bitwig Studio** to load the new extension.
2. Open **Settings** > **Controllers**.
3. Click **Add Controller**.
4. Search for "Deluge" (under "Synthstrom" vendor).
5. Select the **Synthstrom Deluge** script.
6. Configure the MIDI ports:
   - **MIDI Input**: The Deluge USB MIDI port.
   - **MIDI Output**: The Deluge USB MIDI port.

## Deluge Setup

1. Connect Deluge via USB.
2. Enter **Controller Mode**:
   - Hold `SHIFT` + `ACCOMP` (on older firmware) or check your firmware manual.
   - **Modern Method**: Hold `LEARN/INPUT` while powering on.
   - Or: `SHIFT` + `SELECT` > `SETTINGS` > `MIDI` > `CMD` > `ON`.

## Features Guide

### Clip View (Default)
- **Grid**: 16x8 matrix.
  - **Press**: Toggle note.
  - **Hold + Press**: Create long note (Hold start pad, press end pad).
- **Sidebar (Left)**:
  - **MUTE**: Mute sequence rows (Row turns Yellow when muted).
  - **LAUNCH**: Audition row notes (Green when playing).
- **Navigation (Encoders)**:
  - **Horizontal (`<>`)**: Scroll steps pages.
  - **Vertical (Scroll)**: Scroll octaves.
  - **Select**: Change note resolution.
- **Parameters (Gold Knobs)**:
  - **Knob 1**: Volume (Upper) / Pan (Lower).
  - **Knobs 2-8**: Remote Control Parameters 1-8.
  - **Press Knob Button**: Toggle Upper/Lower mode.
