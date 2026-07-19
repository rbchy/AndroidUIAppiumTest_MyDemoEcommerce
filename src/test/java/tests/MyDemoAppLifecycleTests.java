package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.LoginPage;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

import java.time.Duration;

/**
 * LIFECYCLE — background/foreground and terminate/relaunch behavior of the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoAppLifecycleTests extends MyDemoBaseTest {

    private static final String APP_PACKAGE = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    ProductsPage productsPage;
    LoginPage loginPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        productsPage = new ProductsPage(driver);
        loginPage = new LoginPage(driver);
    }

    @Test(priority = 1, groups = "lifecycle")
    public void lifecycle_AppSurvivesBackgroundAndForeground() {
        driver.runAppInBackground(Duration.ofSeconds(3));
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Products page visible after background/foreground: " + recovered);
        Assert.assertTrue(recovered, "App did not recover cleanly after being backgrounded");
    }

    @Test(priority = 2, groups = "lifecycle")
    public void lifecycle_AppSurvivesTerminateAndRelaunch() {
        driver.terminateApp(APP_PACKAGE);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}

        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Products page visible after terminate/relaunch: " + recovered);
        Assert.assertTrue(recovered, "App did not relaunch cleanly to the catalog");
    }

    @Test(priority = 3, groups = "lifecycle")
    public void lifecycle_MultipleBackgroundForegroundCyclesRemainStable() {
        for (int i = 0; i < 3; i++) {
            driver.runAppInBackground(Duration.ofSeconds(2));
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        }
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Products page visible after 3 background/foreground cycles: " + recovered);
        Assert.assertTrue(recovered, "App did not remain stable across repeated background/foreground cycles");
    }

    @Test(priority = 4, groups = "lifecycle")
    public void lifecycle_LoginStateSurvivesBackgroundForeground() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        driver.runAppInBackground(Duration.ofSeconds(3));
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        boolean stillLoggedIn = !loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Still logged in after background/foreground: " + stillLoggedIn);
        Assert.assertTrue(stillLoggedIn, "Login session was lost after backgrounding and foregrounding the app");
    }

    @Test(priority = 5, groups = "lifecycle")
    public void lifecycle_LongBackgroundIntervalRecoversCleanly() {
        driver.runAppInBackground(Duration.ofSeconds(10));
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Products page visible after a 10s background interval: " + recovered);
        Assert.assertTrue(recovered, "App did not recover cleanly after a longer background interval");
    }

    @Test(priority = 6, groups = "lifecycle")
    public void lifecycle_AppRespondsPromptlyAfterTerminateRelaunch() {
        driver.terminateApp(APP_PACKAGE);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        long start = System.currentTimeMillis();
        driver.activateApp(APP_PACKAGE);
        boolean recovered = productsPage.waitForProductsPage();
        long durationMs = System.currentTimeMillis() - start;

        test.log(Status.INFO, "Relaunch-to-catalog duration: " + durationMs + " ms");
        Assert.assertTrue(recovered, "App did not relaunch to the catalog");
        Assert.assertTrue(durationMs < 20000, "App took too long to become responsive after relaunch (" + durationMs + " ms)");
    }
}
