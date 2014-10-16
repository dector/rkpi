__Radio KPI Android player__

Code licenced under [MIT Licence](opensource.org/licenses/MIT).

All art belongs to [Belka & Parovoz](vk.com/belkaiparovoz). Please contact creators for additional information.

Build instructions
------------------

  - Copy file `app/secure_template.gradle` to `app/secure.gradle`.
  - (Optional) Edit values in `app/secure.gradle`.
  - Build app (Linux, MacOS): `./gradlew assembleDebug`.
  - Build app (Windows): `gradle.bat assembleDebug`.
  - Install apk: `adb install app/build/outputs/apk/app-debug-unaligned.apk`.