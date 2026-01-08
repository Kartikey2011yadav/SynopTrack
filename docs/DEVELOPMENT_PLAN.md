# Development Plan

This document outlines the strategic roadmap for SynopTrack. We have transitioned from building "Screens" to building "Systems".

## 📍 Current Status

**Phase**: `PHASE 1 - MAP OS CORE`
**Priority**: 🟥 HIGH (Immediate Focus)

---

## 📅 Roadmap

### ✅ Phase 0: Foundation (Completed)

- [x] Project Setup & Dependencies
- [x] Authentication Module (`auth`)
- [x] Profile Foundation (`profile`)
- [x] Basic Jetpack Compose Setup

### ✅ Phase 0.5: Onboarding & Gatekeeping (Completed)

**Goal**: Smart Onboarding & Permissions Flow.

- [x] **Smart Registration**: Display Name, Profile Picture setup.
- [x] **Permission Gate**: Educational flow for Location permissions.
- [x] **State Preservation**: Ensure users return to the correct step if they exit.

### ✅ Phase 1: Map OS Core (Completed)

**Goal**: Make the map the permanent operating system.

- [x] **Permanent Map Host**: Activity/Scaffold restructuring.
- [x] **Floating UI Layer**: System for panels over the map.
- [x] **Places & Search Layer**: Floating Search Bar, Quick Chips (Restaurants, Gas).
- [x] **State-Based Navigation**: Removing BottomNav for a fluid state machine.
- [x] **Global UI State Store**: Central source of truth for UI visibility.
- [x] **Theming**: Dark/Light runtime toggle.

### ✅ Phase 2: Realtime Presence & Social Graph (Completed)

**Goal**: Make the app alive and connected.

- [x] **Social Graph Engine**: Group Management, Invite Logic (Links/Codes).
- [x] **Convoy Service**: Active Foreground Service for real-time tracking.
- [x] **Passive Location**: WorkManager for battery-efficient background updates.
- [x] **UI Polish**: Glassmorphism/Premium aesthetics for Social & Profile screens.

### 🚧 Phase 3: Ephemeral Chat & Engagement (In Progress)

**Goal**: Make the app emotionally sticky.

- [x] **Basic Chat UI**: Snapchat-style bubbles and layout.
- [x] **Chat Repository**: Firestore integration for messaging.
- [ ] **Notification Infrastructure**: FCM Integration (Background engagement).
- [ ] **Vanish Timers & TTL**: Auto-delete old messages.
- [ ] **Local Persistence**: Room database for offline chat history.

### 🔮 Phase 3: Ephemeral Chat & Engagement

**Goal**: Make the app emotionally sticky.

- [ ] **Notification Infrastructure**: FCM Integration (Background engagement).
- [ ] Chat UI (Snap-style style)
- [ ] Vanish Timers & TTL
- [ ] Local Room Persistence
- [ ] Server Purge Logic

### 🔮 Phase 4: Moments / Stories

- [ ] Story Bar UI
- [ ] Media Viewer
- [ ] 24h Lifecycle Logic

### 🔮 Phase 5: Convoy Intelligence

- [ ] Distance Matrix Integration
- [ ] ETA Synchronization
- [ ] Smart Stop Detection

### 🔮 Phase 6: Trust & Privacy Layer

- [ ] Per-group privacy settings
- [ ] Emergency Freeze / Panic Button
- [ ] Data Wipe features

### 🔮 Phase 7: Polish & Scale

- [ ] Advanced Animations
- [ ] Battery Optimization
- [ ] Release Hardening

---

_Created by Antigravity - Senior Project Lead_
