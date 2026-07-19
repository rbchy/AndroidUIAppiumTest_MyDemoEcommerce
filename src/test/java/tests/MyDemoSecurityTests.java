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

/**
 * SECURITY — auth and session-state checks for the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoSecurityTests extends MyDemoBaseTest {

    private static final String APP_PACKAGE = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    LoginPage loginPage;
    ProductsPage productsPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
    }

    @Test(priority = 1, groups = "security")
    public void security_InvalidCredentialsAreRejected() {
        loginPage.login(INVALID_USERNAME, INVALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        boolean errorShown = loginPage.isErrorDisplayed();
        test.log(Status.INFO, "Still on login: " + stillOnLogin + " | error shown: " + errorShown);
        Assert.assertTrue(stillOnLogin || errorShown, "Invalid credentials were not rejected");
    }

    @Test(priority = 2, groups = "security")
    public void security_AppStateClearsAfterTermination() {
        driver.terminateApp(APP_PACKAGE);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        driver.activateApp(APP_PACKAGE);
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}

        boolean freshCatalog = productsPage.waitForProductsPage();
        test.log(Status.INFO, "Fresh catalog visible after termination/relaunch: " + freshCatalog);
        Assert.assertTrue(freshCatalog, "App did not reset to a clean state after termination");
    }

    @Test(priority = 3, groups = "security")
    public void security_SqlInjectionInUsernameDoesNotBypassLogin() {
        loginPage.login("' OR 1=1 --", "anything");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        boolean errorShown = loginPage.isErrorDisplayed();
        test.log(Status.INFO, "Still on login: " + stillOnLogin + " | error shown: " + errorShown);
        Assert.assertTrue(stillOnLogin || errorShown, "A SQL-injection-style username was not rejected");
    }

    @Test(priority = 4, groups = "security")
    public void security_XssPayloadInUsernameHandledSafely() {
        loginPage.login("<script>alert(1)</script>", "test1234");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean appResponsive = !driver.getPageSource().isEmpty();
        test.log(Status.INFO, "App responsive after XSS-style username input: " + appResponsive);
        Assert.assertTrue(appResponsive, "App became unresponsive after an XSS-style username input");
    }

    @Test(priority = 5, groups = "security")
    public void security_PlaintextPasswordNotExposedInPageSourceAfterLogin() {
        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean loggedIn = !loginPage.isLoginScreenVisible();

        String src = driver.getPageSource();
        boolean passwordExposed = src != null && src.contains(VALID_PASSWORD);
        test.log(Status.INFO, "Logged in: " + loggedIn + " | password visible in page source: " + passwordExposed);
        Assert.assertTrue(loggedIn, "Login did not succeed with valid credentials");
        Assert.assertFalse(passwordExposed, "Plaintext password was found in the page source after login");
    }
}
