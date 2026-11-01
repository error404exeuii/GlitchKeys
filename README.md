# GlitchKeys

GlitchKeys is an open-source Android keyboard starter with a Unicode corruption engine and custom font preview/import.

## Features in this starter

- Android Input Method Service (IME) with basic letter, space, delete and enter keys.
- Toggle for Zalgo-style Unicode combining-mark output while typing.
- Corrupted-text generator with adjustable intensity in the companion activity.
- Import `.ttf` and `.otf` files through Android's document picker.
- Preview imported fonts inside GlitchKeys.
- GitHub Actions workflow that builds a debug APK and publishes it as a downloadable workflow artifact.

## Requirements

- Android Studio/compatible Gradle environment, or GitHub Actions cloud build.
- JDK 17.
- Android SDK platform 35 and build-tools 35.0.0.
- Gradle 8.9+ (the workflow uses the Gradle version provided by the runner).

## Build on GitHub

1. Create a **public** repository named `GlitchKeys`.
2. Upload the contents of this project ZIP to the repository root (not the ZIP file itself).
3. Commit the files to the `main` branch.
4. Open **Actions** → **Build GlitchKeys APK**.
5. Wait for the workflow to finish successfully.
6. Open the completed workflow run and download `GlitchKeys-debug-apk` under **Artifacts**.
7. Extract the downloaded artifact ZIP to get `app-debug.apk`.

You can also start a build from **Actions → Build GlitchKeys APK → Run workflow**.

## Install and enable

1. Install the debug APK on your test phone.
2. Open GlitchKeys and tap **Enable GlitchKeys keyboard**.
3. Enable GlitchKeys in Android's input-method settings.
4. Return to GlitchKeys and tap **Open keyboard picker**, then select GlitchKeys.
5. Test it in a text field.

Android displays a warning when enabling third-party keyboards. This app starter does not send keystrokes over the network.

## Custom fonts

Use **Import custom .ttf / .otf font**, choose a font file, then tap **Choose imported font** to preview it. Font files are stored in the app's private internal storage. Imported fonts affect GlitchKeys' own preview for now; they cannot force other apps to use that font.

## Unicode corruption

The engine inserts Unicode combining marks. Display differs between apps and fonts. Excessive marks can make text difficult to read or affect cursor/editing behavior, so the intensity is capped.

## Current limitations

This is a starter, not a production keyboard:
- The keyboard layout is intentionally basic.
- The current Glitch toggle exits the keyboard view to keep the first IME implementation simple; it will be replaced with an in-keyboard state update.
- Shift, symbols, suggestions, autocorrect, multilingual layouts, and persistent font selection are not implemented yet.
- Only debug APKs are produced; release signing is not configured.
- Android 10/API 29 is the minimum supported version.

## License

MIT. See [LICENSE](LICENSE).
