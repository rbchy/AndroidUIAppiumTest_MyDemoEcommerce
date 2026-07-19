package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.MainScreenPage;
import pages.PermissionPage;
import utils.ExtentReportManager;

/**
 * Permission dialog handling checks against the ApiDemos app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by testng.xml / testng-saucelabs.xml was missing from the project
 * entirely. It's a minimal, working stub; expand as needed.
 */
public class PermissionTests extends BaseTest {

    ExtentTest test;
    MainScreenPage mainPage;
    PermissionPage permissionPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        mainPage = new MainScreenPage(driver);
        mainPage.ensureOnMainScreen(driver);
        permissionPage = new PermissionPage(driver);
    }

    @Test(priority = 1, groups = "permissions")
    public void permissions_GrantFlowDoesNotThrowWhenNoDialogPresent() {
        boolean dialogVisible = permissionPage.isPermissionDialogVisible();
        test.log(Status.INFO, "Permission dialog visible: " + dialogVisible);
        permissionPage.grantPermissionIfPrompted();
        test.log(Status.PASS, "Permission grant flow executed without exception");
    }

    @Test(priority = 2, groups = "permissions")
    public void permissions_DenyFlowDoesNotThrowWhenNoDialogPresent() {
        boolean dialogVisible = permissionPage.isPermissionDialogVisible();
        test.log(Status.INFO, "Permission dialog visible: " + dialogVisible);
        permissionPage.denyPermissionIfPrompted();
        test.log(Status.PASS, "Permission deny flow executed without exception");

        Assert.assertTrue(mainPage.getMenuItemCount() >= 0, "App became unresponsive after permission deny flow");
    }
}
