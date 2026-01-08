# Task Flow

## 🏃 In Progress (Phase 3)

- **Chat Features**: Implementing TTL and advanced message types.
- **Notifications**: Setting up FCM for background alerts.

## 📋 To Do (Immediate)

- [ ] **FCM Setup**: Firebase Cloud Messaging for push notifs.
- [ ] **Chat Persistence**: Cache messages locally using Room.
- [ ] **Message TTL**: Logic to auto-delete messages after 24h.

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
- [x] **Social Repository**: Implement logic for creating/joining groups.
- [x] **Presence Service**: `PresenceForegroundService` implementation.
- [x] **Location Architecture**: Refacted to Active (Convoy) vs Passive (Worker).
- [x] **Group UI**: Create/Join Dialogs and Social Screen.
- [x] **Chat UI**: Built `ChatScreen` with premium bubble design.
- [x] **Profile UI**: Premium redesign of `ProfileScreen`.
- [x] **Ghost Mode**: Pivot to "Convoy-as-a-Service".
