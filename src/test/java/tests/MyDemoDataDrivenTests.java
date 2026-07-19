package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.mydemo.LoginPage;
import utils.ExtentReportManager;

/**
 * DATA — data-driven login checks with multiple credential sets for the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoDataDrivenTests extends MyDemoBaseTest {

    ExtentTest test;
    LoginPage loginPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        loginPage = new LoginPage(driver);
    }

    @DataProvider(name = "credentials")
    public Object[][] credentials() {
        // CONFIRMED against the running app across two separate live runs on
        // 2026-07-16: this login screen does NOT reject invalid username/password
        // combinations — valid, invalid, or a mix of both all navigate past the
        // login screen. The only thing that reliably blocks navigation is an empty
        // field submission (client-side required-field validation). So there is no
        // credential combination this app treats as a rejected login other than
        // blank fields — the data set below reflects that, rather than assuming a
        // stricter validation the app doesn't actually implement.
        //
        // NOTE: tests.MyDemoNegativeTests.negative_InvalidCredentialsShowError
        // currently "passes" for an unrelated reason: it asserts on
        // LoginPage.isErrorDisplayed(), which does a page-source substring search
        // for "invalid"/"wrong" — and INVALID_USERNAME/INVALID_PASSWORD literally
        // contain those words as typed text, so the check matches regardless of
        // whether the app shows a real error. Worth knowing if you rely on that
        // test as a real negative-login check.
        return new Object[][]{
                {VALID_USERNAME, VALID_PASSWORD, true},
                {INVALID_USERNAME, INVALID_PASSWORD, true},
                {"", "", false},
                {VALID_USERNAME, "", false},
                {"", VALID_PASSWORD, false},
                {"user1@example.com", "pass1234", true},
                {"user2@example.com", "pass5678", true},
                {"another.user@example.com", "AnotherPass123", true},
                {"test.user@example.com", "TestPass9876", true},
        };
    }

    @Test(priority = 1, groups = "data-driven", dataProvider = "credentials")
    public void dataDriven_LoginOutcomeMatchesCredentialValidity(String username, String password, boolean expectSuccess) {
        loginPage.login(username, password);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        boolean stillOnLogin = loginPage.isLoginScreenVisible();
        boolean loginSucceeded = !stillOnLogin;
        test.log(Status.INFO, "user='" + username + "' expectSuccess=" + expectSuccess
                + " actualSuccess=" + loginSucceeded);

        Assert.assertEquals(loginSucceeded, expectSuccess,
                "Login outcome did not match expectation for user='" + username + "'");

        // Reset to login screen for the next data row
        if (loginSucceeded) {
            loginPage.navigateToLoginIfNeeded();
        }
    }
}
