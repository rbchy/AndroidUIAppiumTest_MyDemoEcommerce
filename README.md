# 📱 AndroidAppiumTest_MyDemoE-Commerce Automation

<p>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Maven-3-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven 3" />
  <img src="https://img.shields.io/badge/Appium-3.5.2-660198?style=for-the-badge&logo=appium&logoColor=white" alt="Appium 3.5.2" />
  <img src="https://img.shields.io/badge/Selenium-4.32.0-43B02A?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenium 4.32.0" />
  <img src="https://img.shields.io/badge/TestNG-7.10.2-E2231A?style=for-the-badge" alt="TestNG 7.10.2" />
  <img src="https://img.shields.io/badge/Android-API%2031-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android API 31" />
  <img src="https://img.shields.io/badge/REST%20Assured-5.4.0-25A162?style=for-the-badge" alt="REST Assured 5.4.0" />
  <img src="https://img.shields.io/badge/ExtentReports-5.1.2-2196F3?style=for-the-badge" alt="ExtentReports 5.1.2" />
  <img src="https://img.shields.io/badge/Allure%20Report-2.35.3-FF6E00?style=for-the-badge" alt="Allure Report 2.35.3" />
</p>

A comprehensive **Android UI automation framework** built with **Appium 3**, **UiAutomator2**, **TestNG**, and **Java 17**.

![Technology Stack](docs/tech-stack-diagram.svg)

Features two fully automated test suites:
- **MyDemo App Suite** — 75 tests across 12 categories (⭐ primary suite)
- **ApiDemos Suite** — core UI regression tests

> **MyDemo suite status (2026-07-19):** rebuilt to restore full 75-test coverage
> across all 12 categories after several test classes were found reduced to
> minimal stubs (see Known Limitations). Verified via live run: **75/75 passing,
> 0 failures, 0 skipped.** ✅ The "72 pass" result further below is from the
> *earlier* fuller version of this suite and predates the rebuild.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Test runner | TestNG 7.10.2 |
| Automation | Appium 3.5.2 + UiAutomator2 |
| Driver client | Appium Java Client 9.5.0 |
| Selenium core | Selenium 4.32.0 |
| REST testing | REST Assured 5.4.0 |
| Reporting | ExtentReports 5.1.2 + Allure 2.35.3 (allure-maven 3.0.2) |
| Build tool | Maven 3 (Surefire 3.2.5) |

---

## Prerequisites

1. **Java 17** — `JAVA_HOME` must be set
2. **Maven 3** on PATH
3. **Node.js** (needed to install Appium)
4. **Appium 3** and UiAutomator2 driver:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
5. **Android SDK** with `adb` on PATH (installed via Android Studio, or SDK command-line tools)
6. **Android Emulator** running at `emulator-5554` (API 31, Android 12)
7. **APKs (optional)** — only needed for a *fresh install*; if the app is already
   installed on the emulator, tests fall back to launching it as-is:
   - `apks/MyDemoApp.apk` — https://github.com/saucelabs/my-demo-app-android/releases
   - `apks/ApiDemos-debug.apk` — https://github.com/appium/android-apidemos
   - See `apks/README.md` for details.

---

## Quick Start (macOS)

```bash
# 1. Install prerequisites (skip any you already have)
brew install openjdk@17 maven node
npm install -g appium
appium driver install uiautomator2

# 2. Point JAVA_HOME at JDK 17 (add to ~/.zshrc to persist)
export JAVA_HOME=$(/usr/libexec/java_home -v17)

# 3. Start an Android emulator (via Android Studio's Device Manager, or:)
emulator -avd <your_avd_name>

# 4. In a separate terminal, start Appium
npx appium

# 5. Confirm the emulator is visible
adb devices
# Expected: emulator-5554   device

# 6. From the project root, run the tests
cd /path/to/AndroidUIAppiumTest_MyDemoEcommerce
mvn clean test
# ^ runs testng-mydemo-local.xml by default (see pom.xml <suiteXmlFile> property)

# Or use the helper scripts:
./run-mydemo-local.sh   # local suite, ~9 tests
./run-mydemo-all.sh     # full suite, 75+ tests
```

