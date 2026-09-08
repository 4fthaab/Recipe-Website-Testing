package login;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
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

public class SigninSecurityTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "http://localhost:3000/";

    private static final String TEST_USERNAME = "testuser123";
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
    // SECURITY TEST CASES
    // ==========================================

    @Test(priority = 1, description = "SI-S-001: Verify Password Is Masked")
    public void testPasswordIsMasked() throws InterruptedException {
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        WebElement passwordInput = driver.findElement(By.id("userPw"));

        // Step 1: Type password and confirm default masking
        passwordInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1500); // Pause to observe masked characters in field
        Assert.assertEquals(passwordInput.getAttribute("type"), "password", "Password is not masked by default");

        // Step 2: Change focus to username field
        usernameInput.click();
        Thread.sleep(1000); // Pause to verify masking remains after focus change
        Assert.assertEquals(passwordInput.getAttribute("type"), "password", "Password unmasked after focus change");

        // Step 3: Click submit and check masking post-submission
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/button")).click();
        Thread.sleep(1000); // Pause to observe submit action

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000); // Pause to view browser alert
            alert.accept();
        } catch (Exception ignored) {
            // Inline error message scenario
        }

        Thread.sleep(1000); // Pause to verify masking after form submission attempt
        Assert.assertEquals(passwordInput.getAttribute("type"), "password", "Password unmasked after form submission");
    }

    @Test(priority = 2, description = "SI-S-002: Verify Password Is Not Exposed in URL")
    public void testPasswordNotInUrl() throws InterruptedException {
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        WebElement passwordInput = driver.findElement(By.id("userPw"));

        // Step 1: Enter credentials
        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1500); // Pause to inspect entered credentials before submit

        // Step 2: Submit credentials
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/button")).click();

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000); // Pause to view alert
            alert.accept();
        } catch (Exception ignored) {
            // Continuation for inline handling or redirect
        }

        Thread.sleep(2000); // Pause to observe address bar after submit

        // Step 3: Inspect browser URL
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains(TEST_PASSWORD), "Password exposed in URL");
        Assert.assertFalse(currentUrl.contains("userPw="), "Password leaked in query parameters");
    }

    @Test(priority = 3, description = "SI-S-003: Verify Credentials Are Not Exposed in the Page")
    public void testCredentialsNotExposedInPage() throws InterruptedException {
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userPw")));

        // Step 1: Type password
        passwordInput.sendKeys(TEST_PASSWORD);
        Thread.sleep(1500); // Pause to observe typed password state

        // Step 2: Verify field attribute type is masked
        Assert.assertEquals(passwordInput.getAttribute("type"), "password", "Password field exposed in text mode");

        // Step 3: Inspect DOM source for hardcoded plain-text credentials
        Thread.sleep(1000); // Pause before DOM check
        Assert.assertFalse(driver.getPageSource().contains("admin123"), "Hardcoded credentials found in DOM source");
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

        Thread.sleep(2000); // Delay before browser closes

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n##################################################");
        System.out.println("        SECURITY TEST SUITE SUMMARY REPORT        ");
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