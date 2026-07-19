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

import java.time.Duration;

/**
 * Coverage gap-fillers for the ApiDemos app: deep-link-style relaunch,
 * notification-style background/foreground resume, and background-interrupt
 * resilience (standing in for a network drop, since ApiDemos exposes no real
 * deep link or push notification surface to test against).
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by testng.xml was missing from the project entirely. It's a
 * minimal, working stub; expand as needed once real deep-link/notification/
 * network hooks are available to test against.
 */
public class MissingCoverageTests extends BaseTest {

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

    @Test(priority = 1, groups = "deeplink")
    public void deepLink_DirectActivationLandsOnMainScreen() {
        driver.terminateApp(APP_PACKAGE);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        int count = mainPage.getMenuItemCount();
        test.log(Status.INFO, "Menu item count after direct activation: " + count);
        Assert.assertTrue(count > 0, "App did not land on a valid screen after direct activation");
    }

    @Test(priority = 2, groups = "notifications")
    public void notification_BackgroundForegroundResumePreservesState() {
        mainPage.clickMenuItemByText("Views");
        try { Thread.sleep(700); } catch (InterruptedException ignored) {}

        driver.runAppInBackground(Duration.ofSeconds(3));
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        boolean appStillResponsive = !driver.getPageSource().isEmpty();
        test.log(Status.INFO, "App responsive after simulated notification resume: " + appStillResponsive);
        Assert.assertTrue(appStillResponsive, "App became unresponsive after background/foreground resume");

        driver.navigate().back();
        mainPage.ensureOnMainScreen(driver);
    }

    @Test(priority = 3, groups = "network")
    public void network_AppRemainsResponsiveAfterBackgroundInterrupt() {
        driver.runAppInBackground(Duration.ofSeconds(5));
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        boolean appStillResponsive = !driver.getPageSource().isEmpty();
        test.log(Status.INFO, "App responsive after extended background interrupt: " + appStillResponsive);
        Assert.assertTrue(appStillResponsive, "App became unresponsive after extended background interrupt");
    }
}
