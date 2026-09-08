package signup;

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

public class SignupUsabilityTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String URL = "https://recipe-finder-two-murex.vercel.app/";

    // Stores Test ID -> Result Status (PASSED / FAILED / SKIPPED)
    private static final Map<String, String> testResults = new LinkedHashMap<>();

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);
    }

    // Helper method to open Signup panel reliably
    private void openSignupForm() throws InterruptedException {
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        clickElement(signupBtn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        Thread.sleep(1200); // Visual delay after loading form
    }

    // Helper method to click elements smoothly
    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // ==========================================
    // SIGN-UP USABILITY TESTS
    // ==========================================

    @Test(priority = 1, description = "SU-U-001: Verify input fields are clearly identifiable")
    public void testInputFieldsClearlyIdentifiable() throws InterruptedException {
        // Step 1: Load Signup form
        openSignupForm();

        // Step 2: Locate all input fields
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement pwInput = driver.findElement(By.id("pw"));
        WebElement confirmPwInput = driver.findElement(By.id("pw_c"));

        WebElement[] fields = {nameInput, emailInput, pwInput, confirmPwInput};
        String[] fieldNames = {"Username", "Email", "Password", "Confirm Password"};

        // Step 3: Check each input field is clearly visible and distinct
        for (int i = 0; i < fields.length; i++) {
            Assert.assertTrue(fields[i].isDisplayed(), fieldNames[i] + " input field is not visible");
        }
        Thread.sleep(1000); // Visual delay to inspect field visibility

        // Step 4: Confirm placeholder or label presence
        for (int i = 0; i < fields.length; i++) {
            String placeholder = fields[i].getAttribute("placeholder");
            String ariaLabel = fields[i].getAttribute("aria-label");
            boolean hasDescriptor = (placeholder != null && !placeholder.trim().isEmpty()) ||
                    (ariaLabel != null && !ariaLabel.trim().isEmpty());
            Assert.assertTrue(hasDescriptor, fieldNames[i] + " field lacks a visible placeholder or aria label indicator");
        }

        // Step 5: Verify required field indicators
        for (int i = 0; i < fields.length; i++) {
            String isRequired = fields[i].getAttribute("required");
            System.out.println(fieldNames[i] + " required attribute: " + isRequired);
        }

        // Step 6: Test focus states and visual distinction sequentially
        for (int i = 0; i < fields.length; i++) {
            fields[i].click();
            // Highlight active focus boundary visually
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='2px solid #007bff'", fields[i]);
            Thread.sleep(1000); // Visual pause to observe focus highlight
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border=''", fields[i]);
        }
    }

    @Test(priority = 2, description = "SU-U-002: Verify Sign-in navigation is easy to understand")
    public void testSigninNavigationEasyToUnderstand() throws InterruptedException {
        // Step 1: Load Signup form
        openSignupForm();

        // Step 2: Locate Sign-in navigation option
        WebElement signinToggleBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin-btn")));

        // Step 3: Check visibility & button text clarity
        Assert.assertTrue(signinToggleBtn.isDisplayed(), "Sign-in navigation option is not visible");
        String btnText = signinToggleBtn.getText().trim();
        Assert.assertFalse(btnText.isEmpty(), "Sign-in navigation link/button text is blank");

        // Highlight navigation entry point
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.outline='2px solid green'", signinToggleBtn);
        Thread.sleep(1500); // Pause to inspect navigation entry point
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.outline=''", signinToggleBtn);

        // Step 4: Execute navigation to Sign-in page
        clickElement(signinToggleBtn);

        // Step 5: Verify navigation path succeeded
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        Assert.assertTrue(usernameInput.isDisplayed(), "Navigation failed; Sign-in username field was not displayed");

        Thread.sleep(1500); // Pause to observe successful Sign-in page state
    }

    @Test(priority = 3, description = "SU-U-003: Verify Signup button is easy to identify")
    public void testSignupButtonEasyToIdentify() throws InterruptedException {
        // Step 1: Load Signup form
        openSignupForm();

        // Step 2: Locate Signup submit button
        WebElement submitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")));

        // Step 3: Verify button visibility, clickability, and dimensions
        Assert.assertTrue(submitBtn.isDisplayed(), "Signup submit button is not visible");
        Assert.assertTrue(submitBtn.isEnabled(), "Signup submit button is disabled");
        Assert.assertTrue(submitBtn.getSize().getWidth() > 0 && submitBtn.getSize().getHeight() > 0,
                "Signup submit button has invalid dimensions");

        // Step 4: Verify label clarity
        String buttonText = submitBtn.getText().trim();
        Assert.assertFalse(buttonText.isEmpty(), "Signup button text is empty");

        // Step 5: Highlight button visually to confirm prominent placement
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid orange'", submitBtn);
        Thread.sleep(2000); // Pause for live visual inspection of button positioning
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border=''", submitBtn);
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

        Thread.sleep(2000); // Delay prior to browser teardown for visual feedback

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n##################################################");
        System.out.println("       SIGNUP USABILITY TEST SUMMARY REPORT        ");
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