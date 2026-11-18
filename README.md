# Whatsupp

Whatsupp is an offline Android chat mockup creator. It recreates a WhatsApp-inspired dark interface for fictional conversations and visual demonstrations.

## Features

- Create and edit local contact cards with custom names, phone numbers, profile photos and about text.
- Select a contact to open a separate DM-style screen.
- Create incoming and outgoing simulated messages with editable date/time, status ticks and reply context.
- Edit/delete messages and add/remove reactions.
- Add local image attachments and see media in the contact info screen.
- Contact profile/details, local chat settings, chat search and clear-all-data control.
- Room database persists contacts and conversations locally.

## Safety boundary

This is **not a messenger**. No message can be sent to the real contact. The app does not request Internet, SMS, call, call-log or messaging permissions and does not integrate with WhatsApp. Voice, video and payment buttons are visual mock controls only. Contact/photo selection is user initiated.

## Build

Android Studio or GitHub Actions can build the project with Java 17, Gradle 8.9 and Android SDK 35:

```bash
gradle testDebugUnitTest assembleDebug
```

The `Whatsupp Android APK` GitHub Actions workflow builds an installable debug APK artifact on pushes to `main`/`master` and manual dispatch. Once the four release-signing secrets described in `SIGNING_SETUP.md` are configured, it also publishes a stable-signed `Whatsupp.apk` to the latest GitHub Release.

Package ID: `com.sirrlevi.whatsupp`
