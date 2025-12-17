# Arranger View → Bitwig Arranger Timeline

Linear timeline view. Each row = track. Pads = clip instances positioned in time.

## Grid Layout

```
┌────────────────────────────────────────────────────────────────────┬──────┬──────────┐
│                 Timeline (16 columns = time segments)              │ MUTE │ AUDITION │
├────────────────────────────────────────────────────────────────────┼──────┼──────────┤
│ Track 1: [====Instance====][    ][=Instance=][======]...          │  🟢  │   🔵     │
│ Track 2: [=Instance=][    ][========Instance======][    ]...      │  🟢  │   🔵     │
│ Track 3: [    ][===Instance===][    ][    ][    ]...              │  🟡  │   🔵     │
└────────────────────────────────────────────────────────────────────┴──────┴──────────┘
```

---

## Grid Pad Functions

| Gesture | Bitwig Function |
|---------|------------------|
| **Tap bright pad** | Select clip instance |
| **Tap tail pad** (dim) | Enter Clip View for that instance |
| **Hold pad + SCROLL◄►** | Move clip instance |
| **Hold pad + turn SELECT** | Change clip instance / length |
| **SHIFT + turn SCROLL◄►** | Insert/delete time (shift clips) |

---

## MUTE Sidebar (x=16)

| Gesture | Bitwig Function |
|---------|------------------|
| **Tap MUTE** | Toggle track mute |
| **SCROLL◄► + MUTE** | Solo track |

### LED States

| State | LED Color |
|-------|-----------|
| Track unmuted | 🟢 Green |
| Track muted | 🟡 Yellow |
| Track soloed | 🔵 Blue (others dim) |

---

## AUDITION Sidebar (x=17)

| Gesture | Bitwig Function |
|---------|------------------|
| **Tap AUDITION** | Preview track sound |
| **AUDITION + turn SELECT** | Load new preset |
| **AUDITION + instrument button** | Change track type |

---

## Navigation

| Control | Bitwig Function |
|---------|------------------|
| **SCROLL◄►** | Scroll timeline left/right |
| **SCROLL▼▲** | Scroll tracks up/down |
| **Press + Turn SCROLL◄►** | Zoom timeline |
| **SCROLL◄► + PLAY** | Play from current position |

---

## LED Feedback

**Main Grid**:
| State | LED Color |
|-------|-----------|
| Empty | Off |
| Clip instance start | Bright (section color) |
| Clip instance body | Dim (section color) |
| Unique instance | White |
| Playhead position | White cursor column |

---

## Reference
- [Deluge-Guidebook-4p0.txt](../Deluge-Guidebook-4p0.txt) Section 8 (Arranger View)
