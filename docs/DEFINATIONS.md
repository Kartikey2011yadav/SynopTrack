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

### 6. Social Graph Engine

**Phase 2**
The system responsible for connecting users and managing groups.

- **Mechanics**:
  - **Deep Links**: `synoptrack://join/xyz123` auto-adds users to groups.
  - **QR Codes**: In-person group joining.

### 7. Ghost Mode (Granular Privacy)

**Phase 2**
A privacy control system allowing users to obfuscate their real-time location.

- **States**:
  - **👻 Ghost (Frozen)**: Stops uploading GPS. Server shows last known location with timestamp ("Frozen 2h ago").
  - **🌫️ Blurred (Precise-ish)**: Adds random noise (+/- 500m) to coordinates. Friends see a general area circle.
  - **🟢 Live**: Standard high-accuracy GPS (10-30s updates).

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
