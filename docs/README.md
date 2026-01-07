# SynopTrack

**SynopTrack** is a next-generation real-time location and presence platform designed to make coordination feel organic, emotional, and efficient. We are moving away from traditional screen-based navigation to an **Engine-based Architecture**, where every UI is a view into a persistent system state.

## 📚 Documentation

- **[Development Plan](DEVELOPMENT_PLAN.md)**: Detailed roadmap of the 7-Phase execution strategy.
- **[Task Flow](TASK_FLOW.md)**: Current status of tasks, bugs, and backlog items.

## 🏗 Architecture

SynopTrack follows a **Clean Architecture** principle with a heavy emphasis on discrete "Engines":

- **Map OS Core**: The operating system of the app. A permanent host screen with floating UI layers.
- **Presence Engine**: Real-time heartbeat, location streaming, and ghost mode.
- **Ephemeral Engine**: Self-destructing chat and media.
- **Convoy Engine**: Distance matrices, ETA sync, and convoy intelligence.
- **Trust Engine**: Privacy, encryption, and safety audits.

## 🚀 Key Features

- **MapOS**: A state-based navigation system where the map is the background of everything.
- **Live Presence**: See friends move in real-time with "Ghost Mode" privacy defaults.
- **Ephemeral Chat**: Conversations that disappear, making interactions feel lightweight.
- **Moments**: 24h location-tagged stories.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVI / Clean Architecture
- **State Management**: approaches tailored to "Global UI State Store"
- **Backend (Planned)**: RTDB / Firestore / WebSockets