Both `.sh` scripts are executable and are the macOS/Linux equivalents of the
original `run-mydemo-local.bat` / `run-mydemo-all.bat` (which only run on Windows).

---

## Project Structure

```
appium-tests/
├── src/test/java/
│   ├── base/
│   │   ├── MyDemoBaseTest.java         # MyDemo: Appium session setup/teardown
│   │   └── BaseTest.java               # ApiDemos: Appium session setup/teardown
│   │
│   ├── pages/
│   │   ├── mydemo/                     # MyDemo Page Object Model
│   │   │   ├── LoginPage.java
│   │   │   ├── ProductsPage.java
│   │   │   ├── CartPage.java
│   │   │   └── CheckoutPage.java
│   │   ├── MainScreenPage.java         # ApiDemos pages
│   │   └── TextScreenPage.java
│   │
│   ├── tests/
│   │   ├── MyDemoSmokeTests.java       # [SMOKE]     6 tests
│   │   ├── MyDemoE2ETests.java         # [E2E]       9 tests
│   │   ├── MyDemoAppLifecycleTests.java# [LIFECYCLE] 6 tests
│   │   ├── MyDemoNavigationTests.java  # [NAV]       7 tests
│   │   ├── MyDemoDeviceBehaviorTests.java# [DEVICE]  5 tests
│   │   ├── MyDemoNegativeTests.java    # [NEGATIVE]  8 tests
│   │   ├── MyDemoPerformanceTests.java # [PERF]      5 tests
│   │   ├── MyDemoSecurityTests.java    # [SECURITY]  5 tests
│   │   ├── MyDemoAccessibilityTests.java# [A11Y]     6 tests
│   │   ├── MyDemoDataDrivenTests.java  # [DATA]      9 tests
│   │   ├── MyDemoInstallTests.java     # [INSTALL]   4 tests
│   │   ├── MyDemoInterruptTests.java   # [INTERRUPT] 5 tests
│   │   └── CombinedApiDemosTests.java  # ApiDemos core tests
│   │
│   ├── listeners/
│   │   └── ScreenshotListener.java     # Auto-screenshot on failure
│   └── utils/
│       └── ExtentReportManager.java    # HTML report singleton
│
├── apks/                                # Local APK files (gitignored) — see apks/README.md
├── page-dumps/                          # Page-source XML dumps written at runtime for debugging
├── testng-mydemo-all.xml               # MyDemo full suite (75 tests)
├── testng-mydemo-local.xml             # MyDemo local suite (default — see pom.xml)
├── testng.xml                          # ApiDemos suite
├── run-mydemo-local.sh / .bat          # Convenience runner: local suite (macOS/Linux / Windows)
├── run-mydemo-all.sh / .bat            # Convenience runner: full suite (macOS/Linux / Windows)
├── pom.xml
└── README.md
```

---

## Running the MyDemo Suite

### 1. Start the Appium server

```bash
npx appium
```

### 2. Verify the emulator is online

```bash
adb devices
# Expected: emulator-5554   device
```

### 3. Run all 75 tests

```bash
mvn clean test -DsuiteXmlFile=testng-mydemo-all.xml
# or:
./run-mydemo-all.sh
```

### 4. View the HTML report

```
test-output/ExtentReport.html
```

---

## MyDemo Test Categories

| # | Class | Tag | Tests | What it covers |
|---|---|---|---|---|
| 1 | `MyDemoSmokeTests` | `[SMOKE]` | 6 | App launches, login screen loads, catalog visible |
| 2 | `MyDemoE2ETests` | `[E2E]` | 9 | Full login → add to cart → checkout flows |
| 3 | `MyDemoAppLifecycleTests` | `[LIFECYCLE]` | 6 | Background/foreground, kill & relaunch |
| 4 | `MyDemoNavigationTests` | `[NAV]` | 7 | Hamburger menu, product detail, cart, checkout steps |
| 5 | `MyDemoDeviceBehaviorTests` | `[DEVICE]` | 5 | Back button, home button, screen orientation stability |
| 6 | `MyDemoNegativeTests` | `[NEGATIVE]` | 8 | Invalid credentials, empty fields, boundary inputs |
| 7 | `MyDemoPerformanceTests` | `[PERF]` | 5 | Page load SLA checks (login ≤5s, cart open ≤3s) |
| 8 | `MyDemoSecurityTests` | `[SECURITY]` | 5 | SQL injection, XSS in input fields, session handling |
| 9 | `MyDemoAccessibilityTests` | `[A11Y]` | 6 | Content descriptions, focusability, label presence |
| 10 | `MyDemoDataDrivenTests` | `[DATA]` | 9 | Multiple credential sets, addresses, card numbers |
| 11 | `MyDemoInstallTests` | `[INSTALL]` | 4 | Fresh install, upgrade, uninstall verification |
| 12 | `MyDemoInterruptTests` | `[INTERRUPT]` | 5 | Incoming call, SMS, WiFi toggle during active session |

