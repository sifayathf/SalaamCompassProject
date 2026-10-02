#!/usr/bin/env bash
# Installs the debug APK on a running emulator, walks through the main screens
# and saves screenshots to ./screenshots. Used by .github/workflows/android-emulator.yml.
set -uo pipefail

OUT=screenshots
APK=android/app/build/outputs/apk/debug/app-debug.apk
PKG=com.salaam.compass
mkdir -p "$OUT"

shot() { sleep "${2:-3}"; adb exec-out screencap -p > "$OUT/$1.png"; echo "saved $1.png"; }

# Taps the centre of the first on-screen element whose text is exactly $1.
tap_text() {
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null
  adb pull /sdcard/ui.xml /tmp/ui.xml >/dev/null
  local b
  b=$(grep -o "text=\"$1\"[^>]*bounds=\"\[[0-9]*,[0-9]*\]\[[0-9]*,[0-9]*\]\"" /tmp/ui.xml | head -1 |
      sed -E 's/.*bounds="\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\]"/\1 \2 \3 \4/')
  if [ -z "$b" ]; then echo "WARN: '$1' not on screen"; return 1; fi
  read -r x1 y1 x2 y2 <<< "$b"
  adb shell input tap $(( (x1 + x2) / 2 )) $(( (y1 + y2) / 2 ))
}

adb install -r "$APK"
adb logcat -c
adb shell cmd location set-location-enabled true
adb emu geo fix 80.2707 13.0827   # Chennai (lon lat)

# 1. First launch: the app asks for location permission.
adb shell am start -n "$PKG/.MainActivity"
shot 01-permission-prompt 5

# 2. Allow it; the compass shows the Qibla from the current (GPS) location.
tap_text "While using the app" || adb shell pm grant "$PKG" android.permission.ACCESS_FINE_LOCATION
for _ in 1 2 3; do adb emu geo fix 80.2707 13.0827; sleep 2; done
shot 02-compass-current-location

# 3. Location chooser: current location, online search, 50 offline cities.
tap_text "Current location"
shot 03-city-picker

# 4. Typing filters the offline list.
tap_text "City or town"
adb shell input text "Lon"
shot 04-offline-search

# 5. Pick London.
tap_text "London"
shot 05-compass-london

adb logcat -d -b crash > "$OUT/crash-log.txt"
if adb shell pidof "$PKG" >/dev/null; then
  echo "App still running."
else
  echo "App is not running - see crash-log.txt"; cat "$OUT/crash-log.txt"; exit 1
fi
