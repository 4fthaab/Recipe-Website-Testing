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

public class SignupUiTest {

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

    // Helper method to open Sign-up panel reliably
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
    // SIGN-UP UI TEST CASES
    // ==========================================

    @Test(priority = 1, description = "SU-UI-001: Verify signup form elements are visible and aligned")
    public void testSignupFormElementsVisibleAndAligned() throws InterruptedException {
        // Step 1: Open Signup form
        openSignupForm();

        // Step 2: Locate all required form elements
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement pwInput = driver.findElement(By.id("pw"));
        WebElement confirmPwInput = driver.findElement(By.id("pw_c"));
        WebElement submitBtn = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button"));
        WebElement signinToggleBtn = driver.findElement(By.id("signin-btn"));

        // Step 3: Verify all form elements are visible
        Assert.assertTrue(nameInput.isDisplayed(), "Username field is not visible");
        Assert.assertTrue(emailInput.isDisplayed(), "Email field is not visible");
        Assert.assertTrue(pwInput.isDisplayed(), "Password field is not visible");
        Assert.assertTrue(confirmPwInput.isDisplayed(), "Confirm Password field is not visible");
        Assert.assertTrue(submitBtn.isDisplayed(), "Signup submit button is not visible");
        Assert.assertTrue(signinToggleBtn.isDisplayed(), "Sign-in toggle button is not visible");

        Thread.sleep(1000); // Pause to inspect element visibility

        // Step 4: Validate vertical alignment and ordering
        Point nameLoc = nameInput.getLocation();
        Point emailLoc = emailInput.getLocation();
        Point pwLoc = pwInput.getLocation();
        Point confirmPwLoc = confirmPwInput.getLocation();
        Point submitLoc = submitBtn.getLocation();

        Assert.assertTrue(emailLoc.getY() > nameLoc.getY(), "Email input is not positioned below Username input");
        Assert.assertTrue(pwLoc.getY() > emailLoc.getY(), "Password input is not positioned below Email input");
        Assert.assertTrue(confirmPwLoc.getY() > pwLoc.getY(), "Confirm Password input is not positioned below Password input");
        Assert.assertTrue(submitLoc.getY() > confirmPwLoc.getY(), "Submit button is not positioned below Confirm Password input");

        // Step 5: Highlight each input sequentially to observe field boundary alignment
        WebElement[] formFields = {nameInput, emailInput, pwInput, confirmPwInput, submitBtn};
        for (WebElement field : formFields) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='2px solid red'", field);
            Thread.sleep(600); // Visual pause for inspecting alignment borders
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border=''", field);
        }
    }

    @Test(priority = 2, description = "SU-UI-002: Verify password eye icons")
    public void testPasswordEyeIcons() throws InterruptedException {
        // Step 1: Open Signup form
        openSignupForm();

        // Step 2: Locate Password and Confirm Password inputs
        WebElement pwInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pw")));
        WebElement confirmPwInput = driver.findElement(By.id("pw_c"));

        // Step 3: Locate eye icons for BOTH Password and Confirm Password fields
        WebElement pwEyeIcon = driver.findElement(
                By.xpath("//input[@id='pw']/following-sibling::span | //input[@id='pw']/parent::div//span"));
        WebElement confirmPwEyeIcon = driver.findElement(
                By.xpath("//input[@id='pw_c']/following-sibling::span | //input[@id='pw_c']/parent::div//span"));

        // Assert both eye icons are visible on the form
        Assert.assertTrue(pwEyeIcon.isDisplayed(), "Password eye icon is not visible");
        Assert.assertTrue(confirmPwEyeIcon.isDisplayed(), "Confirm Password eye icon is not visible");
        Thread.sleep(1000); // Pause to observe eye icon display

        // Step 4: Enter text into both password fields
        pwInput.sendKeys("TestPassword123!");
        Thread.sleep(500);
        confirmPwInput.sendKeys("TestPassword123!");
        Thread.sleep(1000); // Pause to view masked password input

        // Step 5: Test Password (pw) Eye Icon Toggle
        clickElement(pwEyeIcon);
        Thread.sleep(1200); // Pause to observe unmasked Password field
        Assert.assertEquals(pwInput.getAttribute("type"), "text", "Password text was not unmasked after clicking eye icon");

        clickElement(pwEyeIcon);
        Thread.sleep(1200); // Pause to observe re-masked Password field
        Assert.assertEquals(pwInput.getAttribute("type"), "password", "Password was not re-masked after toggling eye icon");

        // Step 6: Test Confirm Password (pw_c) Eye Icon Toggle
        clickElement(confirmPwEyeIcon);
        Thread.sleep(1200); // Pause to observe unmasked Confirm Password field
        Assert.assertEquals(confirmPwInput.getAttribute("type"), "text", "Confirm Password text was not unmasked after clicking eye icon");

        clickElement(confirmPwEyeIcon);
        Thread.sleep(1200); // Pause to observe re-masked Confirm Password field
        Assert.assertEquals(confirmPwInput.getAttribute("type"), "password", "Confirm Password was not re-masked after toggling eye icon");
    }

    @Test(priority = 3, description = "SU-UI-003: Verify validation message appearance")
    public void testValidationMessageAppearance() throws InterruptedException {
        // Step 1: Open Signup form
        openSignupForm();

        // Step 2: Leave required fields empty and click Signup button
        WebElement submitBtn = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button"));
        clickElement(submitBtn);
        Thread.sleep(1500); // Pause to observe validation response

        // Step 3: Verify browser alert or HTML5 validation message appearance
        boolean messageFound = false;

        try {
            // Check for Alert dialog
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            Assert.assertFalse(alertText.trim().isEmpty(), "Validation alert text is empty");
            Thread.sleep(1500); // Pause to visually read alert box
            alert.accept();
            messageFound = true;
        } catch (Exception e) {
            // Fallback: Check HTML5 browser field validation attributes
            WebElement nameInput = driver.findElement(By.id("name"));
            String validationMsg = nameInput.getAttribute("validationMessage");
            if (validationMsg != null && !validationMsg.isEmpty()) {
                messageFound = true;
                System.out.println("HTML5 Field Validation Message: " + validationMsg);
                Thread.sleep(1500); // Pause to observe field focus/tooltip
            }
        }

        Assert.assertTrue(messageFound, "No readable validation message or alert appeared when submitting empty fields");
        Thread.sleep(1000); // Final visual pause
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
        System.out.println("         SIGNUP UI TEST SUITE SUMMARY REPORT       ");
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