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

### 🚧 Phase 3: Communication & Real-Time Engagement (Current Focus)

**Goal**: Make the app "sticky" with real-time conversations and notifications.

- [ ] **Chat System v1**:
  - [x] **UI**: Gradient bubbles (Snapchat style).
  - [ ] **Real-time Messaging**: Firestore subcollection (`messages`) listener.
  - [ ] **Optimistic UI**: Local display before server confirmation.
  - [ ] **Unified Social Screen**: List displaying both 1:1 DMs (Friends) and Group Chats.
- [ ] **Notification Infrastructure**:
  - [ ] **FCM Integration**: Setup Firebase Cloud Messaging.
  - [ ] **Triggers**: Notify on "Friend Request", "New Message", "Convoy Started".
- [ ] **Smart Convoy Features**:
  - [x] **UI Controls**: "Start/Stop Convoy" directly from Home/Social.
  - [ ] **Presence Indicators**: "Typing...", "Online".

### 🔮 Phase 4: Discovery & Moments

**Goal**: Expand beyond tracking to "Living on the Map".

- [ ] **Moments/Stories**:
  - [ ] 24h Geotagged Media.
  - [ ] Map-based Story viewing.
- [ ] **Places & Search**:
  - [ ] Google Places API integration.
  - [ ] "Meetup Point" selection.

### 🔮 Phase 5: Intelligence & Safety

- [ ] **Safety Core**:
  - [ ] Emergency Panic Button.
  - [ ] "Freeze Location" privacy mode.
- [ ] **Convoy Intelligence**:
  - [ ] ETA Calculation (Distance Matrix).
  - [ ] Automatic "Arrived" notifications.

---

_Maintained by Antigravity - Senior Project Lead_
