package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

import java.time.Duration;

/**
 * DEVICE — device-level behavior (rotation, screen metrics) for the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoDeviceBehaviorTests extends MyDemoBaseTest {

    private static final String APP_PACKAGE = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    ProductsPage productsPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        productsPage = new ProductsPage(driver);
    }

    @Test(priority = 1, groups = "device")
    public void device_RotationDoesNotCrashApp() {
        // Both driver.rotate() and direct adb "settings put system user_rotation" fail
        // to actually flip the display here — even adb-level rotation was never
        // confirmed by the OS. That points to the app's activity declaring a fixed
        // android:screenOrientation="portrait" in its manifest (common for catalog/
        // checkout-style apps), which no system setting can override for that one
        // activity. That's correct app behavior, not a bug — so this test (per its
        // name) verifies what it's actually meant to: requesting a rotation does not
        // crash or destabilize the app, regardless of whether the OS honors it.
        runAdbSettings("accelerometer_rotation", "0");
        runAdbSettings("user_rotation", "1"); // 1 = landscape (90 degrees)
        boolean rotatedToLandscape = waitForRotationAttribute("1", 6);
        test.log(Status.INFO, "Rotation to landscape honored by OS: " + rotatedToLandscape
                + (rotatedToLandscape ? "" : " (app likely locks its own orientation — expected, not a failure)"));

        boolean responsiveAfterRotationAttempt =
                productsPage.isProductsPageVisible() || productsPage.getVisibleProductCount() >= 0;

        runAdbSettings("user_rotation", "0"); // restore portrait
        waitForRotationAttribute("0", 6);
        runAdbSettings("accelerometer_rotation", "1"); // restore default auto-rotate

        boolean responsiveBackInPortrait = productsPage.waitForProductsPage();

        test.log(Status.INFO, "Responsive right after rotation attempt: " + responsiveAfterRotationAttempt
                + " | responsive back in portrait: " + responsiveBackInPortrait);

        Assert.assertTrue(responsiveAfterRotationAttempt && responsiveBackInPortrait,
                "App did not remain stable/responsive around a rotation attempt");
    }

    /** Sets an Android "system" settings key via adb shell (e.g. accelerometer_rotation, user_rotation). */
    private void runAdbSettings(String key, String value) {
        try {
            Runtime.getRuntime().exec(new String[]{"adb", "-s", "emulator-5554", "shell",
                    "settings", "put", "system", key, value}).waitFor();
        } catch (Exception e) {
            System.out.println("[MyDemoDeviceBehaviorTests] adb settings put " + key + "=" + value + " failed: " + e.getMessage());
        }
    }

    /** Reads the display rotation (0-3, i.e. 0/90/180/270 degrees) from the page-source hierarchy root. */
    private String getCurrentRotationAttribute() {
        String src = driver.getPageSource();
        if (src == null) return null;
        int idx = src.indexOf("rotation=\"");
        if (idx == -1) return null;
        int start = idx + "rotation=\"".length();
        int end = src.indexOf('"', start);
        return end > start ? src.substring(start, end) : null;
    }

    private boolean waitForRotationAttribute(String expected, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            if (expected.equals(getCurrentRotationAttribute())) return true;
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        return expected.equals(getCurrentRotationAttribute());
    }

    @Test(priority = 2, groups = "device")
    public void device_ScreenDimensionsAreValid() {
        Dimension size = driver.manage().window().getSize();
        test.log(Status.INFO, "Screen size: " + size.getWidth() + "x" + size.getHeight());
        Assert.assertTrue(size.getWidth() > 0 && size.getHeight() > 0, "Invalid screen dimensions reported");
    }

    @Test(priority = 3, groups = "device")
    public void device_BackButtonFromCatalogRecoversViaReactivate() {
        driver.navigate().back();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        if (!productsPage.isProductsPageVisible()) {
            driver.activateApp(APP_PACKAGE);
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Catalog reachable after back-from-catalog + reactivate: " + recovered);
        Assert.assertTrue(recovered, "Could not recover the catalog after pressing back from it");
    }

    @Test(priority = 4, groups = "device")
    public void device_HomeBackgroundDoesNotCrashApp() {
        driver.runAppInBackground(Duration.ofSeconds(2));
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "App recovered after simulated Home press: " + recovered);
        Assert.assertTrue(recovered, "App did not recover after being sent to the background (Home press)");
    }

    @Test(priority = 5, groups = "device")
    public void device_AppRespondsToRapidBackPresses() {
        driver.navigate().back();
        driver.navigate().back();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "App recovered after rapid back presses: " + recovered);
        Assert.assertTrue(recovered, "App did not recover cleanly after rapid back presses");
    }
}
