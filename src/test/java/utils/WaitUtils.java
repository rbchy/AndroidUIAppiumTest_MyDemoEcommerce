package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Small wait/retry helpers used across the test suite.
 * Recreated on 2026-07-15 after the original class was missing from the project,
 * causing "package utils does not exist" compile errors across the test sources.
 */
public class WaitUtils {

    private WaitUtils() {
        // no instances
    }

    public static WebElement waitForVisible(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement retryFindElement(WebDriver driver, By locator, int retries) {
        NoSuchElementException lastEx = null;
        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                return driver.findElement(locator);
            } catch (NoSuchElementException e) {
                lastEx = e;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw lastEx != null ? lastEx : new NoSuchElementException("Element not found: " + locator);
    }
}
