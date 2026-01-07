1. Map OS Architecture (Level-Based UI)
This diagram illustrates the "glassmorphic Sheet Stack" concept where the map remains the constant background while a few  other UI elements float on top like moments, status, current location, close friends etc.

2. Convoy Intelligence Logic
This diagram visualizes how the app calculates relative distances and manages the "Lead Car" concept for group travel.

🚦 Phase 0.5: Onboarding & Gatekeeping
1. Smart Registration (The "Missing Data" Catcher)
Definition: A logic layer that intercepts the login flow. It does not just check "Is Logged In"; it checks "Is Profile Complete."

Behavior:

New Users: If auth.currentUser exists but firestore.users[uid] is missing, force navigation to RegistrationScreen.

Incomplete Users: If firestore.users[uid].displayName is null/empty, force RegistrationScreen.

Auto-Fill: Pulls photoUrl and email from the Google Auth credential to pre-fill the form, reducing user friction.

2. Permission Gate (The "Educational" Barrier)
Definition: A dedicated UI that explains why permissions are needed before triggering the system dialog.

Behavior:

Soft Ask: Shows a beautiful "Map" illustration with text: "To see your friends, enabling location is required."

System Trigger: Only when the user taps "I Understand" do we launch the OS permission dialog.

Recovery Loop: If a user denies permissions permanently (Settings > App > Deny), the app detects this on restart and changes the button to "Open Settings" instead of "Ask Permission."

🚧 Phase 1: Map OS Core
3. Floating UI Layer (The "Glass" Interface)
Definition: A UI architecture where the GoogleMap composable is the root container (filling MaxSize). All other UI elements (Search Bar, Profile, Bottom Sheet) are siblings in a Box that sit above the map with transparency.

Behavior:

Edge-to-Edge: The map draws under the status bar and navigation bar.

Click-Through: Tapping empty space on the UI layer passes the touch event to the map (allowing panning). Tapping a "Chip" or "Card" intercepts the touch.

4. Global UI State Store
Definition: A centralized StateFlow (Singleton) that controls the visibility of overlays.

Why: To prevent "UI Clutter." If the "Chat Sheet" is open, the "Search Bar" should automatically hide.

States: MapFocused, SheetExpanded, DialogOverlay, GhostModeActive.

🔮 Phase 2: Realtime Presence & Social Graph
5. Ghost Mode (Granular Privacy)
Definition: A privacy system giving users control over how their location is broadcasted, adjustable per-group or globally.

States:

👻 Ghost (Frozen): The user stops uploading GPS updates. The server retains the last known location and adds a "Frozen 2h ago" timestamp. The user appears on the map, but the pin never moves.

🌫️ Blurred (Precise-ish): The app adds random noise (jitter) to the GPS coordinates (e.g., +/- 500m radius) before uploading. Friends see a "General Area" circle instead of a precise pin.

🟢 Live (Precise): Standard high-accuracy GPS uploading (every 10-30s depending on movement).

6. Social Graph Engine (Invite Logic)
Definition: The system connecting users.

Mechanic:

Deep Links: Groups have a unique inviteCode. Sharing a link synoptrack://join/xyz123 opens the app and auto-adds the user to the group.

QR Codes: Each group has a generated QR code for in-person joining.

🔮 Phase 3: Ephemeral Chat & Engagement
7. Vanish Logic (TTL - Time To Live)
Definition: The mechanism ensuring data is temporary (Snapchat style).

Types:

Read-Based Vanish: "Delete 10 seconds after viewing" (For sensitive media).

Time-Based Vanish (Standard): "Delete 24 hours after posting."

Implementation:

Client-Side: The app filters out messages where timestamp < (now - 24h).

Server-Side: A Firestore TTL Policy or Cloud Function physically deletes the documents daily to save storage costs.

🔮 Phase 4: Moments / Stories
8. The "Story Bar"
Definition: When a user uploads a "Moment" (Video/Photo), their avatar on the map gains a colored ring (Gold/Purple).

Behavior:

Tapping the avatar on the map opens the Media Viewer (Full screen, immersive) instead of the Profile Sheet.

The location of the Moment is pinned to where it was taken, not where the user is now.

🔮 Phase 5: Convoy Intelligence
9. Collaborative Driving (The "Lead Car" Logic)
Definition: Specialized tracking for road trips involving multiple cars.

Logic:

The Delta: Instead of just showing "Distance to Destination," the app calculates "Distance to Leader."

Example: If Car A (Lead) is at Mile 50 and Car B is at Mile 40, Car B sees: "10 miles behind Leader."

Smart Stop: If the Lead Car's speed drops to 0 for > 2 minutes (and it's not traffic), the app sends a push notification to followers: "Lead Car has stopped."

🔮 Phase 6: Trust & Privacy
10. Emergency Panic Button
Definition: A safety trigger for solo travelers.

Behavior:

Trigger: Long-press a specific button or shake the phone (if configured).

Action: Immediately forces "Live Mode" (bypassing Ghost Mode), sends an SMS to emergency contacts with the Google Maps link, and notifies all Group Members with a "High Priority" push notification.

