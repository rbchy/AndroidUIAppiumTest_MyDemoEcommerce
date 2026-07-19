package tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.time.Duration;

public class ApiDemosTest {

    AndroidDriver driver;

    @BeforeClass
    public void setUp() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName("emulator-5554");
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        // Cross-platform: <project-root>/apks/ApiDemos-debug.apk. If missing, fall
        // back to launching whatever's already installed instead of failing the session.
        File apk = new File("apks/ApiDemos-debug.apk");
        if (apk.exists()) {
            options.setApp(apk.getAbsolutePath());
        } else {
            options.setNoReset(true);
        }
        options.setAppPackage("io.appium.android.apis");
        options.setAppActivity("io.appium.android.apis.ApiDemos");
        options.setNewCommandTimeout(Duration.ofSeconds(120));
        options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(60));
        options.setUiautomator2ServerInstallTimeout(Duration.ofSeconds(60));

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
        Thread.sleep(2000);
    }

    @Test
    public void testClickAccessibilityMenu() {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"Accessibility\")")).click();

        Assert.assertTrue(driver.getPageSource().contains("Accessibility"));

        System.out.println("Successfully navigated to Accessibility screen");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}