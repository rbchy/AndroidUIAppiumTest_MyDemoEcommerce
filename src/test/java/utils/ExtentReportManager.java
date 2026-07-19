package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Singleton wrapper around ExtentReports.
 * Recreated on 2026-07-15 after the original class was missing from the project,
 * causing "package utils does not exist" compile errors across the test sources.
 */
public class ExtentReportManager {

    private static ExtentReports extent;

    private ExtentReportManager() {
        // no instances
    }

    public static synchronized ExtentReports getReportInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportDir = "test-output" + File.separator + "ExtentReports";
            new File(reportDir).mkdirs();
            String reportPath = reportDir + File.separator + "ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("MyDemo eCommerce Automation Report");
            sparkReporter.config().setReportName("Appium Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        return getReportInstance().createTest(testName);
    }
}
