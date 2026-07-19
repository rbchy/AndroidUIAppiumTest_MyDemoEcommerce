package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.CartPage;
import pages.mydemo.CheckoutPage;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

/**
 * NAV — screen-to-screen navigation flows within the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoNavigationTests extends MyDemoBaseTest {

    private static final String PKG = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    ProductsPage productsPage;
    CartPage cartPage;
    CheckoutPage checkoutPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        productsPage = new ProductsPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        productsPage.waitForProductsPage();
    }

    @Test(priority = 1, groups = "navigation")
    public void navigation_CanOpenCartFromCatalog() {
        productsPage.waitForProductsPage();
        // Cart must have at least one item — an empty cart doesn't render the
        // "Proceed To Checkout" button or "Cart" header that CartPage looks for.
        productsPage.addFirstProductToCart();
        productsPage.openCart();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        boolean onCart = cartPage.isCartPageVisible();
        test.log(Status.INFO, "Cart page visible after navigating from catalog: " + onCart);
        Assert.assertTrue(onCart, "Could not navigate from catalog to cart");
    }

    @Test(priority = 2, groups = "navigation", dependsOnMethods = "navigation_CanOpenCartFromCatalog")
    public void navigation_BackButtonReturnsToCatalog() {
        driver.navigate().back();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        boolean backOnCatalog = productsPage.isProductsPageVisible();
        test.log(Status.INFO, "Catalog visible after pressing back from cart: " + backOnCatalog);
        Assert.assertTrue(backOnCatalog, "Back button did not return to the product catalog");
    }

    @Test(priority = 3, groups = "navigation")
    public void navigation_HamburgerMenuIconIsPresentOnCatalog() {
        boolean present = !driver.findElements(By.id(PKG + ":id/menuIV")).isEmpty();
        test.log(Status.INFO, "Hamburger menu icon present on catalog: " + present);
        Assert.assertTrue(present, "Hamburger menu icon was not found on the catalog screen");
    }

    @Test(priority = 4, groups = "navigation")
    public void navigation_ProductDetailOpensFromCatalog() {
        driver.findElements(By.id(PKG + ":id/productIV")).get(0).click();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        boolean leftCatalog = driver.findElements(
                By.xpath("//*[@resource-id='" + PKG + ":id/productTV' and @text='Products']")).isEmpty();
        test.log(Status.INFO, "Navigated away from catalog header into product detail: " + leftCatalog);
        Assert.assertTrue(leftCatalog, "Tapping a product image did not navigate to the product detail screen");
    }

    @Test(priority = 5, groups = "navigation", dependsOnMethods = "navigation_ProductDetailOpensFromCatalog")
    public void navigation_BackFromProductDetailReturnsToCatalog() {
        driver.navigate().back();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        boolean backOnCatalog = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Catalog visible after back from product detail: " + backOnCatalog);
        Assert.assertTrue(backOnCatalog, "Back button did not return to the catalog from product detail");
    }

    @Test(priority = 6, groups = "navigation", dependsOnMethods = "navigation_BackFromProductDetailReturnsToCatalog")
    public void navigation_CartToCheckoutNavigationWorks() {
        productsPage.addFirstProductToCart();
        productsPage.openCart();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page did not open from the catalog");

        cartPage.proceedToCheckout();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        boolean onCheckout = checkoutPage.isCheckoutPageVisible();
        test.log(Status.INFO, "Checkout page visible after proceeding from cart: " + onCheckout);

        // This test ends on the checkout screen — reset back to the catalog so the
        // next test (navigation_CatalogScrollKeepsPageStable) doesn't inherit this state.
        // NOTE: activateApp() alone is a no-op while the app is still foregrounded on the
        // checkout screen — terminateApp() + activateApp() forces an actual relaunch back
        // to the catalog (activateApp()-only had no effect, confirmed by a live run).
        driver.terminateApp("com.saucelabs.mydemoapp.android");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        driver.activateApp("com.saucelabs.mydemoapp.android");
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(onCheckout, "Proceeding from cart did not open the checkout/shipping screen");
    }

    @Test(priority = 7, groups = "navigation")
    public void navigation_CatalogScrollKeepsPageStable() {
        // Defensive reset — don't assume the prior test left us on the catalog.
        if (!productsPage.isProductsPageVisible()) {
            driver.terminateApp("com.saucelabs.mydemoapp.android");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            driver.activateApp("com.saucelabs.mydemoapp.android");
            try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
        }
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"));
            Thread.sleep(500);
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollToBeginning(5)"));
        } catch (Exception e) {
            System.out.println("[MyDemoNavigationTests] Scroll gesture failed (non-fatal): " + e.getMessage());
        }
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        boolean stillOnCatalog = productsPage.isProductsPageVisible() || productsPage.getVisibleProductCount() > 0;
        test.log(Status.INFO, "Catalog remains stable after scroll gestures: " + stillOnCatalog);
        Assert.assertTrue(stillOnCatalog, "Catalog became unstable/unreadable after scrolling");
    }
}
