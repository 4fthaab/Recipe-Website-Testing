package signup;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class SignupValidationNegativeTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "http://localhost:3000/";

    // Stores Test ID -> Result Status (PASSED / FAILED / SKIPPED)
    private static final Map<String, String> testResults = new LinkedHashMap<>();

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);
    }

    // ==========================================
    // SIGN-UP VALIDATION & NEGATIVE TESTS
    // ==========================================

    @Test(priority = 1, description = "SU-V-001: Submit signup form with all fields empty")
    public void testSignupEmptyFields() throws InterruptedException {
        // Step 1: Switch to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        try {
            signupBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupBtn);
        }
        Thread.sleep(1000); // Visual pause after panel transition

        // Step 2: Ensure fields are clear
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("pw")).clear();
        driver.findElement(By.id("pw_c")).clear();
        Thread.sleep(1000); // Visual pause showing empty form

        // Step 3: Click Signup submit button
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();
        Thread.sleep(1500); // Visual pause to observe validation behavior

        // Step 4: Verify validation (JS Alert OR HTML5 validationMessage)
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000);
            alert.accept();
        } catch (Exception e) {
            String validationMsg = nameInput.getAttribute("validationMessage");
            Assert.assertNotNull(validationMsg, "No validation message displayed for empty fields");
            Assert.assertFalse(validationMsg.trim().isEmpty(), "Validation message attribute is empty for required fields");
        }

        Assert.assertEquals(driver.getCurrentUrl(), URL, "Account was created with empty fields");
    }

    @Test(priority = 2, description = "SU-V-002: Signup with mismatched passwords")
    public void testSignupMismatchedPasswords() throws InterruptedException {
        // Step 1: Switch to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        try {
            signupBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupBtn);
        }
        Thread.sleep(1000); // Visual pause after panel transition

        // Step 2: Fill form with mismatched passwords
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        nameInput.sendKeys("testuser123");
        Thread.sleep(500);

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.clear();
        emailInput.sendKeys("test@example.com");
        Thread.sleep(500);

        WebElement pwInput = driver.findElement(By.id("pw"));
        pwInput.clear();
        pwInput.sendKeys("Test@123");
        Thread.sleep(500);

        WebElement confirmPwInput = driver.findElement(By.id("pw_c"));
        confirmPwInput.clear();
        confirmPwInput.sendKeys("Test@456");
        Thread.sleep(1000); // Visual pause prior to submission

        // Step 3: Click Signup submit button
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();
        Thread.sleep(1500); // Visual pause to observe submission result

        // Step 4: Verify password mismatch error message
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000);
            String alertText = alert.getText().toLowerCase();
            Assert.assertTrue(alertText.contains("match") || alertText.contains("password") || alertText.contains("error"),
                    "Alert text did not indicate password mismatch error: " + alert.getText());
            alert.accept();
        } catch (Exception e) {
            String validationMsg = confirmPwInput.getAttribute("validationMessage");
            Assert.assertNotNull(validationMsg, "No validation message displayed for mismatched passwords");
            Assert.assertFalse(validationMsg.trim().isEmpty(), "Validation message attribute is empty for password mismatch");
        }

        Assert.assertEquals(driver.getCurrentUrl(), URL, "Account was created despite mismatched passwords");
    }

    @Test(priority = 3, description = "SU-V-003: Signup with invalid email")
    public void testSignupInvalidEmail() throws InterruptedException {
        // Step 1: Switch to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        try {
            signupBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupBtn);
        }
        Thread.sleep(1000); // Visual pause after panel transition

        // Step 2: Fill form with invalid email format (missing '@')
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        nameInput.sendKeys("testuser123");
        Thread.sleep(500);

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.clear();
        emailInput.sendKeys("testexample.com"); // Invalid email
        Thread.sleep(500);

        WebElement pwInput = driver.findElement(By.id("pw"));
        pwInput.clear();
        pwInput.sendKeys("Test@123");
        Thread.sleep(500);

        WebElement confirmPwInput = driver.findElement(By.id("pw_c"));
        confirmPwInput.clear();
        confirmPwInput.sendKeys("Test@123");
        Thread.sleep(1000); // Visual pause prior to submission

        // Step 3: Click Signup submit button
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();
        Thread.sleep(1500); // Visual pause to observe submission result

        // Step 4: Verify email format validation message
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000);
            String alertText = alert.getText().toLowerCase();
            Assert.assertTrue(alertText.contains("email") || alertText.contains("invalid") || alertText.contains("error"),
                    "Alert text did not indicate invalid email error: " + alert.getText());
            alert.accept();
        } catch (Exception e) {
            String validationMsg = emailInput.getAttribute("validationMessage");
            Assert.assertNotNull(validationMsg, "Form validation failed: No browser alert or HTML5 validation message displayed for invalid email format");
            Assert.assertFalse(validationMsg.trim().isEmpty(), "Form validation failed: HTML5 validationMessage attribute was empty for invalid email");
        }

        Assert.assertEquals(driver.getCurrentUrl(), URL, "Account was created with an invalid email");
    }

    // ==========================================
    // EXECUTION LOGGING & SUMMARY REPORT
    // ==========================================

    @AfterMethod
    public void tearDown(ITestResult result) throws InterruptedException {
        String testIdAndDesc = result.getMethod().getDescription();
        if (testIdAndDesc == null || testIdAndDesc.isEmpty()) {
            testIdAndDesc = result.getMethod().getMethodName();
        }

        String statusStr;
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                statusStr = "PASSED";
                break;
            case ITestResult.FAILURE:
                statusStr = "FAILED";
                break;
            case ITestResult.SKIP:
                statusStr = "SKIPPED";
                break;
            default:
                statusStr = "UNKNOWN";
                break;
        }

        testResults.put(testIdAndDesc, statusStr);

        System.out.println("==================================================");
        System.out.println("EXECUTED: " + testIdAndDesc);
        System.out.println("STATUS  : [" + statusStr + "]");
        if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null) {
            System.out.println("REASON  : " + result.getThrowable().getMessage());
        }
        System.out.println("==================================================\n");

        Thread.sleep(2000); // Visual delay before closing browser

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n##################################################");
        System.out.println("      SIGNUP VALIDATION TEST SUMMARY REPORT      ");
        System.out.println("##################################################");
        System.out.printf("%-12s | %-45s | %-8s%n", "TEST ID", "TEST DESCRIPTION", "STATUS");
        System.out.println("-------------------------------------------------------------------------");

        int total = testResults.size();
        int passed = 0;
        int failed = 0;

        for (Map.Entry<String, String> entry : testResults.entrySet()) {
            String fullDesc = entry.getKey();
            String status = entry.getValue();

            String testId = "N/A";
            String description = fullDesc;

            if (fullDesc.contains(":")) {
                String[] parts = fullDesc.split(":", 2);
                testId = parts[0].trim();
                description = parts[1].trim();
            }

            System.out.printf("%-12s | %-45s | %-8s%n", testId, description, status);

            if ("PASSED".equals(status)) {
                passed++;
            } else if ("FAILED".equals(status)) {
                failed++;
            }
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("TOTAL TESTS: " + total + " | PASSED: " + passed + " | FAILED: " + failed);
        System.out.println("##################################################\n");
    }
}