# Pinpoint 📍

**Pinpoint** is a privacy-first, minimalist Android geocoding tool designed for users who need precise "rooftop" coordinates without trading away their personal data.

Unlike standard map apps that track your location history, Pinpoint operates on a **"Ghost Protocol"**: it retains zero data, uses anonymously queried commercial-grade maps (ArcGIS), and actively prevents your keyboard from learning your input.



## 🚀 Features

*   **Commercial-Grade Precision**: Utilizes **ArcGIS (Esri)** geocoding services for verified, rooftop-level accuracy.
*   **Privacy by Design**:
    *   **RAM-Only State**: No databases or SharedPrefs. Data is wiped instantly on exit or via the "Kill Switch".
    *   **Anonymous Queries**: Uses public, keyless API endpoints. No user accounts, no tracking ids.
    *   **Incognito Input**: Forces keyboards into "incognito mode" to prevent predictive text learning.
    *   **Zero-Cloud Backup**: Explicitly blocks Android Auto-Backup to Google Drive.
*   **Minimalist "Stealth" UI**: Dark monochrome theme with high-contrast visibility.
*   **F-Droid Ready**: Free and Open Source Software (FOSS), dependent only on standard libraries.

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **UI**: Jetpack Compose (Material3)
*   **Architecture**: MVVM with `StateFlow`
*   **Networking**: Retrofit / OkHttp
*   **Concurrency**: Coroutines

## 📦 Installation

### Download APK
Check the [Releases](https://github.com/yourusername/Pinpoint/releases) page for the latest `.apk`.

### Build from Source
To build the app for yourself:

1.  Clone the repository:
    ```bash
    git clone https://github.com/Evso1/Pinpoint.git
    ```
2.  Open in **Android Studio**.
3.  Run the build command:
    ```bash
    ./gradlew assembleDebug
    ```
4.  Install on your device via ADB:
    ```bash
    adb install app/build/outputs/apk/debug/app-debug.apk
    ```

## 🔒 Privacy Policy

Pinpoint collects **no data**.
*   We do not use analytics (Firebase, Crashlytics, etc.).
*   We do not use advertising IDs.
*   We do not require Location permissions (you input addresses manually).
*   Address queries are sent directly to ArcGIS commercial endpoints and are subject to their [terms](https://www.esri.com/en-us/legal/privacy), but are performed anonymously without user-identifiable tokens.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
