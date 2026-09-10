package utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import java.lang.reflect.Field;

public class JUnitFailureWatcher implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String className = context.getRequiredTestClass().getSimpleName();
        String testName = context.getDisplayName();
        String cleanTestName = className + " - " + testName;

        // 1. Extract Driver & Browser Version Details
        WebDriver driver = null;
        String browserName = "Unknown Browser";
        String browserVersion = "Unknown Version";

        try {
            Object instance = context.getRequiredTestInstance();
            Field field = instance.getClass().getDeclaredField("driver");
            field.setAccessible(true);
            driver = (WebDriver) field.get(instance);

            if (driver instanceof HasCapabilities) {
                Capabilities caps = ((HasCapabilities) driver).getCapabilities();
                browserName = caps.getBrowserName();
                browserVersion = caps.getBrowserVersion();
            }
        } catch (Exception ignored) {}

        // 2. Capture Screenshot
        String screenshotPath = ScreenshotUtil.captureScreenshot(driver, cleanTestName);

        // 3. System & OS Details
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");

        // 4. Extract Failure Details / Assertion Messages
        String failureDetail = cause != null ? cause.getMessage() : "No exception message available";

        // 5. Print Detailed Terminal Output (Format suitable for Jira)
        System.out.println("\n========================================================================");
        System.out.println("                   TEST FAILURE REPORT (JUnit 5)                        ");
        System.out.println("========================================================================");
        System.out.println("Test Case         : " + cleanTestName);
        System.out.println("OS / Arch         : " + osName + " (" + osArch + ")");
        System.out.println("Java Version      : " + javaVersion);
        System.out.println("Browser / Version : " + browserName + " " + browserVersion);
        System.out.println("Screenshot File   : " + screenshotPath);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("ASSERTION / FAILURE DETAILS:");
        System.out.println(failureDetail);
        System.out.println("========================================================================\n");

        // 6. Append to HTML Report
        HtmlReportManager.logFailureCard("JUnit 5", cleanTestName, cause, screenshotPath, 0L);
    }
}