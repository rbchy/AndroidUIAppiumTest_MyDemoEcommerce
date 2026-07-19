package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class MainScreenPage {

    private AndroidDriver driver;
    private final By menuItems = By.id("android:id/text1");

    public MainScreenPage(AndroidDriver driver) {
        this.driver = driver;
    }

    public int getMenuItemCount() {
        return driver.findElements(menuItems).size();
    }

    public List<WebElement> getAllMenuItems() {
        return driver.findElements(menuItems);
    }

    public void clickMenuItemByText(String text) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + text + "\")")).click();
    }

    public void clickMenuItemContainingText(String partialText) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiSelector().textContains(\"" + partialText + "\")")).click();
    }

    public boolean isMenuItemVisible(String text) {
        return driver.getPageSource().contains(text);
    }

    public boolean navigateToScreenWithEditText() {
        List<WebElement> items = getAllMenuItems();
        int total = items.size();
        By editTextLocator = By.xpath("//*[contains(@class, 'EditText')]");

        for (int i = 0; i < total; i++) {
            List<WebElement> freshItems = getAllMenuItems();
            if (i >= freshItems.size()) break;

            freshItems.get(i).click();

            // Poll for the EditText up to 5s instead of a single fixed sleep.
            // A fixed sleep races the screen transition on slower/cold emulators
            // (first-boot AVDs in particular) — if the transition takes longer
            // than the sleep, the check runs too early and reports a false
            // negative, which was skipping tests that would otherwise pass.
            boolean hasEditText;
            try {
                hasEditText = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(d -> !d.findElements(editTextLocator).isEmpty());
            } catch (Exception timedOut) {
                hasEditText = false;
            }

            if (hasEditText) {
                return true;
            }

            driver.navigate().back();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    /**
     * Navigates Home → Views → TextFields, then confirms an EditText is present.
     *
     * This build (appium/android-apidemos v6.0.13, downloaded from GitHub
     * Releases) has no standalone "Text" category — confirmed by inspecting
     * AndroidManifest.xml on GitHub. Text-input demo screens live elsewhere:
     * "App/Text-To-Speech", "Content/Resources/Styled Text", and
     * "Views/TextFields". Of those, only Views/TextFields exposes real
     * EditText widgets, so that's the one this targets directly instead of
     * blind-scanning every item in a category (slow and can click into
     * unrelated screens like camera/permission prompts).
     */
    public boolean navigateToTextFieldsScreen() {
        try {
            clickMenuItemByText("Views");
        } catch (Exception e) {
            System.out.println("[MainScreenPage] Could not click 'Views' category: " + e.getMessage());
            dumpPageSource("nav-fail-views-click");
            return false;
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        System.out.println("[MainScreenPage] Entered 'Views' — item count: " + getMenuItemCount());

        try {
            // Scroll to bring it into view WITHOUT clicking yet — clicking the
            // element handle returned mid-scroll was landing one row off
            // ("Switches" instead of "TextFields"), a known Android list-scroll
            // timing issue where the list is still settling when the click
            // coordinates are captured. Scroll, wait for settle, then click fresh.
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                            + ".setMaxSearchSwipes(10)"
                            + ".scrollIntoView(new UiSelector().textContains(\"TextFields\"))"));
        } catch (Exception e) {
            System.out.println("[MainScreenPage] Could not scroll 'TextFields' into view in Views: " + e.getMessage());
            dumpPageSource("nav-fail-textfields-not-found-in-views");
            try { driver.navigate().back(); } catch (Exception ignored) {}
            return false;
        }
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        try {
            clickMenuItemByText("TextFields");
        } catch (Exception e) {
            System.out.println("[MainScreenPage] Scrolled into view but fresh click on 'TextFields' failed: " + e.getMessage());
            dumpPageSource("nav-fail-textfields-click-after-scroll");
            try { driver.navigate().back(); } catch (Exception ignored) {}
            return false;
        }
        System.out.println("[MainScreenPage] Clicked 'TextFields'");

        try {
            boolean found = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> !d.findElements(
                            By.xpath("//*[contains(@class, 'EditText')]")).isEmpty());
            System.out.println("[MainScreenPage] EditText present on TextFields screen: " + found);
            if (!found) {
                dumpPageSource("nav-fail-no-edittext-on-textfields-screen");
            }
            return found;
        } catch (Exception timedOut) {
            System.out.println("[MainScreenPage] No EditText appeared within 5s on TextFields screen: "
                    + timedOut.getMessage());
            dumpPageSource("nav-fail-no-edittext-on-textfields-screen");
            return false;
        }
    }

    private void dumpPageSource(String label) {
        try {
            String src = driver.getPageSource();
            java.io.File dumpDir = new java.io.File("page-dumps");
            dumpDir.mkdirs();
            java.io.File out = new java.io.File(dumpDir, label + ".xml");
            try (java.io.FileWriter fw = new java.io.FileWriter(out)) {
                fw.write(src);
            }
            System.out.println("[MainScreenPage] Page source dumped: " + out.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("[MainScreenPage] Failed to dump page source: " + e.getMessage());
        }
    }

    public void scrollToAndClick(String text) {
        WebElement el = driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".setMaxSearchSwipes(10)"
                        + ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))"));
        el.click();
    }

    public String getCurrentAppPackage() {
        return driver.getCurrentPackage();
    }

    public void ensureOnMainScreen(AndroidDriver driver) {
        int attempts = 0;
        while (getMenuItemCount() != 12 && attempts < 5) {
            driver.navigate().back();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            attempts++;
        }
        if (getMenuItemCount() != 12) {
            driver.terminateApp("io.appium.android.apis");
            driver.activateApp("io.appium.android.apis");
            // App পুরোপুরি load হওয়া পর্যন্ত explicit wait (max 20s)
            try {
                new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(d -> d.findElements(By.id("android:id/text1")).size() >= 10);
            } catch (Exception ignored) {}
        }
    }
}
