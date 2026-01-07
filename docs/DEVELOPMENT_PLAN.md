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

### 🚦 Phase 0.5: Onboarding & Gatekeeping (New)

**Goal**: Smart Onboarding & Permissions Flow.

- [ ] **Smart Registration**: Display Name, Profile Picture setup.
- [ ] **Permission Gate**: Educational flow for Location permissions.
- [ ] **State Preservation**: Ensure users return to the correct step if they exit.

### 🚧 Phase 1: Map OS Core (In Progress)

**Goal**: Make the map the permanent operating system.

- [ ] **Permanent Map Host**: Activity/Scaffold restructuring.
- [ ] **Floating UI Layer**: System for panels over the map.
- [ ] **Places & Search Layer**: Floating Search Bar, Quick Chips (Restaurants, Gas).
- [ ] **State-Based Navigation**: Removing BottomNav for a fluid state machine.
- [ ] **Global UI State Store**: Central source of truth for UI visibility.
- [ ] **Theming**: Dark/Light runtime toggle.

### 🔮 Phase 2: Realtime Presence & Social Graph

**Goal**: Make the app alive and connected.

- [ ] **Social Graph Engine**: Group Management, Invite Logic (Links/QR).
- [ ] RTDB Heartbeat Model
- [ ] Location Streaming Service
- [ ] Ghost Mode (Privacy controls)
- [ ] Offline Caching Strategy

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
