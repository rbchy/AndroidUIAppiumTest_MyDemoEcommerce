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
 * INTERRUPT — simulated interruptions (background events like calls/SMS/notifications)
 * for the MyDemo app. Uses runAppInBackground() as a stand-in for a real telephony/SMS
 * simulation, matching the pattern already used elsewhere in this codebase
 * (see tests.CombinedApiDemosTests, tests.E2EJourneyTests).
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoInterruptTests extends MyDemoBaseTest {

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

    @Test(priority = 1, groups = "interrupt")
    public void interrupt_AppSurvivesShortBackgroundInterrupt() {
        driver.runAppInBackground(Duration.ofSeconds(2));
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "App recovered after short interrupt: " + recovered);
        Assert.assertTrue(recovered, "App did not recover after a short background interrupt");
    }

    @Test(priority = 2, groups = "interrupt")
    public void interrupt_AppSurvivesLongerBackgroundInterrupt() {
        driver.runAppInBackground(Duration.ofSeconds(6));
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "App recovered after longer interrupt: " + recovered);
        Assert.assertTrue(recovered, "App did not recover after a longer background interrupt");
    }

    @Test(priority = 3, groups = "interrupt")
    public void interrupt_AppSurvivesRepeatedShortInterrupts() {
        for (int i = 0; i < 3; i++) {
            driver.runAppInBackground(Duration.ofSeconds(1));
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        }
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "App recovered after 3 repeated short interrupts: " + recovered);
        Assert.assertTrue(recovered, "App did not recover after repeated short interrupts");
    }

    @Test(priority = 4, groups = "interrupt")
    public void interrupt_CartStateSurvivesInterruptDuringShopping() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        productsPage.addFirstProductToCart();

        driver.runAppInBackground(Duration.ofSeconds(3));
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        boolean cartHasItems = productsPage.isCartNotEmpty();
        test.log(Status.INFO, "Cart still has items after interrupt during shopping: " + cartHasItems);
        Assert.assertTrue(cartHasItems, "Cart contents were lost after a background interrupt while shopping");
    }

    @Test(priority = 5, groups = "interrupt")
    public void interrupt_NetworkToggleSimulationDoesNotCrashApp() {
        try {
            Runtime.getRuntime().exec(new String[]{"adb", "-s", "emulator-5554", "shell", "svc", "wifi", "disable"}).waitFor();
            Thread.sleep(1500);
            Runtime.getRuntime().exec(new String[]{"adb", "-s", "emulator-5554", "shell", "svc", "wifi", "enable"}).waitFor();
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("[MyDemoInterruptTests] Wifi toggle via adb failed (non-fatal): " + e.getMessage());
        }
        boolean recovered = productsPage.waitForProductsPage();
        test.log(Status.INFO, "App still responsive after network toggle: " + recovered);
        Assert.assertTrue(recovered, "App did not remain responsive after a network connectivity toggle");
    }
}
