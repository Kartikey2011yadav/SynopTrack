# 📖 Feature Definitions & System Logic

Detailed explanations of core features, architectural concepts, and system behaviors in SynopTrack.

---

## 🏗️ Core Architecture (Map OS)

### 1. Map OS Architecture (Level-Based UI)

**Phase 1**
The Map serves as the **Home Screen**, but distinct features like Chat and Profile reside on **Dedicated Pages** (full-screen destinations).

- **Concept**: "Map First, Feature Second".
- **Behavior**:
  - **Home (Map)**: Contains quick actions (FAB, Status/Moments rings) and essential overlays.
  - **Navigation**: Transitioning to Chat or Profile moves away from the floating state into a dedicated screen experience.

### 2. Floating UI Layer (Map Overlays)

**Phase 1**
The UI elements that specifically live _on top_ of the map.

- **Structure**: `Box` layout containing the Map and its immediate controls.
- **Components**:
  - **Quick Actions**: Current Location FAB, Compass.
  - **Status Rings**: Tappable avatars for "Close Friends Moments" floating near the top or bottom.
- **Interaction**: Click-through enabled for empty spaces so panning works naturally.

### 3. Global UI State Store

**Phase 1**
A centralized `StateFlow` (Singleton) managing the visibility of all overlay elements.

- **Purpose**: Prevents "UI Clutter." (e.g., Opening Chat automatically hides the Search Bar).
- **States**: `MapFocused`, `SheetExpanded`, `DialogOverlay`, `GhostModeActive`.

---

## � Onboarding & Gatekeeping

### 4. Smart Registration

**Phase 0.5**
A logic layer triggering _after_ authentication but _before_ granting access to the Map OS.

- **Logic**: Checks if the user profile is complete (Display Name, Photo).
- **Behavior**:
  - **New Users**: Forces navigation to `RegistrationScreen`.
  - **Auto-Fill**: Pulls data (photo/email) from Auth credentials to reduce friction.

### 5. Permission Gate ("Educational Barrier")

**Phase 0.5**
A dedicated UI step explaining _why_ location is needed before triggering the system dialog.

- **Flow**:
  1.  **Soft Ask**: Show illustration + "To see friends, enabling location is required."
  2.  **System Trigger**: User taps "I Understand" -> System Dialog opens.
  3.  **Recovery**: If permanently denied, button changes to "Open Settings".

---

## 🤝 Social & Presence

### 6. Social Graph Engine (Friends & Groups)

**Phase 2 & 3**
The system responsible for connecting users.

- **Friends (Mutual)**: Two-way connection. Uses unique Invite Codes to connect. Allows 1:1 Messaging.
- **Groups (Convoy)**: Multi-user collection for travel. Allows Group Messaging and Location Sharing.
- **Structure**:
  - `users/{uid}/friends`: List of connected User IDs.
  - `groups/{groupId}`: Shared context for Convoys.

### 7. Instagram-Inspired UI Logic

**Phase 3 (Polish)**
Adopting familiar patterns for navigation and hierarchy.

- **Home (MapOs)**:
  - **Top Bar**: (+) Add, <App Title>, (Heart) Social/Notifications.
- **Profile**:
  - **Header**: Stats (Friends Count) + Bio.
  - **Settings**: Moved to a dedicated screen, accessible via top-right menu.
- **Social Screen**: List of active 1:1 chats and Groups.

### 7. Location Modes (Active vs Passive)

**Phase 2 (Refactored)**
A dual-mode location strategies to balance battery life and real-time utility.

- **Modes**:
  - **Passive (Default)**: Uses `WorkManager` to fetch location periodically (e.g., every 1 hour). Main purpose is to provide "Last Known Location".
  - **Active Convoy**: Triggered by user ("Start Convoy"). Uses `ForegroundService` for high-frequency (10s) updates.
  - **Ghost (Offline)**: User can stop sharing entirely (effectively "Offline").

### 8. Convoy-as-a-Service

**Phase 2**
The "Convoy" is treated as an active session, similar to a navigation trip.

- **Trigger**: Explicit "Start" and "Stop" actions.
- **Notification**: Persistent notification ("Convoy Active") ensures the service is not killed.
- **Privacy**: High-accuracy location is ONLY shared during an active Key.

---

## 💬 Engagement & Stories

### 8. Vanish Logic (TTL)

**Phase 3**
Data lifecycle management for ephemeral content (Snapchat-style).

- **Types**:
  - **Read-Based**: "Delete 10s after viewing" (for sensitive media).
  - **Time-Based**: "Delete 24h after posting."
- **Implementation**: Client filters old data; Server physically deletes via TTL policy/Cloud Function.

### 9. The "Story Bar"

**Phase 4**
Visual indicator for ephemeral "Moments" on the map.

- **UI**: User avatar gains a colored ring (Gold/Purple).
- **Behavior**:
  - Tap Avatar -> Opens Full-Screen Media Viewer.
  - **Location-Pinned**: Moments stay where they were taken, not where the user is currently.

---

## � Convoy Intelligence

### 10. Collaborative Driving ("Lead Car")

**Phase 5**
Specialized tracking for multi-car travel.

- **Logic**: Calculates "Distance to Leader" instead of just "Distance to Destination".
- **Smart Stop**: If Lead Car speed is 0 for >2 mins (excluding traffic), followers get a "Lead Car has stopped" alert.

---

## �️ Trust & Safety

### 11. Emergency Panic Button

**Phase 6**
Safety trigger for immediate assistance.

- **Trigger**: Long-press button or Shake (configurable).
- **Action**:
  - Forces **Live Mode** (bypasses Ghost).
  - SMS to emergency contacts with Map Link.
  - "High Priority" push to Group Members.

---

## 🔐 Identity & Security

### 12. Identity System (Name#Hash)

**Phase 3**
A unique identification method ensuring no two users have the same display identity, while allowing duplicate display names.

- **Structure**: `DisplayName` + `#` + `Discriminator` (4-char alphanumeric).
- **Example**: `Kartikey#9uwu`
- **Logic**:
  - `Discriminator` is auto-generated or custom.
  - **Constraint**: The pair `(Name, Discriminator)` MUST be unique globally.
  - **Search**: Users can be found by entering the full ID.

### 13. Secure Vanishing Chat

**Phase 4**
A privacy-first messaging protocol.

- **Storage Policy**:
  - **Server**: Messages are "Hard Deleted" 48 hours after timestamp.
  - **Client**: Messages persist indefinitely unless manually deleted by user (or app cache cleared).
- **Encryption**: End-to-End encryption ensures only the sender and recipient can read the content.
