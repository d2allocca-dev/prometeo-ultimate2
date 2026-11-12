# Build APK with GitHub Actions

## Upload
1. Create a private repository on GitHub.
2. Extract this ZIP on your PC.
3. Open the extracted folder `PROMETEO_ULTIMATE_MULTIPLATFORM`.
4. Upload the *contents* of that folder to the repository root (so `settings.gradle.kts` is at repository root).
5. Commit to branch `main`.

## Start / retrieve build
- Open the repository's **Actions** tab.
- Select **Build PROMETEO ULTIMATE APK**.
- If needed, choose **Run workflow**.
- Wait for the job to finish.
- Open the successful run and download the artifact named `PROMETEO-ULTIMATE-debug-apk`.

## Important
- This workflow creates a DEBUG APK for testing, not a signed production release.
- GitHub Actions compiles the source; it does not prove the app is functionally secure or that external integrations work.
- The current project has no Gradle wrapper, so this workflow installs Gradle 8.9 on the GitHub runner.
- If the build fails, download the build log and share it for diagnosis.
- Never commit passwords, secret phrases, API keys, signing keys, or personal credentials.
