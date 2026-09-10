package utils;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.lang.reflect.Field;

public class TestNGFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        String cleanTestName = className + " - " + testName;
        Throwable exception = result.getThrowable();
        long executionTime = result.getEndMillis() - result.getStartMillis();

        // 1. Extract Driver & Browser Version Details
        WebDriver driver = null;
        String browserName = "Unknown Browser";
        String browserVersion = "Unknown Version";

        try {
            Object instance = result.getInstance();
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
        String failureDetail = exception != null ? exception.getMessage() : "No exception message available";

        // 5. Print Detailed Terminal Output (Format suitable for Jira)
        System.out.println("\n========================================================================");
        System.out.println("                   TEST FAILURE REPORT (TestNG)                         ");
        System.out.println("========================================================================");
        System.out.println("Test Case         : " + cleanTestName);
        System.out.println("Execution Time    : " + executionTime + " ms");
        System.out.println("OS / Arch         : " + osName + " (" + osArch + ")");
        System.out.println("Java Version      : " + javaVersion);
        System.out.println("Browser / Version : " + browserName + " " + browserVersion);
        System.out.println("Screenshot File   : " + screenshotPath);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("ASSERTION / FAILURE DETAILS:");
        System.out.println(failureDetail);
        System.out.println("========================================================================\n");

        // 6. Append to HTML Report
        HtmlReportManager.logFailureCard("TestNG", cleanTestName, exception, screenshotPath, executionTime);
    }
}