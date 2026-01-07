# SynopTrack

**SynopTrack** is a premium location-based social application built for Android using Jetpack Compose. It reimagines the map interface as a "MapOS" — a persistent, immersive, full-screen map experience that serves as the foundation for all social interactions, similar to high-end apps like Uber or Snapchat.

## 🚀 Vision
To creates a seamless, "floating" UI over a living map. Users can see friends' locations, status (battery, charging), and interact through ephemeral chats and moments, all without leaving the map context.

## 🛠 Tech Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (Material3)
- **Architecture**: MVVM + Clean Architecture + "MapOS" Pattern
- **Dependency Injection**: Hilt
- **Async**: Coroutines & Flow
- **Backend / BaaS**: Firebase (Auth, Firestore, Storage)
- **Maps**: Google Maps SDK for Android (Compose)
- **Navigation**: Jetpack Navigation Compose
- **Permissions**: Accompanist Permissions (Migration to standard ActivityResultContracts)
- **Image Loading**: Coil
- **Build System**: Gradle (Version Catalogs)

## 📂 Project Structure
```text
com.example.synoptrack
├── core/                # Global infrastructure (Theme, Nav, Utils)
├── auth/                # Authentication & Onboarding (Splash, Login, Permissions)
├── mapos/               # The Core Map Experience (Home Screen)
├── profile/             # User Identity & Settings
├── presence/            # Real-time Location Engine
├── chat/                # Messaging (Floating Overlays)
└── moments/             # Stories & Media
```

## ✨ Key Features
- **MapOS Architecture**: The Map is the root. All other UI (Chats, Profile) floats above it.
- **Smart Onboarding**: Detects new vs. returning users. Frictionless "Education-First" permission flow.
- **Live Presence**: Real-time user status including battery level and activity state.
- **Premium UI**: Glassmorphism, Edge-to-Edge layout, Dark/Light mode support.

## 🔧 Setup
1. Clone the repository.
2. Add your `local.properties`:
   ```properties
   MAPS_API_KEY=your_google_maps_key
   WEB_CLIENT_ID=your_firebase_oauth_client_id
   ```
3. Sync Gradle and Run.

## 🚧 Status
- [x] Basic Auth Flow (Google Sign In)
- [x] Profile Management (Firestore)
- [x] Map Integration (MapOS Shell)
- [x] Permission Handling
- [ ] Real-time Location Sync
- [ ] Chat System
- [ ] Moments/Stories

