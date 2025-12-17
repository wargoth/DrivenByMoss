# Clip View → Bitwig Note Editor / Drum Machine

Piano roll / step sequencer. Rows = notes (synth) or drum pads (kit). Columns = steps.

## Grid Layout

```
┌────────────────────────────────────────────────────────────────────┬──────┬──────────┐
│                  Steps (16 columns = note steps)                   │ MUTE │ AUDITION │
├────────────────────────────────────────────────────────────────────┼──────┼──────────┤
│ C5:  [●][  ][  ][●][  ][  ][  ][  ][●][  ][  ][●][  ][  ][  ][  ] │  🟢  │   🎵     │
│ B4:  [  ][  ][●][  ][  ][  ][●][  ][  ][  ][●][  ][  ][  ][●][  ] │  🟢  │   🎵     │
│ A4:  [  ][●][  ][  ][●][  ][  ][●][  ][●][  ][  ][●][  ][  ][●]  │  🟢  │   🎵     │
│ C4:  [●][  ][  ][  ][●][  ][  ][  ][●][  ][  ][  ][●][  ][  ][  ] │  🟢  │   🎵     │
└────────────────────────────────────────────────────────────────────┴──────┴──────────┘
```

---

## Grid Pad Functions

| Gesture | Bitwig Function |
|---------|------------------|
| **Tap pad** | Toggle note on/off at step |
| **Hold pad + turn SCROLL◄►** | Adjust note velocity |
| **Hold start pad + press end pad** (same row) | Set note length / create tie |

---

## MUTE Sidebar (x=16)

| Gesture | Bitwig Function |
|---------|------------------|
| **Tap MUTE** | Mute/unmute row (pitch or drum lane) |

### LED States

| State | LED Color |
|-------|-----------|
| Row unmuted | 🟢 Green |
| Row muted | 🟡 Yellow |

---

## AUDITION Sidebar (x=17)

| Gesture | Bitwig Function |
|---------|------------------|
| **Tap AUDITION** | Play note/sound (MIDI out) |
| **SHIFT + AUDITION** | Select row silently (kit) |

### LED States (Synth)
- Root note: Highlighted
- Octave note: Secondary highlight

### LED States (Kit)
- Each pad shows row color

---

## Navigation

| Control | Bitwig Function |
|---------|------------------|
| **SCROLL◄►** | Scroll steps left/right |
| **SCROLL▼▲** | Scroll pitch/rows up/down |
| **Press + Turn SCROLL◄►** | Zoom (change step resolution 1/4 - 1/64) |

---

## LED Feedback (Main Grid)

| State | LED Color |
|-------|-----------|
| Empty step | Off |
| Note exists | Note color (by pitch) |
| Velocity encoding | Brightness |
| Playhead position | White cursor column |

---

## Synth vs Kit Differences

| Aspect | Synth Clip | Kit Clip |
|--------|-----------|----------|
| Rows | Pitches (chromatic/scale) | Drum pads |
| AUDITION | Plays pitch | Plays drum sound |
| Colors | By pitch | By row |
| AFFECT_ENTIRE | Always on | Optional |

---

## Reference
- [Deluge-Guidebook-4p0.txt](../Deluge-Guidebook-4p0.txt) Section 2.6 (Clip View)
