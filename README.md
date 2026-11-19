# Whatsupp

Whatsupp is an offline Android **prank / chat-generator** app. It lets you build chat conversations for fun using a
WhatsApp-style dark interface. Every screen carries a visible **PRANK** marking: a header label, a footer label and a
large diagonal "PRANK" watermark drawn above the chat wallpaper (it cannot be switched off and does not depend on the
wallpaper or theme).

## Features

- Create and edit local contact cards with custom names, phone numbers, profile photos and about text.
- Select a contact to open a separate DM-style screen.
- Create incoming and outgoing messages with editable date/time, status ticks and reply context.
- Edit/delete messages and add/remove reactions; add several messages at once.
- Add local image/video/audio attachments; media shows up in the contact info screen.
- Dark/light/system theme, a default wallpaper and a wallpaper per chat.
- Contact profile/details, chat search, "Save chat image" and a clear-all-data control.
- Room database keeps contacts and conversations on the device between launches.
- In-app updates: a newer build found on GitHub is offered inside the app and installs over the existing one.

## Safety boundary

This is **not a messenger**. No message can be sent to the real contact. The app does not request SMS, call, call-log or
messaging permissions and does not integrate with WhatsApp. Voice, video and payment buttons are visual controls only.
The only network use is the update check against this repository's GitHub releases (code lives only in
`update/`; CI fails the build if network code appears anywhere else, if the PRANK labels are removed, or if a
messaging/calling permission is added).

## In-app updates (private repository)

Releases of a private repository need a token. Once, in **Settings → App updates**, paste a fine-grained GitHub token
(Resource owner `Sirrlevi`, only the `Whatsupp` repository, **Contents: Read-only**). It is stored encrypted with
Android Keystore and only sent to GitHub. After that, every new build shows an update dialog with **Download & install**.

## Build

Android Studio or GitHub Actions can build the project with Java 17, Gradle 8.9 and Android SDK 35:

```bash
gradle testDebugUnitTest assembleRelease
```

The `Whatsupp Android APK` workflow runs on every push to `main`/`master` and on manual dispatch. It runs the unit tests
and the safety/PRANK checks, builds a release APK signed with one stable key (so every build installs over the previous
one), uploads it as a workflow artifact and publishes it to the rolling `latest-apk` GitHub Release together with
`whatsupp-update.json`, which the in-app updater reads. Version code/name are set automatically from the run number.
See `SIGNING_SETUP.md` for how the signing key is kept.

Direct download of the newest APK: `https://github.com/Sirrlevi/Whatsupp/releases/download/latest-apk/Whatsupp.apk`
(private repository: open it while signed in to GitHub).

Package ID: `com.sirrlevi.whatsupp`
