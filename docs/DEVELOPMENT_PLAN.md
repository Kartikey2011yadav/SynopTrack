# Development Plan

This document outlines the strategic roadmap for SynopTrack. We have transitioned from building "Screens" to building a "Social Location Operating System".

## 📍 Current Status

**Phase**: `PHASE 3 - SOCIAL CONNECTION & ENGAGEMENT`
**Priority**: 🟥 HIGH (Chat & Notifications)

---

## 📅 Roadmap

### ✅ Phase 1: Foundation & Core OS (Completed)

**Goal**: Establish the "Map OS" architecture and secure foundations.

- [x] **Authentication**: Firebase Login/Registration with smart onboarding.
- [x] **Map OS Core**:
  - [x] Google Maps Integration with Custom Styles (Dark/Retro/Silver).
  - [x] Persistent Map Host (Activity/Scaffold restructuring).
  - [x] State-Based Navigation (replacing fragile fragment transactions).
- [x] **Location Engine**:
  - [x] **Active Convoy**: Foreground Service for high-frequency updates during trips.
  - [x] **Passive Tracking**: WorkManager for battery-efficient hourly updates (Ghost Mode).
  - [x] **Battery Awareness**: Streaming battery level and charging status.

### ✅ Phase 2: Social Graph & UI Overhaul (Completed)

**Goal**: Create a premium, Instagram-inspired aesthetic and foundational social structures.

- [x] **Social Graph**:
  - [x] **Mutual Friends**: Invite Code logic (6-digit alphanumeric).
  - [x] **Groups/Convoys**: Create and Join logic.
  - [x] **Unified Data Layer**: `SocialRepository` handling both Friends and Groups.
- [x] **UI/UX 2.0 (Instagram Aesthetic)**:
  - [x] **Home**: Opaque Top Bar (Logo, Add, Social icons).
  - [x] **Navigation**: Custom "Black" Bottom Navigation with Filled/Outlined icon states.
  - [x] **Profile**: Premium header with Stats, clickable Invite Code, and clean typography.
  - [x] **Theme**: Dark Mode optimized with `Color.Black` surfaces.
- [x] **Local Persistence**: Room Database setup for caching social data and chat.

### 🚧 Phase 3: Identity & Friend Network (Current Focus)

**Goal**: Establish a robust Social Graph and User Identity system.

- [ ] **User Identity 2.0**:
  - [ ] **Unique ID System**: `Name#Hash` (e.g., Kartikey#9uwu) for unique identification.
  - [ ] **Profile Enhancements**: Edit Bio, Profile Picture, and Name.
- [ ] **Friend Network**:
  - [ ] **Friend Requests**: Send/Receive methodology.
  - [ ] **Activity Center**: "Heart" screen for pending requests and friend suggestions.
  - [ ] **Discovery**: Search by Name or Invite Code; Contact Syncing.

### 🔮 Phase 4: Secure Communication & Moments

**Goal**: Real-time engagement with privacy and location context.

- [ ] **Secure Chat**:
  - [ ] **E2E Encryption**: Private/Public key exchange for secure messaging.
  - [ ] **Vanishing Messages**: 48h Server TTL; Local persistence until manual deletion.
  - [ ] **Rich Media**: Share Images, Videos, GIFs.
- [ ] **Moments (Stories)**:
  - [ ] **Map Integrated**: Stories appear at the location they were taken.
  - [ ] **Format**: Images/Videos with 24h visibility.
  - [ ] **Ghost Mode**: Privacy controls for location sharing.

### 🔮 Phase 5: Intelligence & Safety

- [ ] **Safety Core**:
  - [ ] Emergency Panic Button.
  - [ ] "Freeze Location" privacy mode.
- [ ] **Convoy Intelligence**:
  - [ ] ETA Calculation (Distance Matrix).
  - [ ] Automatic "Arrived" notifications.

---

_Maintained by Antigravity - Senior Project Lead_
