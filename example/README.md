## RUN Android
> npx react-native run-android

## Buid APK
> react-native bundle --platform android --dev false --entry-file index.tsx --bundle-output android/app/src/main/assets/index.android.bundle --assets-dest android/app/src/main/res

> cd android
> ./gradlew assembleDebug
