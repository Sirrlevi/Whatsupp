# Stable APK signing setup

Android only permits an APK to update an installed app when both APKs are signed with the same key. A persistent release keystore has been generated separately in **Whatsupp-signing-kit.zip**; it is not stored in this project and must never be committed to Git.

1. Download and extract `Whatsupp-signing-kit.zip` somewhere private. Keep `whatsupp-release.jks` and the password details as a secure backup.
2. Open `GITHUB_SECRETS.txt` in the extracted kit. It contains the exact secret names and passwords. Open `keystore-base64.txt` and copy its entire one-line contents.
3. In your GitHub repository, open **Settings → Secrets and variables → Actions → New repository secret** and add all four values:
   - `ANDROID_KEYSTORE_BASE64`: the complete one-line content of `keystore-base64.txt`.
   - `ANDROID_KEYSTORE_PASSWORD`: the value shown in `GITHUB_SECRETS.txt`.
   - `ANDROID_KEY_ALIAS`: `whatsupp-release`.
   - `ANDROID_KEY_PASSWORD`: the value shown in `GITHUB_SECRETS.txt`.
4. Keep the same keystore and values for every future release. Never upload the raw `.jks` file, Base64 text, or password file to the repository.
5. Run the `Whatsupp Android APK` workflow on `main` or `master`. It uploads a debug APK artifact and publishes the stable-signed release APK in a versioned GitHub Release marked as the latest release.

Without the secrets, CI still tests and uploads a debug APK, but deliberately skips Release publishing. GitHub-hosted runners are temporary; generating a new release key on every run would break install-over-existing updates.
