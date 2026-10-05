# B4XAppium

A B4J/B4X wrapper around the [Appium Java client](https://github.com/appium/java-client) and Selenium's W3C Actions API. It lets a B4J program drive a real Appium session — launch/attach to an app, find elements, tap, type, swipe, scroll, screenshot, and generate an HTML test report — without writing any Java.

Two libraries are included:

- **B4XAppium** (`ShortName: B4XAppium`) — the Appium/Selenium driver wrapper. Also exposes `WebElement` and `DesiredCapabilities` helper types.
- **TestReport** (`ShortName: TestReport`) — a small [ExtentReports](https://www.extentreports.com/) wrapper for producing a pass/fail HTML report.

Current version: **1.29**. Supports Android (UiAutomator2) and iOS (XCUITest) sessions.

## Requirements

- B4J (v9+ recommended)
- A running [Appium server](https://appium.io/) (`appium` on `http://127.0.0.1:4723` by default)
- An Android device/emulator with the UiAutomator2 driver, or an iOS device/simulator with the XCUITest driver
- The dependency jars listed below, all placed in the same folder

## Installation

1. Copy `B4J_Library/B4XAppium.jar` and `B4J_Library/B4XAppium.xml` into your B4J **Additional Libraries Folder** (configured in B4J under `Tools > Configure Paths`, or found in `b4xV5.ini` as `AdditionalLibrariesFolder`).
2. The library depends on the following jars — place them in that same folder (they're vendored under `libs/` in this repo for reference):
   - `java-client-9.2.0.jar`
   - `selenium-api-4.18.1.jar`
   - `selenium-http-4.18.1.jar`
   - `selenium-java-4.18.1.jar`
   - `selenium-remote-driver-4.18.1.jar`
   - `selenium-support-4.7.2.jar`
   - `guava-32.1.2-jre.jar`
   - `extentreports-5.1.2.jar` (required by `TestReport`, not by `B4XAppium` itself)
3. In the B4J IDE, open **Libraries Manager** and check both `b4xappium` and, if you want reporting, confirm `TestReport` shows up under the same `b4xappium` entry (both classes ship in one jar/xml pair).
4. Add `#AdditionalJar` is not needed — B4J resolves everything via the Additional Libraries Folder + the library's own `dependsOn` metadata.

## Building from source

The Eclipse project under `src/` is the canonical source. To rebuild the jar: export `com.genesis.appium.B4XAppium` and `com.genesis.appium.ReportWrapper` (plus their nested classes) as a jar, using the jars in `libs/` as the compile classpath, then regenerate `B4XAppium.xml` with B4J's library doclet. Drop the resulting jar/xml pair into `B4J_Library/`.

## API overview

### `B4XAppium`

| Method | Description |
|---|---|
| `Initialize(ba, platform, urlHost, port, basePath, capabilities)` | Starts a session. `platform` is `"android"` or `"ios"`. |
| `isInitialized` | Whether a session is active. |
| `quit` | Ends the session. |
| `SessionId` (property) | The Appium session id. |
| `PageSource` (property) | Current page source XML. |
| `takeScreenShot(filePath)` | Saves a PNG screenshot to disk. |
| `findElementByid(id)` / `tryFindElementById(id)` | Locate by resource-id (Android) / accessibility id fallback. The `try*` variant returns `Null` (an uninitialized `WebElement`) instead of throwing. |
| `findElementByText(text)` / `tryFindElementByText(text)` | Locate by visible text or label (matches `@text`/`@content-desc` on Android, `@name`/`@label` on iOS). |
| `findElementByAccessibilityid(id)` / `tryFindElementByAccessibilityId(id)` | Locate by accessibility id. |
| `findElementByAndroidUIAutomator(uiSelector)` / `tryFindElementByAndroidUIAutomator(uiSelector)` | Locate using a raw `UiSelector(...)` expression string (Android only). |
| `findElementByXPathRaw(xpath)` | Locate using a raw XPath expression. |
| `findElementByTagName(tagName)` / `findElementByName(ba, elementName)` | Locate by tag name / `name` attribute. |
| `waitForId(id, timeoutSec)` / `waitForText(text, timeoutSec)` | Polls (via `WebDriverWait`) until the element appears; returns `True`/`False`. |
| `Back` | Native back navigation. |
| `SwipeScreen(startX, startY, endX, endY, durationMs)` | Raw single-finger swipe gesture. |
| `SwipeUp(durationMs)` / `SwipeDown(durationMs)` | Swipe by screen percentage (80%→20% of height), not fixed pixels — works across device resolutions. |
| `ScrollToText(text, maxSwipes)` | Repeatedly swipes up until `text` is found or `maxSwipes` is reached. |

### `WebElement` (returned by every `findElementBy*`)

`Click`, `SendKeys(input)`, `Clear`, `Text` (property), `AccessibleName` (property), `IsInitialized`.

### `DesiredCapabilities`

`Initialize(ba)`, `setCapability(ba, key, value)` — call once per capability key before passing it to `B4XAppium.Initialize`.

### `TestReport`

`Initialize(reportPath)`, `StartTest(name)`, `LogPass(msg)`, `LogFail(msg)`, `AddScreenshot(path)`, `EndReport` — call `EndReport` exactly once, after all tests for the run have finished.

## Examples

### 1. Connect to an Android app and tap a button

```b4x
Sub AppStart (Form1 As Form, Args() As String)
    Dim caps As DesiredCapabilities
    caps.Initialize
    caps.setCapability("platformName", "Android")
    caps.setCapability("automationName", "UiAutomator2")
    caps.setCapability("appium:deviceName", "R3GL105QLNZ")
    caps.setCapability("appium:app", "C:\Apps\MyApp.apk")
    caps.setCapability("appium:autoGrantPermissions", True)
    caps.setCapability("appium:noReset", True)

    Dim driver As B4XAppium
    driver.Initialize(Me, "android", "http://127.0.0.1", 4723, "", caps)

    Dim btn As WebElement = driver.tryFindElementByText("Sign In")
    If btn.IsInitialized Then
        btn.Click
    End If

    driver.takeScreenShot(File.Combine(File.DirApp, "after_signin.png"))
    driver.quit
End Sub
```

### 2. Wait for a field, type into it, and submit

```b4x
If driver.waitForText("Enter username", 10) Then
    Dim userField As WebElement = driver.findElementByText("Enter username")
    userField.SendKeys("walterf25")
End If

Dim loginBtn As WebElement = driver.findElementByText("LOGIN")
loginBtn.Click
```

### 3. Scroll down a list until a label is visible, then tap it

```b4x
If driver.ScrollToText("Advanced Settings", 6) Then
    Dim item As WebElement = driver.findElementByText("Advanced Settings")
    item.Click
Else
    Log("Advanced Settings not found after scrolling")
End If
```

### 4. Locate with a raw UiAutomator2 selector (e.g. partial text match)

```b4x
Dim deviceRow As WebElement = driver.tryFindElementByAndroidUIAutomator($"new UiSelector().textContains("Ranger")"$)
If deviceRow.IsInitialized Then
    deviceRow.Click
End If
```

### 5. Drive a short scripted run and generate an HTML report

```b4x
Dim driver As B4XAppium
Dim report As TestReport

driver.Initialize(Me, "android", "http://127.0.0.1", 4723, "", caps)
report.Initialize(File.Combine(File.DirApp, "test-report.html"))
report.StartTest("Login flow")

If driver.waitForText("BLE State", 20) Then
    report.LogPass("Connect screen loaded")
Else
    report.LogFail("Connect screen did not load in time")
End If

Dim shot As String = File.Combine(File.DirApp, "connect.png")
driver.takeScreenShot(shot)
report.AddScreenshot(shot)

report.EndReport   ' call exactly once, after every test in the run has finished
driver.quit
```

### 6. iOS instead of Android

Only the `platform` argument and the capabilities change — the rest of the API (`findElementByText`, `SwipeUp`, `ScrollToText`, etc.) behaves the same, automatically switching its internal XPath/locator strategy for iOS:

```b4x
caps.setCapability("platformName", "iOS")
caps.setCapability("automationName", "XCUITest")
caps.setCapability("appium:deviceName", "iPhone 15")
caps.setCapability("appium:app", "/path/to/MyApp.app")

driver.Initialize(Me, "ios", "http://127.0.0.1", 4723, "", caps)
```

## Notes & gotchas

- `findElementBy*` throws if nothing matches; use the `tryFindElementBy*` variant (returns an uninitialized `WebElement`, check with `.IsInitialized`) when the element may legitimately be absent.
- `SwipeUp`/`SwipeDown` scroll by screen percentage, so a recipe written against one device resolution will still work on another.
- `findElementByAndroidUIAutomator` / `tryFindElementByAndroidUIAutomator` only work on Android — they throw `IllegalStateException` if called against an iOS session.
- `TestReport.EndReport` must be called exactly once per report file, after the last test finishes — calling it mid-run or more than once breaks the underlying ExtentReports lifecycle.
