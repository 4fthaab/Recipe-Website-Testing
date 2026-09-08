package login;

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

public class SigninFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "http://localhost:3000/";

    private static final String TEST_USERNAME = "testuser123";
    private static final String TEST_EMAIL = "testuser123@gmail.com";
    private static final String TEST_PASSWORD = "Test@123";
    private static final String NEW_PASSWORD = "NewPassword123!";

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
    // SIGN-IN FUNCTIONAL TESTS
    // ==========================================

    @Test(priority = 1, description = "SI-F-001: Login with Valid Credentials")
    public void testLoginWithValidCredentials() throws InterruptedException {
        // Register account first to ensure valid credentials exist
        WebElement signupToggleBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        signupToggleBtn.click();

        driver.findElement(By.id("name")).sendKeys(TEST_USERNAME);
        driver.findElement(By.id("email")).sendKeys(TEST_EMAIL);
        driver.findElement(By.id("pw")).sendKeys(TEST_PASSWORD);
        driver.findElement(By.id("pw_c")).sendKeys(TEST_PASSWORD);

        Thread.sleep(1000); // Visual pause before registration submit
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Switch back to Sign-in panel
        WebElement signinToggle = wait.until(ExpectedConditions.elementToBeClickable(By.id("signin-btn")));
        try {
            signinToggle.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signinToggle);
        }

        // Fill sign-in credentials
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        WebElement passwordInput = driver.findElement(By.id("userPw"));

        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys(TEST_PASSWORD);

        Thread.sleep(1500); // Visual pause to view filled inputs

        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/button")).click();

        wait.until(d -> !d.getCurrentUrl().equals(URL));
        Assert.assertNotEquals(driver.getCurrentUrl(), URL, "User was not redirected after valid login");

        Thread.sleep(2000); // Visual pause to view redirection result
    }

    @Test(priority = 2, description = "SI-F-002: Navigate to Forgot Password")
    public void testNavigateToForgotPassword() throws InterruptedException {
        WebElement forgotPwLink = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Forgot")));

        Thread.sleep(1000); // Visual pause before click
        forgotPwLink.click();

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        Assert.assertTrue(emailInput.isDisplayed(), "Password recovery form/email field is not visible");

        emailInput.sendKeys(TEST_EMAIL);
        Thread.sleep(1000); // Visual pause before reset request submit

        driver.findElement(By.cssSelector("form button")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        WebElement newPwInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pw")));
        newPwInput.sendKeys(NEW_PASSWORD);

        Thread.sleep(1000); // Visual pause before final password submission
        driver.findElement(By.cssSelector("form button")).click();

        Alert finalAlert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = finalAlert.getText().toLowerCase();
        Assert.assertTrue(alertText.contains("success") || alertText.contains("updated"), "Password reset failed");
        finalAlert.accept();

        Thread.sleep(2000); // Visual pause to observe result
    }

    @Test(priority = 3, description = "SI-F-003: Navigate to Signup")
    public void testNavigateToSignup() throws InterruptedException {
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));

        Thread.sleep(1000); // Visual pause before navigating
        signupBtn.click();

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        Assert.assertTrue(nameInput.isDisplayed(), "Signup form name field was not loaded");

        Thread.sleep(2000); // Visual pause to verify form display
    }

    @Test(priority = 4, description = "SI-F-004: Password Visibility Toggle")
    public void testPasswordVisibilityToggle() throws InterruptedException {
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userPw")));
        WebElement toggleIcon = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/div[1]/span/i"));

        // Step 1: Default masked state
        Assert.assertEquals(passwordInput.getAttribute("type"), "password", "Password field is not masked by default");

        passwordInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1000); // Visual pause showing entered masked password

        // Step 2: Unmask password
        toggleIcon.click();
        Assert.assertEquals(passwordInput.getAttribute("type"), "text", "Password was not revealed on click");
        Assert.assertEquals(passwordInput.getAttribute("value"), TEST_PASSWORD, "Password value changed after toggle");
        Thread.sleep(1500); // Visual pause showing revealed text

        // Step 3: Re-mask password
        toggleIcon.click();
        Assert.assertEquals(passwordInput.getAttribute("type"), "password", "Password was not re-masked on click");
        Thread.sleep(1500); // Visual pause showing masked text again
    }

    @Test(priority = 5, description = "SI-F-005: Social Sign-In Options")
    public void testSocialSignInOptions() throws InterruptedException {
        By[] socialButtons = {
                By.xpath("/html/body/div/div/div[1]/div[2]/div/div/div/i[2]")
        };

        for (By buttonLocator : socialButtons) {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));

            Thread.sleep(1000); // Visual pause before clicking social icon
            button.click();

            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                Thread.sleep(1000);
                alert.accept();
            } catch (Exception e) {
                // Ignore if no alert is triggered by social button
            }
        }

        Thread.sleep(2000); // Visual pause after interaction
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

        // Delay prior to browser teardown for visual verification
        Thread.sleep(2000);

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n##################################################");
        System.out.println("       SIGN-IN FUNCTIONAL TEST SUMMARY REPORT     ");
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