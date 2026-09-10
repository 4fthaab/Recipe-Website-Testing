package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "Test Results Screenshots/";

    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            return "WebDriver instance was null; screenshot skipped.";
        }

        try {
            // Wait briefly for DOM to settle before snapshot
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
                wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
            } catch (Exception ignored) {
                // Proceed to snapshot even if wait times out
            }

            Files.createDirectories(Paths.get(SCREENSHOT_DIR));

            // Clean filename without timestamps -> directly overwrites previous file
            String cleanFileName = testName.replaceAll("[^a-zA-Z0-9._-]", "_") + ".png";
            String filePath = SCREENSHOT_DIR + cleanFileName;

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);

            // Directly overrides the screenshot file if it already exists
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return filePath;
        } catch (IOException e) {
            return "Failed to save screenshot: " + e.getMessage();
        }
    }
}