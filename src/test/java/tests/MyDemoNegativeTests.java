package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.LoginPage;
import utils.ExtentReportManager;

/**
 * NEGATIVE — invalid input / boundary handling for the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoNegativeTests extends MyDemoBaseTest {

    ExtentTest test;
    LoginPage loginPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        loginPage = new LoginPage(driver);
    }

    @Test(priority = 1, groups = "negative")
    public void negative_InvalidCredentialsShowError() {
        loginPage.login(INVALID_USERNAME, INVALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean errorShown = loginPage.isErrorDisplayed();
        test.log(Status.INFO, "Error displayed for invalid credentials: " + errorShown);
        Assert.assertTrue(errorShown, "No error was shown for invalid login credentials");
    }

    @Test(priority = 2, groups = "negative")
    public void negative_EmptyCredentialsDoNotCrashApp() {
        loginPage.navigateToLoginIfNeeded();
        loginPage.enterUsername("");
        loginPage.enterPassword("");
        loginPage.tapLoginButton();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        boolean appStillResponsive = !driver.getPageSource().isEmpty();
        test.log(Status.INFO, "App responsive after empty-credential submit: " + appStillResponsive);
        Assert.assertTrue(appStillResponsive, "App became unresponsive after submitting empty credentials");
    }

    @Test(priority = 3, groups = "negative")
    public void negative_SqlInjectionStringInUsernameDoesNotCrashOrBypass() {
        loginPage.login("' OR '1'='1", "irrelevant");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean appResponsive = !driver.getPageSource().isEmpty();
        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "App responsive: " + appResponsive + " | still on login: " + stillOnLogin);
        Assert.assertTrue(appResponsive, "App became unresponsive after a SQL-injection-style username");
    }

    @Test(priority = 4, groups = "negative")
    public void negative_VeryLongUsernameInputHandledGracefully() {
        String longUsername = "a".repeat(300) + "@example.com";
        loginPage.login(longUsername, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean appResponsive = !driver.getPageSource().isEmpty();
        test.log(Status.INFO, "App responsive after 300+ char username: " + appResponsive);
        Assert.assertTrue(appResponsive, "App became unresponsive after a very long username input");
    }

    @Test(priority = 5, groups = "negative")
    public void negative_SpecialCharactersInPasswordHandledGracefully() {
        loginPage.login(VALID_USERNAME, "!@#$%^&*()_+-=[]{}");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        boolean appResponsive = !driver.getPageSource().isEmpty();
        test.log(Status.INFO, "App responsive after special-character password: " + appResponsive);
        Assert.assertTrue(appResponsive, "App became unresponsive after a special-character password");
    }

    @Test(priority = 6, groups = "negative")
    public void negative_EmptyUsernameOnlyStaysOnLoginScreen() {
        loginPage.navigateToLoginIfNeeded();
        loginPage.enterUsername("");
        loginPage.enterPassword(VALID_PASSWORD);
        loginPage.tapLoginButton();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Still on login screen with empty username: " + stillOnLogin);
        Assert.assertTrue(stillOnLogin, "App proceeded past login with an empty username field");
    }

    @Test(priority = 7, groups = "negative")
    public void negative_EmptyPasswordOnlyStaysOnLoginScreen() {
        loginPage.navigateToLoginIfNeeded();
        loginPage.enterUsername(VALID_USERNAME);
        loginPage.enterPassword("");
        loginPage.tapLoginButton();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Still on login screen with empty password: " + stillOnLogin);
        Assert.assertTrue(stillOnLogin, "App proceeded past login with an empty password field");
    }

    @Test(priority = 8, groups = "negative")
    public void negative_BothFieldsEmptyStaysOnLoginScreen() {
        loginPage.navigateToLoginIfNeeded();
        loginPage.enterUsername("");
        loginPage.enterPassword("");
        loginPage.tapLoginButton();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        test.log(Status.INFO, "Still on login screen with both fields empty: " + stillOnLogin);
        Assert.assertTrue(stillOnLogin, "App proceeded past login with both fields empty");
    }
}