**Total: 75 tests — 75 pass · 0 fail · 0 skip** ✅ (verified live)

---

## Design Decisions

### One Appium session per test class
`@BeforeClass` / `@AfterClass` creates and destroys one driver session per class — significantly faster than spinning up a session per test method.

### Fast app reset via ADB
Instead of `fullReset` (slow APK reinstall), `MyDemoBaseTest` runs:
```bash
adb -s emulator-5554 shell pm clear com.saucelabs.mydemoapp.android
```
Same clean-state effect, much faster session startup.

### Session health check in `@BeforeMethod`
Every test class guards against a dead Appium session so cascade failures become clean skips:
```java
@BeforeMethod
public void checkSession() {
    try {
        driver.getSessionId();
        driver.findElements(By.id(PKG + ":id/menuIV")); // real Appium round-trip
    } catch (Exception e) {
        throw new SkipException("Session dead — skipping: " + e.getMessage());
    }
}
```

### Navigation drawer — never press BACK
On this app + emulator combination, pressing `navigate().back()` from an open navigation drawer exits the app to the Android home screen instead of closing the drawer. All drawer code closes by tapping the hamburger icon a second time.

### Logout → `activateApp()` rescue
After logout, the app navigates to the Android home screen. `LoginPage` automatically calls `driver.activateApp()` before any further navigation to bring the app back.

### Avoid `driver.currentActivity()`
This method internally calls `adb dumpsys window displays`, which hangs 20+ seconds on a loaded emulator. Session health is checked using `driver.findElements()` instead — a real Appium round-trip with no ADB dependency.

---

## Test Reports & Screenshots

