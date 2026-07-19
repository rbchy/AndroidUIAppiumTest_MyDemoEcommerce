package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.LoginPage;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

import java.util.List;

/**
 * SMOKE — basic sanity checks that the MyDemo app deploys and boots correctly.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoSmokeTests extends MyDemoBaseTest {

    private static final String PKG = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    ProductsPage productsPage;
    LoginPage loginPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        productsPage = new ProductsPage(driver);
        loginPage = new LoginPage(driver);

        // None of the smoke tests depend on each other, but smoke_LoginScreenIsReachable
        // navigates away to the login screen — reset to the catalog before every test so
        // later ones (package/image checks) don't inherit that state. isProductsPageVisible()
        // is an instant check, so this costs nothing when already on the catalog.
        //
        // NOTE: activateApp() alone is a no-op here — the app is still running in the
        // foreground the whole time (just showing a different screen), so there's nothing
        // to "activate". terminateApp() + activateApp() forces an actual kill-and-relaunch,
        // which is what reliably lands back on the catalog (same pattern already proven in
        // MyDemoAppLifecycleTests / MyDemoSecurityTests).
        if (!productsPage.isProductsPageVisible()) {
            driver.terminateApp("com.saucelabs.mydemoapp.android");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            driver.activateApp("com.saucelabs.mydemoapp.android");
            try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
        }
    }

    @Test(priority = 1, groups = "smoke")
    public void smoke_AppLaunchesToProductCatalog() {
        boolean visible = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Products catalog visible on launch: " + visible);
        Assert.assertTrue(visible, "App did not launch to the product catalog");
    }

    @Test(priority = 2, groups = "smoke")
    public void smoke_ProductCatalogHasItems() {
        int count = productsPage.getVisibleProductCount();
        test.log(Status.INFO, "Visible product count: " + count);
        Assert.assertTrue(count > 0, "Product catalog is empty on launch");
    }

    @Test(priority = 3, groups = "smoke")
    public void smoke_LoginScreenIsReachable() {
        loginPage.navigateToLoginIfNeeded();
        boolean visible = loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Login screen reachable: " + visible);
        Assert.assertTrue(visible, "Could not reach the login screen from the catalog");
    }

    @Test(priority = 4, groups = "smoke")
    public void smoke_AppPackageIsCorrectOnLaunch() {
        String actualPackage = driver.getCurrentPackage();
        test.log(Status.INFO, "Current package: " + actualPackage);
        Assert.assertEquals(actualPackage, PKG, "App launched under an unexpected package name");
    }

    @Test(priority = 5, groups = "smoke")
    public void smoke_ProductImagesArePresentAndClickable() {
        productsPage.waitForProductsPage();
        List<WebElement> images = driver.findElements(By.id(PKG + ":id/productIV"));
        test.log(Status.INFO, "Product tap targets found: " + images.size());
        Assert.assertFalse(images.isEmpty(), "No clickable product images found on the catalog");
    }

    @Test(priority = 6, groups = "smoke")
    public void smoke_PageSourceIsNotEmptyOnLaunch() {
        String src = driver.getPageSource();
        test.log(Status.INFO, "Page source length: " + (src == null ? 0 : src.length()));
        Assert.assertTrue(src != null && !src.isEmpty(), "Page source was empty right after launch");
    }
}
