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

public class SigninValidationTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "http://localhost:3000/";

    private static final String TEST_USERNAME = "testuser123";
    private static final String TEST_EMAIL = "testuser123@gmail.com";
    private static final String TEST_PASSWORD = "Test@123";
    private static final String WRONG_PASSWORD = "Wrong@123";

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
    // SIGN-IN VALIDATION TEST CASES
    // ==========================================

    @Test(priority = 1, description = "SI-V-002: Validation for Empty Fields")
    public void testSubmitEmptyFields() throws InterruptedException {
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        WebElement passwordInput = driver.findElement(By.id("userPw"));

        usernameInput.clear();
        passwordInput.clear();

        Thread.sleep(1000); // Visual pause before submitting empty form

        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/button")).click();

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000); // Pause to see alert popup
            alert.accept();
        } catch (Exception e) {
            String validationMsg = usernameInput.getAttribute("validationMessage");
            Assert.assertNotNull(validationMsg, "No validation message appeared for empty fields");
        }

        Thread.sleep(1500); // Visual pause to confirm user remains on page
        Assert.assertEquals(driver.getCurrentUrl(), URL, "User was redirected despite submitting empty fields");
    }

    @Test(priority = 2, description = "SI-V-003: Validation for Empty Password")
    public void testSubmitEmptyPassword() throws InterruptedException {
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        WebElement passwordInput = driver.findElement(By.id("userPw"));

        usernameInput.clear();
        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.clear();

        Thread.sleep(1000); // Visual pause showing filled username and empty password

        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/button")).click();

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000); // Pause to view alert
            alert.accept();
        } catch (Exception e) {
            String validationMsg = passwordInput.getAttribute("validationMessage");
            Assert.assertNotNull(validationMsg, "No validation message appeared for empty password field");
        }

        Thread.sleep(1500); // Visual pause to confirm submission blocked
        Assert.assertEquals(driver.getCurrentUrl(), URL, "User was redirected despite empty password");
    }

    @Test(priority = 3, description = "SI-V-001: Login with Incorrect Password")
    public void testLoginWithIncorrectPassword() throws InterruptedException {
        // Step 1: Register account to ensure valid username exists
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        signupBtn.click();

        driver.findElement(By.id("name")).sendKeys(TEST_USERNAME);
        driver.findElement(By.id("email")).sendKeys(TEST_EMAIL);
        driver.findElement(By.id("pw")).sendKeys(TEST_PASSWORD);
        driver.findElement(By.id("pw_c")).sendKeys(TEST_PASSWORD);

        Thread.sleep(1000); // Visual pause before submitting registration
        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();

        try {
            Alert regAlert = wait.until(ExpectedConditions.alertIsPresent());
            regAlert.accept();
        } catch (Exception ignored) {
            // Continuation if registration uses inline notifications
        }

        // Step 2: Navigate back to Sign-In
        WebElement signinToggle = wait.until(ExpectedConditions.elementToBeClickable(By.id("signin-btn")));
        try {
            signinToggle.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signinToggle);
        }

        // Step 3: Attempt sign in with wrong password
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        WebElement passwordInput = driver.findElement(By.id("userPw"));

        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys(WRONG_PASSWORD);

        Thread.sleep(1500); // Visual pause before clicking submit with wrong credentials

        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/button")).click();

        // Step 4: Locate inline error message text on the page DOM instead of checking for browser Alert
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') or " +
                        "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') or " +
                        "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'wrong') or " +
                        "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'error')]")
        ));

        Thread.sleep(2000); // Visual pause to view error message rendered on UI

        Assert.assertTrue(errorElement.isDisplayed(), "Inline error message was not displayed");

        Thread.sleep(1500); // Visual pause to confirm access denied & user remains on sign-in
        Assert.assertEquals(driver.getCurrentUrl(), URL, "User was redirected despite entering wrong password");
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

        Thread.sleep(2000); // Visual delay before quitting browser

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n##################################################");
        System.out.println("      SIGN-IN VALIDATION TEST SUMMARY REPORT      ");
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