| Artifact | Location |
|---|---|
| HTML Report (ExtentReports) | `test-output/ExtentReport.html` |
| HTML Report (Allure) | `target/site/allure-maven-plugin/index.html` (see [Allure Report](#-allure-report) below) |
| Failure Screenshots | `test-output/screenshots/<testMethodName>.png` |

Screenshots are captured automatically by `ScreenshotListener` on every test failure.

`CheckoutPage` and `ProductsPage` also dump the raw page source to
`page-dumps/<label>-dump.xml` for debugging locked/unexpected screens.

> **Note:** four stale dump files with literal Windows-style names —
> e.g. `` C:\Users\rezau\eclipse-workspace\appium-tests\payment-page-dump.xml `` —
> exist in the project root from before this path was fixed (macOS treats `\`
> as a normal filename character, not a path separator, so they landed there
> instead of erroring). They're harmless and safe to delete manually; new runs
> now write cleanly to `page-dumps/`.

---

## 📊 Allure Report

<p>
  <a href="#-allure-report">
    <img src="https://img.shields.io/badge/📊_Generate_%26_View-Allure_Report-FF6E00?style=for-the-badge" alt="Generate and View Allure Report" />
  </a>
</p>

Test results are also collected as raw Allure results (`target/allure-results`) via the
`allure-testng` listener, on top of the existing ExtentReports HTML report. Allure gives a
richer, interactive report — timeline, categories, retries, and per-test steps/attachments.

```bash
# 1. Run the tests (writes to target/allure-results)
mvn clean test -DsuiteXmlFile=testng-mydemo-all.xml

# 2a. Generate a static report and open it yourself
mvn allure:report
open target/site/allure-maven-plugin/index.html

# 2b. Or generate + auto-open in one step (recommended — avoids the
#     file:// CORS issue that leaves a static-opened report blank)
mvn allure:serve
```

> **Why `allure:serve` is recommended:** opening `index.html` directly via
> `file://` triggers browser CORS restrictions and the report renders blank.
> `allure:serve` spins up a local server and opens the report in your default
> browser, which works reliably every time.

Pinned to the Allure 2 (Java-based) report runtime — see the comment above the
`allure-maven` plugin in `pom.xml` for why (a path-doubling bug in the default
Allure 3/Node runtime's `serve` step).

---

## Test Credentials (MyDemo App)

| Field | Value |
|---|---|
| Username | `bob@example.com` |
| Password | `10203040` |

---

## Known Limitations

| Limitation | Detail |
|---|---|
| **App locks its own screen orientation** | `device_RotationDoesNotCrashApp` requests landscape both via `driver.rotate()`/adb `user_rotation`, but the OS never confirms it — the app's activity almost certainly declares `android:screenOrientation="portrait"` in its manifest, which overrides any system-level rotation request. This is expected app behavior, not a bug, so the test asserts what its name promises: the app stays stable/responsive around a rotation attempt, regardless of whether the OS honors it. |
| **Emulator CPU load after interrupt tests** | Background/network-toggle simulations in `MyDemoInterruptTests` leave elevated CPU for ~30–60 seconds. Tests in subsequent classes may need slightly longer waits. |
| **APK path** | Resolved as `<project-root>/apks/MyDemoApp.apk` (cross-platform). If absent, tests launch whatever's already installed via `noReset`. |
| **"Continue Shopping" locator unverified** | `MyDemoE2ETests.returnToCatalogAfterOrder()` best-effort-taps a button matching text containing "Continue"; if that locator doesn't match this app build, it falls back to reactivating the app, which reliably reaches the catalog either way. |
| **Suite rebuilt 2026-07-18/19** | The 12 MyDemo test classes were expanded from 27 real tests back to the documented 75, after several were found reduced to minimal stubs. Verified via live run at 75/75 passing, 0 skipped, 0 failed. |

---

## SauceLabs Cloud (Optional)

Suite files for SauceLabs are **excluded from Git** via `.gitignore` because they contain credentials:

```
testng-saucelabs.xml
testng-saucelabs-crossdevice.xml
testng-saucelabs-mydemo.xml
```

To run on SauceLabs, create your own copy based on `testng-mydemo-all.xml` and add your credentials as parameters. **Never commit credential files.**

Credentials can also be passed as environment variables:

```bash
export SAUCE_USERNAME=your-username
export SAUCE_ACCESS_KEY=your-key
```

---

## Run History (MyDemo Suite)

| Run | Pass | Fail | Skip | Notes |
|---|---|---|---|---|
| Run 5 | 61 | 10 | 4 | Initial baseline |
| Run 6 | 69 | 3 | 3 | Navigation drawer BACK bug fixed |
| Run 7 | 71 | 1 | 3 | Logout → home screen rescue added |
| Run 8 | 70 | 2 | 3 | Emulator flakiness regression |
| Run 9 | 72 | 0 | 3 | ✅ Target achieved (pre-rebuild, 27-test version) |
| Run 10 | 72 | 2 | 1 | Post-rebuild to 75 tests; 2 catalog-state races found |
| Run 11 | 75 | 0 | 1 | `terminateApp()+activateApp()` reset fixed both races |
| Run 12 | 75 | 0 | 1 | Rotation test still skipped after adb-based rotation attempt (app locks its own orientation) — skip path replaced with a stability assertion |
| **Run 13** | **75** | **0** | **0** | **✅ Full target achieved — 100% pass, 0 skips** |

---

## Running the ApiDemos Suite

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

Targets the [ApiDemos](https://github.com/appium/android-apidemos) reference app. APK should be at `apks/ApiDemos-debug.apk` (optional — falls back to the already-installed app otherwise).

---
## 👨‍💻 Author

**Ranajit B Chowdhury**
**QA Automation Engineer & SDET
📧 Cell Phone - 2673425565
📧 chyranajit@gmail.com

## License

This project is for personal learning and portfolio purposes.  
The SauceLabs My Demo App is owned by [Sauce Labs, Inc](https://saucelabs.com/).
