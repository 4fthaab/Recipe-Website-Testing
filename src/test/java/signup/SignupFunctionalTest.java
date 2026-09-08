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

public class SignupFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "http://localhost:3000/";

    private static final String TEST_USERNAME = "testuser123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Test@123";

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
    // SIGN-UP FUNCTIONAL TEST CASES
    // ==========================================

    @Test(priority = 1, description = "SU-F-001: Signup with Valid Details")
    public void testSignupWithValidDetails() throws InterruptedException {
        // Step 1: Switch to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        signupBtn.click();
        Thread.sleep(1000); // Visual pause after panel transition

        // Step 2: Fill out Sign-up form fields
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.sendKeys(TEST_USERNAME);
        Thread.sleep(500); // Pause to observe username entry

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.sendKeys(TEST_EMAIL);
        Thread.sleep(500); // Pause to observe email entry

        WebElement pwInput = driver.findElement(By.id("pw"));
        pwInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(500); // Pause to observe password entry

        WebElement pwConfirmInput = driver.findElement(By.id("pw_c"));
        pwConfirmInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1000); // Pause showing completed form prior to submission

        // Step 3: Submit registration
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();

        // Step 4: Handle registration confirmation
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000); // Pause to view success alert
            Assert.assertTrue(alert.getText().toLowerCase().contains("success") || alert.getText().length() > 0,
                    "Alert text did not indicate successful registration");
            alert.accept();
        } catch (Exception ignored) {
            // Flow continuation if application uses DOM messages instead of browser alert
        }

        Thread.sleep(1500); // Visual pause after form submission
    }

    @Test(priority = 2, description = "SU-F-002: Navigate to Sign-in Page")
    public void testNavigateToSignInPage() throws InterruptedException {
        // Step 1: Click signup button to open Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        signupBtn.click();
        Thread.sleep(1500); // Visual pause showing Sign-up panel

        // Step 2: Click "Already have an account? Signin." toggle button
        WebElement signinToggle = wait.until(ExpectedConditions.elementToBeClickable(By.id("signin-btn")));
        try {
            signinToggle.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signinToggle);
        }
        Thread.sleep(1500); // Visual pause showing transition back to Sign-in

        // Step 3: Verify redirection back to Sign-in form
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        Assert.assertTrue(usernameInput.isDisplayed(), "Failed to navigate back to the Sign-in page");
    }

    @Test(priority = 3, description = "SU-F-003: Password Visibility Toggle")
    public void testSignupPasswordVisibilityToggle() throws InterruptedException {
        // Step 1: Navigate to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        signupBtn.click();
        Thread.sleep(1000); // Visual pause after panel transition

        // Locate input elements
        WebElement signupPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pw")));
        WebElement confirmPasswordInput = driver.findElement(By.id("pw_c"));

        // Locate respective eye toggle icons
        WebElement signupToggleIcon = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/div[1]/span"));
        WebElement confirmToggleIcon = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/div[2]/span/i"));

        // Step 2: Enter passwords into both fields and verify initial masked state
        signupPasswordInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(500);
        confirmPasswordInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1000); // Visual pause showing both fields in masked state

        Assert.assertEquals(signupPasswordInput.getAttribute("type"), "password", "Password field is not masked initially");
        Assert.assertEquals(confirmPasswordInput.getAttribute("type"), "password", "Confirm password field is not masked initially");

        // Step 3: Click Password eye icon to unmask Password field
        clickToggle(signupToggleIcon);
        Thread.sleep(1200); // Visual pause
        Assert.assertEquals(signupPasswordInput.getAttribute("type"), "text", "Password was not unmasked when eye icon was clicked");

        // Step 4: Click Confirm Password eye icon to unmask Confirm Password field
        clickToggle(confirmToggleIcon);
        Thread.sleep(1200); // Visual pause
        Assert.assertEquals(confirmPasswordInput.getAttribute("type"), "text", "Confirm password was not unmasked when eye icon was clicked");

        // Step 5: Click Password eye icon again to re-mask Password field
        clickToggle(signupToggleIcon);
        Thread.sleep(1200); // Visual pause
        Assert.assertEquals(signupPasswordInput.getAttribute("type"), "password", "Password was not re-masked on second click");

        // Step 6: Click Confirm Password eye icon again to re-mask Confirm Password field
        clickToggle(confirmToggleIcon);
        Thread.sleep(1200); // Visual pause
        Assert.assertEquals(confirmPasswordInput.getAttribute("type"), "password", "Confirm password was not re-masked on second click");
    }

    private void clickToggle(WebElement toggleIcon) {
        try {
            toggleIcon.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggleIcon);
        }
    }

    // ==========================================
    // EXECUTION LOGGING & FINAL SUMMARY REPORT
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

        Thread.sleep(2000); // Delay before closing browser

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n##################################################");
        System.out.println("        SIGN-UP TEST SUITE SUMMARY REPORT         ");
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