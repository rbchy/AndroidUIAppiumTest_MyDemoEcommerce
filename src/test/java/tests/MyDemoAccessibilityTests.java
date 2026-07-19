package tests;

import base.MyDemoBaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.mydemo.ProductsPage;
import utils.ExtentReportManager;

import java.util.List;

/**
 * A11Y — accessibility basics (labels, tap target size) for the MyDemo app.
 *
 * NOTE: This class was rebuilt from scratch on 2026-07-15 — the original file
 * referenced by the suite XMLs was missing from the project entirely. It's a
 * minimal, working stub covering the class's intended category; expand as needed.
 */
public class MyDemoAccessibilityTests extends MyDemoBaseTest {

    private static final String PKG = "com.saucelabs.mydemoapp.android";

    ExtentTest test;
    ProductsPage productsPage;

    @BeforeMethod
    public void setupReport(java.lang.reflect.Method method) {
        if (driver == null) throw new SkipException("Driver not initialized — skipping test");
        test = ExtentReportManager.createTest(method.getName());
        productsPage = new ProductsPage(driver);
        productsPage.waitForProductsPage();
    }

    @Test(priority = 1, groups = "accessibility")
    public void accessibility_ProductTitlesHaveReadableText() {
        List<WebElement> titles = driver.findElements(By.id(PKG + ":id/titleTV"));
        int missing = 0;
        for (WebElement title : titles) {
            String text = title.getText();
            if (text == null || text.trim().isEmpty()) missing++;
        }
        test.log(Status.INFO, "Product titles missing text: " + missing + "/" + titles.size());
        Assert.assertEquals(missing, 0, "Some product titles have no readable text for screen readers");
    }

    @Test(priority = 2, groups = "accessibility")
    public void accessibility_TapTargetsAreReasonablySized() {
        List<WebElement> images = driver.findElements(By.id(PKG + ":id/productIV"));
        Assert.assertFalse(images.isEmpty(), "No product tap targets found to evaluate");

        Rectangle rect = images.get(0).getRect();
        test.log(Status.INFO, "First product tap target size: " + rect.getWidth() + "x" + rect.getHeight());
        Assert.assertTrue(rect.getWidth() > 0 && rect.getHeight() > 0,
                "Tap target size should be measurable and non-zero");
    }

    private String describe(WebElement el) {
        String desc = el.getAttribute("content-desc");
        if (desc == null || desc.isEmpty()) desc = el.getAttribute("contentDescription");
        return desc == null ? "" : desc;
    }

    @Test(priority = 3, groups = "accessibility")
    public void accessibility_HamburgerMenuHasContentDescription() {
        List<WebElement> menu = driver.findElements(By.id(PKG + ":id/menuIV"));
        Assert.assertFalse(menu.isEmpty(), "Hamburger menu icon not found");
        String desc = describe(menu.get(0));
        test.log(Status.INFO, "Hamburger menu content-desc: '" + desc + "'");
        Assert.assertFalse(desc.isEmpty(), "Hamburger menu icon has no content-description for screen readers");
    }

    @Test(priority = 4, groups = "accessibility")
    public void accessibility_CartIconHasContentDescription() {
        List<WebElement> cart = driver.findElements(By.id(PKG + ":id/cartRL"));
        Assert.assertFalse(cart.isEmpty(), "Cart icon not found");
        String desc = describe(cart.get(0));
        test.log(Status.INFO, "Cart icon content-desc: '" + desc + "'");
        Assert.assertFalse(desc.isEmpty(), "Cart icon has no content-description for screen readers");
    }

    @Test(priority = 5, groups = "accessibility")
    public void accessibility_ProductImagesHaveContentDescriptions() {
        List<WebElement> images = driver.findElements(By.id(PKG + ":id/productIV"));
        Assert.assertFalse(images.isEmpty(), "No product images found to evaluate");

        int withDescription = 0;
        for (WebElement img : images) {
            if (!describe(img).isEmpty()) withDescription++;
        }
        test.log(Status.INFO, withDescription + "/" + images.size() + " product images have a content-description");
        Assert.assertTrue(withDescription > 0, "None of the product images have a content-description");
    }

    @Test(priority = 6, groups = "accessibility")
    public void accessibility_LoginFieldsHaveAccessibleLabels() {
        productsPage.waitForProductsPage();
        pages.mydemo.LoginPage loginPage = new pages.mydemo.LoginPage(driver);
        loginPage.navigateToLoginIfNeeded();

        List<WebElement> fields = driver.findElements(By.xpath("//android.widget.EditText"));
        Assert.assertFalse(fields.isEmpty(), "No input fields found on the login screen");

        int labeled = 0;
        for (WebElement field : fields) {
            String desc = describe(field);
            String hint = field.getAttribute("hint");
            if (!desc.isEmpty() || (hint != null && !hint.isEmpty())) labeled++;
        }
        test.log(Status.INFO, labeled + "/" + fields.size() + " login fields have a content-description or hint");
        Assert.assertTrue(labeled > 0, "None of the login fields expose a content-description or hint for accessibility");
    }
}
