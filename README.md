# Whatsupp

Whatsupp is a local-only WhatsApp-inspired chat mockup creator for Android. It is designed for fictional conversations, UI previews and fun mock chats.

## Safety by design

- No INTERNET, SMS, calling, call-log or messaging permissions.
- No message delivery or integration with WhatsApp or other messengers.
- Contacts and conversations are stored locally on the device.
- Contact picker and Android photo picker are user-initiated local selection flows.

## Build

Open with Android Studio or run `gradle testDebugUnitTest assembleDebug` with Android SDK 35 and Java 17.

GitHub Actions builds an installable debug APK artifact on pushes to `main`/`master` and manual dispatch. A signed release APK is published only after the four stable signing secrets listed in `SIGNING_SETUP.md` are configured.
