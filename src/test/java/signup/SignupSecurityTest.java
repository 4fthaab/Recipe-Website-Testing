package signup;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

public class SignupSecurityTest {

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
    // SIGN-UP SECURITY TEST CASES
    // ==========================================

    @Test(priority = 1, description = "SU-S-001: Verify password is masked")
    public void testSignupPasswordIsMasked() throws InterruptedException {
        // Step 1: Switch to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        clickElement(signupBtn);
        Thread.sleep(1000); // Visual pause after panel transition

        // Step 2: Enter password into Password and Confirm Password fields
        WebElement pwInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pw")));
        WebElement confirmPwInput = driver.findElement(By.id("pw_c"));

        pwInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(500); // Visual pause showing password entry
        confirmPwInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1000); // Visual pause showing confirm password entry

        // Step 3: Verify both input fields mask input by default
        Assert.assertEquals(pwInput.getAttribute("type"), "password", "Password field is not masked by default");
        Assert.assertEquals(confirmPwInput.getAttribute("type"), "password", "Confirm password field is not masked by default");
        Thread.sleep(1000); // Visual pause for assertion check
    }

    @Test(priority = 2, description = "SU-S-002: Verify password is not exposed in URL")
    public void testSignupPasswordNotInUrl() throws InterruptedException {
        // Step 1: Switch to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        clickElement(signupBtn);
        Thread.sleep(1000); // Visual pause after panel transition

        // Step 2: Enter valid sign-up credentials
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.sendKeys(TEST_USERNAME);
        Thread.sleep(500);

        driver.findElement(By.id("email")).sendKeys(TEST_EMAIL);
        Thread.sleep(500);

        driver.findElement(By.id("pw")).sendKeys(TEST_PASSWORD);
        Thread.sleep(500);

        driver.findElement(By.id("pw_c")).sendKeys(TEST_PASSWORD);
        Thread.sleep(1000); // Visual pause showing completed form

        // Step 3: Submit the Sign-up form
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();
        Thread.sleep(1500); // Visual pause after submission

        // Step 4: Handle confirmation alert if present
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000); // Pause to view alert
            alert.accept();
        } catch (Exception ignored) {}

        // Step 5: Verify browser URL does not expose raw password or parameter values
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains(TEST_PASSWORD), "Password exposed in URL address bar: " + currentUrl);
        Assert.assertFalse(currentUrl.contains("pw="), "Password parameter leaked in URL query string: " + currentUrl);
        Thread.sleep(1000); // Visual pause for assertion check
    }

    @Test(priority = 3, description = "SU-S-003: Verify password fields do not expose sensitive information unnecessarily")
    public void testSignupCredentialsNotExposedInPage() throws InterruptedException {
        // Step 1: Switch to Sign-up panel
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        clickElement(signupBtn);
        Thread.sleep(1000); // Visual pause after panel transition

        // Step 2: Enter password without clicking eye icon
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        WebElement pwInput = driver.findElement(By.id("pw"));
        WebElement confirmPwInput = driver.findElement(By.id("pw_c"));

        pwInput.sendKeys(TEST_PASSWORD);
        confirmPwInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1000); // Visual pause showing entered password

        // Step 3: Change field focus to observe if masking persists without user toggling
        nameInput.click();
        Thread.sleep(1000); // Visual pause observing un-toggled state

        // Verify fields remain strictly masked
        Assert.assertEquals(pwInput.getAttribute("type"), "password", "Password field unmasked without clicking visibility icon");
        Assert.assertEquals(confirmPwInput.getAttribute("type"), "password", "Confirm password field unmasked without clicking visibility icon");

        // Step 4: Inspect DOM source code to ensure sensitive credentials are not exposed
        String pageSource = driver.getPageSource();
        Assert.assertFalse(pageSource.contains("admin123"), "Hardcoded credentials found in client DOM source");
        Thread.sleep(1000); // Visual pause prior to test finish
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
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
        System.out.println("        SIGN-UP SECURITY SUITE SUMMARY REPORT     ");
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