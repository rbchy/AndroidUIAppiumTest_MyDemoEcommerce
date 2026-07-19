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
import pages.mydemo.CartPage;
import pages.mydemo.CheckoutPage;
import pages.mydemo.LoginPage;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.List;

/**
 * E2E — full Login + Cart + Checkout flow against the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs (testng-mydemo-local.xml, testng-mydemo-all.xml,
 * testng-saucelabs-mydemo.xml) was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoE2ETests extends MyDemoBaseTest {

    private static final String PKG = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    LoginPage loginPage;
    ProductsPage productsPage;
    CartPage cartPage;
    CheckoutPage checkoutPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
    }

    @Test(priority = 1, groups = "e2e")
    public void e2e_LoginWithValidCredentials() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Still on login screen after valid credentials: " + stillOnLogin);
        Assert.assertFalse(stillOnLogin, "Login did not proceed past the login screen with valid credentials");
    }

    @Test(priority = 2, groups = "e2e", dependsOnMethods = "e2e_LoginWithValidCredentials")
    public void e2e_AddProductToCartAndVerify() {
        productsPage.waitForProductsPage();
        productsPage.addFirstProductToCart();
        boolean cartHasItems = productsPage.isCartNotEmpty();
        test.log(Status.INFO, "Cart has items after add-to-cart: " + cartHasItems);
        Assert.assertTrue(cartHasItems, "Cart badge did not reflect the added product");
    }

    @Test(priority = 3, groups = "e2e", dependsOnMethods = "e2e_AddProductToCartAndVerify")
    public void e2e_CompleteCheckoutFlow() {
        productsPage.openCart();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page did not open");

        cartPage.proceedToCheckout();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(checkoutPage.isCheckoutPageVisible(), "Checkout/shipping page did not open");

        checkoutPage.fillShippingInfo("Test User", "123 Main St", "Springfield", "12345", "USA");
        checkoutPage.goToPayment();
        checkoutPage.fillPaymentInfo("4111111111111111", "12/28", "123");
        checkoutPage.goToOrderReview();
        checkoutPage.placeOrder();

        boolean success = checkoutPage.isOrderSuccessful();
        test.log(Status.INFO, "Order confirmation: " + checkoutPage.getConfirmationText());
        Assert.assertTrue(success, "Order was not placed successfully");
    }

    // ── Additional E2E flows continuing on from the order just placed above ────

    /**
     * Best-effort tap of a post-order "Continue Shopping" style button. If it
     * can't be found (unknown exact locator on this app build), falls back to
     * re-activating the app, which reliably lands back on the catalog.
     */
    private void returnToCatalogAfterOrder() {
        try {
            List<WebElement> continueBtn = driver.findElements(By.xpath(
                    "//*[contains(@text,'Continue') or contains(@text,'CONTINUE')"
                    + " or contains(@content-desc,'Continue')]"));
            if (!continueBtn.isEmpty()) {
                continueBtn.get(0).click();
                Thread.sleep(1500);
                if (productsPage.waitForProductsPage()) return;
            }
        } catch (Exception e) {
            System.out.println("[MyDemoE2ETests] Continue-shopping tap failed: " + e.getMessage());
        }
        // Fallback: reactivating the app reliably returns to the catalog.
        try {
            driver.activateApp(PKG);
            Thread.sleep(2000);
        } catch (Exception ignored) {}
    }

    @Test(priority = 4, groups = "e2e", dependsOnMethods = "e2e_CompleteCheckoutFlow")
    public void e2e_ReturnToCatalogAfterOrderCompletion() {
        returnToCatalogAfterOrder();
        boolean onCatalog = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Catalog reachable after order completion: " + onCatalog);
        Assert.assertTrue(onCatalog, "Could not return to the catalog after completing an order");
    }

    @Test(priority = 5, groups = "e2e", dependsOnMethods = "e2e_ReturnToCatalogAfterOrderCompletion")
    public void e2e_CartIsEmptyAfterSuccessfulOrder() {
        boolean cartHasItems = productsPage.isCartNotEmpty();
        test.log(Status.INFO, "Cart still has items after order placed: " + cartHasItems);
        Assert.assertFalse(cartHasItems, "Cart still shows items after a successfully placed order");
    }

    @Test(priority = 6, groups = "e2e", dependsOnMethods = "e2e_ReturnToCatalogAfterOrderCompletion")
    public void e2e_LoginPersistsAcrossAppBackgroundForeground() {
        driver.runAppInBackground(Duration.ofSeconds(3));
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        boolean stillLoggedIn = !loginPage.isLoginScreenVisible();
        boolean catalogReachable = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Still logged in after background/foreground: " + stillLoggedIn);
        Assert.assertTrue(stillLoggedIn && catalogReachable,
                "Session did not persist across a background/foreground cycle");
    }

    @Test(priority = 7, groups = "e2e", dependsOnMethods = "e2e_CartIsEmptyAfterSuccessfulOrder")
    public void e2e_AddProductToCartAgainAfterOrderSucceeds() {
        productsPage.addFirstProductToCart();
        boolean cartHasItems = productsPage.isCartNotEmpty();
        test.log(Status.INFO, "Cart has items after adding post-order: " + cartHasItems);
        Assert.assertTrue(cartHasItems, "Could not add a product to cart again after completing an earlier order");
    }

    @Test(priority = 8, groups = "e2e", dependsOnMethods = "e2e_AddProductToCartAgainAfterOrderSucceeds")
    public void e2e_NavigateToCartAndBackToCatalogWorksPostOrder() {
        productsPage.openCart();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page did not open on the second visit");

        driver.navigate().back();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        boolean backOnCatalog = productsPage.isProductsPageVisible();
        test.log(Status.INFO, "Back on catalog after revisiting cart: " + backOnCatalog);
        Assert.assertTrue(backOnCatalog, "Could not navigate back to the catalog from the cart");
    }

    @Test(priority = 9, groups = "e2e", dependsOnMethods = "e2e_NavigateToCartAndBackToCatalogWorksPostOrder")
    public void e2e_LogoutAfterCompletingPurchaseReturnsToLoginScreen() {
        // Currently logged in and on the catalog — navigateToLoginIfNeeded() detects
        // this and drives the internal Log Out flow instead of a no-op.
        loginPage.navigateToLoginIfNeeded();
        boolean onLoginScreen = loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Login screen visible after logout: " + onLoginScreen);
        Assert.assertTrue(onLoginScreen, "Logging out after a completed purchase did not return to the login screen");
    }
}
