package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.CartPage;
import pages.mydemo.LoginPage;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

/**
 * PERF — response-time SLA checks for the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoPerformanceTests extends MyDemoBaseTest {

    private static final String APP_PACKAGE = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    ProductsPage productsPage;
    LoginPage loginPage;
    CartPage cartPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        productsPage = new ProductsPage(driver);
        loginPage = new LoginPage(driver);
        cartPage = new CartPage(driver);
    }

    @Test(priority = 1, groups = "performance")
    public void performance_ProductCatalogLoadsWithinThreshold() {
        long start = System.currentTimeMillis();
        boolean loaded = productsPage.waitForProductsPage();
        long durationMs = System.currentTimeMillis() - start;

        test.log(Status.INFO, "Catalog load duration: " + durationMs + " ms");
        Assert.assertTrue(loaded, "Product catalog did not load");
        Assert.assertTrue(durationMs < 10000, "Product catalog took too long to load (" + durationMs + " ms)");
    }

    @Test(priority = 2, groups = "performance")
    public void performance_AddToCartWithinThreshold() {
        long start = System.currentTimeMillis();
        productsPage.addFirstProductToCart();
        long durationMs = System.currentTimeMillis() - start;

        test.log(Status.INFO, "Add-to-cart duration: " + durationMs + " ms");
        Assert.assertTrue(durationMs < 15000, "Add-to-cart flow took too long (" + durationMs + " ms)");
    }

    @Test(priority = 3, groups = "performance")
    public void performance_LoginFlowCompletesWithinThreshold() {
        long start = System.currentTimeMillis();
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        long durationMs = System.currentTimeMillis() - start;

        boolean loggedIn = !loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Login flow duration: " + durationMs + " ms");
        Assert.assertTrue(loggedIn, "Login did not complete successfully");
        Assert.assertTrue(durationMs < 15000, "Login flow took too long (" + durationMs + " ms)");
    }

    @Test(priority = 4, groups = "performance")
    public void performance_CartOpenWithinThreshold() {
        productsPage.addFirstProductToCart();

        long start = System.currentTimeMillis();
        productsPage.openCart();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        long durationMs = System.currentTimeMillis() - start;

        test.log(Status.INFO, "Cart-open duration: " + durationMs + " ms");
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page did not open");
        Assert.assertTrue(durationMs < 8000, "Opening the cart took too long (" + durationMs + " ms)");
    }

    @Test(priority = 5, groups = "performance")
    public void performance_AppRelaunchWithinThreshold() {
        driver.terminateApp(APP_PACKAGE);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        long start = System.currentTimeMillis();
        driver.activateApp(APP_PACKAGE);
        boolean recovered = productsPage.waitForProductsPage();
        long durationMs = System.currentTimeMillis() - start;

        test.log(Status.INFO, "Relaunch duration: " + durationMs + " ms");
        Assert.assertTrue(recovered, "App did not relaunch to the catalog");
        Assert.assertTrue(durationMs < 15000, "App relaunch took too long (" + durationMs + " ms)");
    }
}
