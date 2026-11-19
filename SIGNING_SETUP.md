# Stable APK signing

Android only lets an APK update an installed app when both APKs are signed with the same key. The workflow therefore
signs every build with one persistent key and never generates a throw-away key per run.

## How the key is kept (no setup needed)

1. If the four repository secrets below exist, the workflow uses them.
2. Otherwise, on the first run it creates a random key and keeps it in a release named `signing-key-private` of this
   **private** repository (not in the source tree; `*.jks`/`*.keystore` are git-ignored). Later runs restore the same key.
3. If the repository is ever made **public**, the workflow refuses to use that release and fails with a message, because
   the key would be exposed. Switch to secrets before making the repository public.

## Optional: use repository secrets instead

Settings → Secrets and variables → Actions → New repository secret:

- `ANDROID_KEYSTORE_BASE64` – the keystore file, Base64 on one line
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Important: switching from the release-kept key to a different key changes the signature, and Android will then refuse to
update an app that was installed with the old one (a one-time uninstall is needed). Keep whichever key you start with.
Never commit the keystore, its Base64 text or the passwords to the repository.
