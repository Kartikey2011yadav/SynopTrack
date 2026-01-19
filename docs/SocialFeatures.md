# Social & Connection Features

## Overview

SynopTrack provides multiple robust methods for users to discover and connect with each other, leveraging a Riot ID-style identity system.

## 1. Riot ID Style Search

We separate user identity into **Game Name** and **Tag** (Discriminator).

- **Exact Match**: Users can enter both Name and Tag to find a specific user (e.g., `Kartik` + `#1234`).
- **Uniqueness**: The combination of Name + Tag is globally unique and validated against the backend during profile creation and editing.

## 2. Name Prefix Search

If a user only knows the **Game Name**, they can search using that field alone.

- The system returns users matching the name prefix.
- Useful for browsing common names or finding friends without knowing their exact tag.

## 3. Invite Code Search

Every user has a unique 6-character alphanumeric **Invite Code**.

- Users can enter this code in the search bar.
- This provides a guaranteed exact match without needing special characters.

## 4. QR Code Scanning

- Users can view their **QR Code** on the Profile screen.
- Friends can scan this code (using a system scanner or future in-app scanner) to trigger a direct connection request.

## 5. Deep Linking

- SynopTrack supports the `synoptrack://invite/{code}` custom scheme.
- Sharing this link allows others to click and instantly open an "Add Friend" dialog within the app.
