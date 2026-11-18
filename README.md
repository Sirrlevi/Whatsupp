# Whatsupp

Whatsupp is an offline Android chat mockup studio for creating fictional WhatsApp-inspired conversations. It is not a messenger.

## Project source

The complete Android Studio project is included in [Whatsupp-Android-Project.tar.xz](Whatsupp-Android-Project.tar.xz). The GitHub Actions workflow extracts the archive in the runner before testing and building. To inspect locally, extract the archive with any XZ/TAR utility.

## Safety design

- No INTERNET, SMS, phone-call, call-log or message-sending permissions.
- No integration with WhatsApp, SMS, RCS or any messaging provider.
- Contacts, chats, edits, reactions and attachments are local-only.
- Phone contacts and media are imported only through explicit Android system pickers.
- Call, video and payment icons are decorative mock controls.

## CI

Pushes to main/master and manual workflow dispatch run unit tests and build a debug APK. The debug APK is uploaded as a GitHub Actions artifact. A signed Release APK is published only after the four secrets listed in SIGNING_SETUP.md are configured:

- ANDROID_KEYSTORE_BASE64
- ANDROID_KEYSTORE_PASSWORD
- ANDROID_KEY_ALIAS
- ANDROID_KEY_PASSWORD

Never commit the signing keystore or passwords. Keep the same keystore for every future install-over-existing update.
