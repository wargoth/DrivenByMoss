# Song View → Bitwig Clip Launcher

Each row = Bitwig Track, Grid shows clip representation. LAUNCH sidebar controls playback.

## Grid Layout (16×8 main + 2 sidebar columns)

```
┌────────────────────────────────────────────────────────────────────┬──────────┬──────────┐
│                     Clip Rows (16 columns)                         │  LAUNCH  │ SECTION  │
├────────────────────────────────────────────────────────────────────┼──────────┼──────────┤
│ Track 1: [====Clip====][    ][    ][    ][    ][    ]...          │  🟢/🔴   │   🔵     │
│ Track 2: [=Clip=][    ][    ][    ][    ][    ][    ]...          │  🟢/🔴   │   🔵     │
│ Track 3: [    ][===Clip===][    ][    ][    ][    ]...            │  🟢/🔴   │   🔵     │
└────────────────────────────────────────────────────────────────────┴──────────┴──────────┘
```

---

## Grid Pad Functions

| Gesture | Deluge Function | Bitwig Function |
|---------|-----------------|------------------|
| **Tap pad** (empty row) | Create new clip | Create new track + clip, enter editor |
| **Tap pad** (existing clip) | Enter clip view | Enter clip editor for track |
| **Hold pad** | Show info + parameters | Select track, access parameter controls |
| **Hold pad + DELETE** | Delete clip | Delete clip |
| **Hold pad + turn SELECT** | Change preset | Change clip/preset |
| **Hold pad + destination pad** | Clone clip | Clone clip to destination row |

---

## LAUNCH Sidebar (x=16)

| Gesture | Deluge Function | Bitwig Function |
|---------|-----------------|------------------|
| **Tap LAUNCH** | Arm clip (quantized) | Queue clip launch/stop |
| **SHIFT + LAUNCH** | Immediate launch/stop | Immediate launch/stop |
| **SCROLL◄► + LAUNCH** | Solo clip row | Solo track |

### LED States

| State | LED Color |
|-------|-----------|
| Clip stopped | 🔴 Red solid |
| Clip armed to play | 🔴 Red blinking |
| Clip playing | 🟢 Green solid |
| Clip armed to stop | 🟢 Green blinking |
| Track soloed | 🔵 Blue |

---

## SECTION Sidebar (x=17)

| Gesture | Deluge Function | Bitwig Function |
|---------|-----------------|------------------|
| **Tap SECTION** | Arm section to launch | Queue scene launch |
| **SHIFT + SECTION** | Cycle section colors | Assign to different scene |
| **Hold SECTION + SELECT** | Change repeat mode | Scene follow action |

### LED States
- Shows section color (one of 12 colors matching clip groupings)

---

## Navigation

| Control | Bitwig Function |
|---------|------------------|
| **SCROLL◄►** | Navigate scenes (columns) |
| **SCROLL▼▲** | Navigate track rows |

---

## Reference
- [Deluge-Guidebook-4p0.txt](../Deluge-Guidebook-4p0.txt) Section 7 (Song View)
