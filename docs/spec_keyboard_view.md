# Keyboard View → Isomorphic Keyboard

Full 16×8 grid becomes isomorphic keyboard for live melodic playing.

## Grid Layout

```
┌────────────────────────────────────────────────────────────────────────────────────┐
│                     Isomorphic Keyboard (16×8 = 128 notes)                         │
├────────────────────────────────────────────────────────────────────────────────────┤
│ [C6][D6][E6][F6][G6][A6][B6][C7]...                                                │
│ [C5][D5][E5][F5][G5][A5][B5][C6]...                                                │
│ [C4][D4][E4][F4][G4][A4][B4][C5]...                                                │
│  ...                                                                                │
└────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Pad Functions

| Gesture | Bitwig Function |
|---------|------------------|
| **Press pad** | Note On (MIDI output) |
| **Release pad** | Note Off |

---

## LED Feedback

| State | LED Color |
|-------|-----------|
| Scale root notes | Highlighted color |
| In-scale notes | Secondary color |
| Out-of-scale notes | Dim/off |
| Currently playing | Bright |

---

## Navigation

| Control | Bitwig Function |
|---------|------------------|
| **SCROLL▼▲** | Shift octave up/down |
| **SCALE button** | Toggle scale mode |

---

## Reference
- [Deluge-Guidebook-4p0.txt](../Deluge-Guidebook-4p0.txt) Keyboard sections
