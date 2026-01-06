# SynopTrack 📍

**SynopTrack** is a Location-Based Social Network that turns a static map into a "Living Map" of your social circle. Think "Google Maps meets Snapchat." It bridges the gap between navigation and social connection, allowing communities to co-exist on a shared map with real-time context.

## 🎯 Core Goal

To foster a sense of belonging by visualizing where friends are and *what they are experiencing* in real-time. It removes the friction of "texting to ask" and replaces it with spontaneous visual connection.

## 🌟 Key Features

*   **Contextual Social Sharing**: Tap a friend’s avatar to see their "Status" (photo/video). It’s not just a location; it’s an emotional update.
*   **Ephemeral "Moments"**: Media shared on the map lasts 24 hours, encouraging raw, unfiltered updates.
*   **Community & Group Dynamics**: Distinct social circles (Community, Family, Best Friends) with granular privacy controls.
*   **Real-Time Connection**: "Live Party Mode" for close friends, "City-Level Presence" for acquaintances.
*   **Flexible Privacy**: "Ghost Mode" (invisible), "Blurred" (approximate), or "Live" (precise).

## 🛠️ Tech Stack

We use **Modern Android Development (MAD)** standards:

*   **Language**: Kotlin
*   **UI**: Jetpack Compose (Material3)
*   **Architecture**: MVVM + Clean Architecture
*   **Dependency Injection**: Hilt
*   **Backend**: Firebase (Auth, Firestore, Realtime Database, Storage, Analytics)
*   **Maps**: Google Maps SDK for Android (Maps Compose)
*   **Async**: Coroutines & Flow
*   **Image Loading**: Coil
*   **Permissions**: Accompanist

## 📂 Project Structure

The project follows a feature-based Clean Architecture:

*   `app/src/main/java/com/example/synoptrack/`
    *   `auth/`: Authentication logic
    *   `mapos/`: Map interactions and rendering
    *   `moments/`: Ephemeral media handling
    *   `presence/`: Real-time location and status updates
    *   `profile/`: User profile management
    *   `core/`: Shared utilities and base classes
    *   `di/`: Hilt modules

## 🚀 Setup & Build

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/Kartikey2011yadav/SynopTrack.git
    ```
2.  **Firebase Setup**:
    *   Place your `google-services.json` file in the `app/` directory.
3.  **Google Maps API Key**:
    *   Ensure your `local.properties` contains your Maps API key:
        ```properties
        MAPS_API_KEY=your_api_key_here
        ```
4.  **Build**:
    *   Open in Android Studio (Koala or later recommended).
    *   Sync Gradle.
    *   Run on an emulator or API 30+ device.

## 🚧 Current Status

**Current Phase: Stage 2**
Refining the "Social/Home" UI to ensure a premium, immersive experience (Glassmorphism, floating stories, polished animations).

---
*Built with ❤️ by the SynopTrack Team*
