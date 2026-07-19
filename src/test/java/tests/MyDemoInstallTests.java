package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

/**
 * INSTALL — install-state and relaunch checks for the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 *
 * Intentionally does NOT call removeApp()/installApp() here — this class shares
 * the emulator session with the rest of the MyDemo suite via MyDemoBaseTest, and
 * uninstalling mid-suite would break every test that runs after it. If you need
 * true install/uninstall coverage, give this class its own driver session
 * (see tests.InstallLifecycleTests for that pattern against the ApiDemos app).
 */
public class MyDemoInstallTests extends MyDemoBaseTest {

    private static final String APP_PACKAGE = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    ProductsPage productsPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        productsPage = new ProductsPage(driver);
    }

    @Test(priority = 1, groups = "install")
    public void install_AppIsInstalled() {
        boolean installed = driver.isAppInstalled(APP_PACKAGE);
        test.log(Status.INFO, "App installed: " + installed);
        Assert.assertTrue(installed, "MyDemo app is not reported as installed");
    }

    @Test(priority = 2, groups = "install")
    public void install_AppLaunchesCleanlyAfterActivate() {
        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean loaded = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Catalog visible after activate: " + loaded);
        Assert.assertTrue(loaded, "App did not launch cleanly to the catalog after activation");
    }

    @Test(priority = 3, groups = "install")
    public void install_AppPackageNameMatchesExpected() {
        String actualPackage = driver.getCurrentPackage();
        test.log(Status.INFO, "Current package: " + actualPackage);
        Assert.assertEquals(actualPackage, APP_PACKAGE, "Running app package does not match the expected MyDemo package");
    }

    @Test(priority = 4, groups = "install")
    public void install_ReactivatingAlreadyRunningAppIsIdempotent() {
        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        boolean stillOk = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Catalog visible after double activateApp(): " + stillOk);
        Assert.assertTrue(stillOk, "Re-activating an already-running app left it in a broken state");
    }
}
