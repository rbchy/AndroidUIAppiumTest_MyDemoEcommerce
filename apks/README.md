# apks/

Drop local APK files here if you want tests to perform a fresh install:

- `MyDemoApp.apk` — used by `MyDemoBaseTest.java` (MyDemo suite)
- `ApiDemos-debug.apk` — used by `BaseTest.java`, `InstallLifecycleTests.java`,
  `ApiDemosTest.java`, `FirstAppiumTest.java` (ApiDemos suite)

If a file isn't present, the tests fall back to `noReset: true` and simply
launch whatever is already installed on the connected device/emulator —
so this folder can stay empty as long as the app is pre-installed.

APK files are gitignored; download links:
- MyDemo App: https://github.com/saucelabs/my-demo-app-android/releases
- ApiDemos: https://github.com/appium/android-apidemos
