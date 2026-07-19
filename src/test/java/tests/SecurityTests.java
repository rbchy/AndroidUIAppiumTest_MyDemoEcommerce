package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.MainScreenPage;
import utils.ExtentReportManager;
import utils.WaitUtils;

import org.openqa.selenium.By;

/**
 * Security-focused checks against the ApiDemos app (session/state clearing on
 * relaunch, no unexpected package after termination).
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by testng.xml / testng-saucelabs.xml was missing from the project
 * entirely. It's a minimal, working stub; expand as needed.
 */
public class SecurityTests extends BaseTest {

    private static final String APP_PACKAGE = "io.appium.android.apis";

    ExtentTest test;
    MainScreenPage mainPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        mainPage = new MainScreenPage(driver);
        mainPage.ensureOnMainScreen(driver);
    }

    @Test(priority = 1, groups = "security")
    public void security_AppStateClearsAfterTermination() {
        mainPage.clickMenuItemByText("Graphics");
        try { Thread.sleep(700); } catch (InterruptedException ignored) {}

        driver.terminateApp(APP_PACKAGE);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        driver.activateApp(APP_PACKAGE);
        WaitUtils.waitForVisible(driver, By.id("android:id/text1"), 15);

        int count = mainPage.getMenuItemCount();
        test.log(Status.INFO, "Menu count after fresh activation: " + count);
        Assert.assertEquals(count, 12,
                "App did not reset to a clean main screen — possible stale state/session leak");
    }

    @Test(priority = 2, groups = "security")
    public void security_ForegroundPackageMatchesExpectedAfterRelaunch() {
        driver.terminateApp(APP_PACKAGE);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String currentPackage = mainPage.getCurrentAppPackage();
        test.log(Status.INFO, "Foreground package after relaunch: " + currentPackage);
        Assert.assertEquals(currentPackage, APP_PACKAGE,
                "Unexpected app in foreground after relaunch — possible session leak to another app");
    }
}
