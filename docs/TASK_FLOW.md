# Task Flow & Progress Tracker

## 🟢 Phase 3: Communication & Real-Time Engagement (Active)

### Step 1: Real-Time Chat System

- [ ] **Data Layer**: Implement `sendMessage()` and `observeMessages()` in `ChatRepository`.
- [ ] **Domain Layer**: Create `SendMessageUseCase` and `GetMessagesUseCase`.
- [ ] **UI Layer (SocialScreen)**: Finalize Unified List (Friends + Groups) with real data.
- [ ] **UI Layer (ChatScreen)**: Connect UI to live Firestore stream. Add "Typing" indicators.

### Step 2: Notifications (FCM)

- [ ] **Setup**: Configure FCM in Firebase Console and Android App.
- [ ] **Service**: Create `MyFirebaseMessagingService` to handle incoming data payloads.
- [ ] **Local Handling**: Show system notifications for background messages.
- [ ] **Deep Linking**: Tap notification -> Open specific Chat/Map location.

### Step 3: Social Refinements

- [x] **Profile Refactor**: Dual-State UI (My Profile vs Other), Privacy Mode, and Image Uploads.
- [ ] **Friend Requests**: UI for "Accept/Decline" (Logic partially implemented in ViewModel).
- [x] **Profile Stats**: Connected to SocialRepository (Friends/Groups count).

## 🟡 Phase 4: Discovery & Moments (Planned)

### Step 1: Places API

- [ ] Enable Places SDK.
- [ ] Implement Search Bar with Autocomplete.

### Step 2: Geotagged Stories

- [ ] Camera integration.
- [ ] Upload media to Firebase Storage with Location metadata.
- [ ] Render Story Markers on Map.

---

### Archive (Completed)

- [x] Phase 1: Map OS Core (Google Maps, Location Service).
- [x] Phase 2: Social Graph (Friends, Groups, Invite Codes).
- [x] UI Overhaul: Instagram-style Home, Profile, and Navigation.
