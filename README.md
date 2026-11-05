# Shinchan Pet — Android project

A lightweight, offline floating desktop-pet prototype. The character is drawn locally with Android Canvas; no network access is needed by the app itself.

## Build an APK without Android Studio (GitHub Actions)

1. Sign in to GitHub and create a **new private repository**.
2. Upload the contents of this `ShinchanPet` folder to the repository (make sure `.github/workflows/build-apk.yml` is included).
3. Open the repository's **Actions** tab. If prompted, enable workflows.
4. Select **Build Shinchan Pet APK** and tap **Run workflow** (or push to the `main` branch).
5. Wait for the workflow to finish successfully.
6. Open the completed workflow run. Under **Artifacts**, download `ShinchanPet-debug-apk`.
7. Extract the downloaded artifact ZIP. It contains `app-debug.apk`. Transfer it to your Poco X6 Pro and install it. If Android asks, allow your file manager to install unknown apps.

The workflow uses GitHub-hosted build machines to download Gradle and the Android SDK, then compiles the debug APK. You do not need Android Studio on your own computer or phone. Keep the repository private if you prefer not to publish the source publicly.

## Build locally (optional)

Open this folder in Android Studio with Android SDK Platform 35 installed, sync Gradle, then choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**. The APK is at `app/build/outputs/apk/debug/app-debug.apk`.

## First launch

1. Open **Shinchan Pet**.
2. Tap **LET SHINCHAN OUT**.
3. Enable **Display over other apps** for Shinchan Pet when Android opens Settings.
4. Return to the app and tap the button again.
5. Drag the pet to move him. Tap him to cycle through reactions. Use **Put Shinchan away** to stop the pet.

## Included prototype features

- Floating overlay above the launcher and other apps.
- Character drawn locally with Canvas; visible character is 32 × 40 dp, centered in a 48 × 56 dp touch target.
- Idle bobbing, blinking, tap moods and prewritten offline reactions.
- Battery-low reaction.
- Foreground notification while active.
- Boot receiver attempts to restart the pet after reboot, if overlay access remains enabled.

## Notes

- This is a first prototype, not an AI assistant. Dialogues and reactions are prewritten and offline.
- Android/HyperOS may restrict background starts or auto-start. If the pet does not return after reboot, open the app and start it again; enable Autostart/background activity for the app in HyperOS settings if available.
- The app requires the Android "Display over other apps" permission. You can revoke it at any time in Settings.
