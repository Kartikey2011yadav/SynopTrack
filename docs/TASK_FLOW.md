# Task Flow & Progress Tracker

## 🟢 Phase 3: Communication & Real-Time Engagement (Active)

### Step 1: Identity & User Profile

- [ ] **Backend**: Update `UserProfile` to support `username` and `discriminator` (#Hash).
- [ ] **Logic**: Implement "Collision Check" to ensure `Name#Hash` is unique.
- [ ] **UI**: Create `EditProfileScreen` (Bio, Avatar, Name).
- [ ] **UI**: Update `ProfileScreen` to display the new Identity format.

### Step 2: Friend Network & Discovery

- [ ] **Data**: Create `FriendRequest` collection and Repository.
- [ ] **UI (Search)**: Search by "Invite Code" or "Name#Hash". Add Contact Sync tab.
- [ ] **UI (Activity)**: Create new Screen for "Requests" (Accept/Decline) and "Suggestions".

### Step 3: Secure Chat & Media

- [ ] **Encryption**: Implement E2E key generation and message locking.
- [ ] **Vanish Logic**: Implement 48h TTL Cloud Function trigger types.
- [ ] **Media**: Add Image/Video/GIF picker and upload logic.
- [ ] **Groups**: Apply encryption logic to Group contexts (Sender Key distribution).

### Step 4: Moments & Map Intelligence

- [ ] **Capture**: Camera/Gallery integration for Stories.
- [ ] **Map**: Render distinct Markers for Friends' last location vs. their Stories.
- [ ] **Ghost Mode**: Implement "Freeze" and "Off" states.

---

### Archive (Completed)

- [x] Phase 1: Map OS Core (Google Maps, Location Service).
- [x] Phase 2: Social Graph (Friends, Groups, Invite Codes).
- [x] UI Overhaul: Instagram-style Home, Profile, and Navigation.
