# Task Flow

## 🏃 In Progress

- **Project Analysis**: Analyzing current codebase vs intended architecture.
- **Documentation**: Setting up `docs/` and plans.

## 📋 To Do (Immediate - Phase 2)

- [ ] **Social Repository**: Implement logic for creating/joining groups (Social Graph).
- [ ] **Presence Service**: Implement Heartbeat/Online mechanism.
- [ ] **Location Streaming**: Realtime updates to DB.
- [ ] **Group UI**: Screens for "Create Group" and "Invite Members".
- [ ] **Ghost Mode**: Implement Privacy toggle logic.

## 📌 Backlog (Future Phases)

- [ ] Implement `ChatRepository` with Room (Phase 3)
- [ ] Setup FCM Service (Phase 3)

## ✅ Done

- [x] Initial Project Analysis
- [x] `docs/` folder creation
- [x] `dump` file analysis
- [x] **Refactor Navigation**: Remove existing BottomBar if present, implement `MapOS` navigation state.
- [x] **Onboarding Flow**: Implement Registration logic and Permission Gates (Phase 0.5).
- [x] **Map Host Screen**: Create the `MapScreen` as the root composable.
- [x] **Search UI**: Implement Floating Search Bar and Chips (Phase 1).
- [x] **Floating Panels**: Create generic `OverlayPanel` composables for Chat, Profile, etc.
- [x] **Theme Toggle**: Implement dynamic theme switching in `core`.
