# Salaam Compass (Android)

A Qibla compass for Android. It works offline and only needs location access.

## Features
- **Works offline.** The Qibla bearing comes from a great-circle calculation, and GPS gives your position without internet.
- **Location options:**
  - **Current location** is the default. The app asks for location permission, and if location is switched off it takes you to Settings to turn it on.
  - **50 major cities** are built into the app, so you can pick one with no internet.
  - **Any city or town worldwide** can be found with online search (OpenStreetMap Nominatim).
  - Your choice is saved between launches.
- **Steady, trustworthy needle:**
  - It uses the gyro-assisted rotation-vector sensor, and falls back to accelerometer + magnetometer on phones without one.
  - Readings are smoothed on the circle, so the needle never spins from 359° to 0°. A 1° dead band stops jitter.
  - Magnetic declination is corrected, so the needle points to true north, not magnetic north.
  - When a reading is unreliable, the needle turns grey and the app tells you what to fix. This covers:
    - an uncalibrated compass (asks for a figure-8 motion)
    - magnetic interference (field strength differs from Earth's expected field at your location)
    - the phone held too steeply (the heading freezes instead of swinging).
  - You get a haptic "facing the Qibla" confirmation within ±3°.

## Build
Needs JDK 17 and the Android SDK (API 35).

```
cd android
./gradlew testDebugUnitTest assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

The `Android` GitHub Actions workflow builds the debug APK on every push. You can download it from the run's artifacts.
