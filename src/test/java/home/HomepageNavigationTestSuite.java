package home;

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
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomepageNavigationTestSuite {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:3000/";

    // Locates all social media icon containers matching class 'smb'
    private static final By SOCIAL_ICON_LOCATOR = By.xpath("//div[contains(@class,'smb')]");

    // Tracks individual test execution results for final suite report
    private static final Map<String, String> testResults = new LinkedHashMap<>();

    @BeforeMethod
    public void setUp() throws InterruptedException {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(APP_URL);
        Thread.sleep(1000);

        registerAndLogin();
    }

    private void registerAndLogin() throws InterruptedException {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        String dynamicUser = "user" + timestamp;
        String dynamicEmail = "user" + timestamp + "@gmail.com";
        String password = "TestPassword123!";

        WebElement signupToggleBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-btn")));
        signupToggleBtn.click();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).sendKeys(dynamicUser);
        driver.findElement(By.id("email")).sendKeys(dynamicEmail);
        driver.findElement(By.id("pw")).sendKeys(password);
        driver.findElement(By.id("pw_c")).sendKeys(password);
        Thread.sleep(1000);

        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div/form/button")).click();

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Thread.sleep(1000);
            alert.accept();
        } catch (Exception ignored) {}

        WebElement signinToggle = wait.until(ExpectedConditions.elementToBeClickable(By.id("signin-btn")));
        try {
            signinToggle.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signinToggle);
        }
        Thread.sleep(1000);

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));
        usernameInput.sendKeys(dynamicUser);
        driver.findElement(By.id("userPw")).sendKeys(password);
        Thread.sleep(1000);

        driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/form/button")).click();

        wait.until(d -> !d.getCurrentUrl().equals(APP_URL) || d.findElements(SOCIAL_ICON_LOCATOR).size() > 0);
        Thread.sleep(2000);
    }

    @Test(priority = 1, description = "SM-NT-01: Verify social media URL or trigger configuration for all icons")
    public void testSocialMediaUrlIsCorrect() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on the page.");

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            WebElement parentLink = null;
            try {
                parentLink = icon.findElement(By.xpath("ancestor-or-self::a"));
            } catch (Exception ignored) {}

            String href = (parentLink != null) ? parentLink.getAttribute("href") : icon.getAttribute("href");
            String onClickAttr = icon.getAttribute("onclick");
            boolean isMail = isMailIcon(iconId, icon);

            if (href == null && onClickAttr == null && !isMail) {
                softAssert.fail("FAIL [" + iconId + "]: Missing 'href' link attribute, active click listener, or mail handler.");
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "SM-NT-02: Verify social media links are not broken for all icons")
    public void testSocialMediaLinkNotBroken() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));

        String originalWindow = driver.getWindowHandle();

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            Set<String> oldWindows = driver.getWindowHandles();
            String initialUrl = driver.getCurrentUrl();

            clickElementWithFallback(icon);
            Thread.sleep(2000);

            Set<String> newWindows = driver.getWindowHandles();
            boolean isMail = isMailIcon(iconId, icon);

            if (newWindows.size() > oldWindows.size()) {
                // Opened in a new browser tab/window
                for (String windowHandle : newWindows) {
                    if (!oldWindows.contains(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        driver.close();
                        break;
                    }
                }
                driver.switchTo().window(originalWindow);

            } else if (!driver.getCurrentUrl().equals(initialUrl)) {
                // Redirected within same browser tab
                driver.navigate().back();
                Thread.sleep(1500);

            } else if (!isMail) {
                // Non-responsive / broken element
                softAssert.fail("FAIL [" + iconId + "]: Icon click produced no tab opening, page navigation, or mail app launch.");
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 3, description = "SM-NT-03: Verify navigation back to application for all icons")
    public void testNavigationBackToApplication() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));

        String originalWindow = driver.getWindowHandle();

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            Set<String> oldWindows = driver.getWindowHandles();
            String initialUrl = driver.getCurrentUrl();

            clickElementWithFallback(icon);
            Thread.sleep(2000);

            Set<String> newWindows = driver.getWindowHandles();
            boolean isMail = isMailIcon(iconId, icon);

            if (newWindows.size() > oldWindows.size()) {
                // New tab opened -> close tab to return to main application
                for (String windowHandle : newWindows) {
                    if (!oldWindows.contains(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        driver.close();
                        break;
                    }
                }
                driver.switchTo().window(originalWindow);
                softAssert.assertEquals(driver.getCurrentUrl(), initialUrl, "FAIL [" + iconId + "]: Failed to return to main window handle.");

            } else if (!driver.getCurrentUrl().equals(initialUrl)) {
                // Same tab navigation -> trigger browser back navigation
                driver.navigate().back();
                Thread.sleep(1500);
                softAssert.assertEquals(driver.getCurrentUrl(), initialUrl, "FAIL [" + iconId + "]: Failed to navigate back to original URL.");

            } else if (isMail) {
                // Mail icon launches desktop application (Outlook) while staying on the web page
                softAssert.assertEquals(driver.getCurrentUrl(), initialUrl, "FAIL [" + iconId + "]: Web application state disrupted during mail trigger.");

            } else {
                // Non-functional icon
                softAssert.fail("FAIL [" + iconId + "]: Cannot test back navigation because the icon failed to trigger any action.");
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 4, description = "SM-NT-04: Verify link structure and interactive wrapper for all icons")
    public void testLinkOpeningBehavior() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            WebElement parentAnchor = null;
            try {
                parentAnchor = icon.findElement(By.xpath("ancestor-or-self::a"));
            } catch (Exception ignored) {}

            boolean isMail = isMailIcon(iconId, icon);

            if (parentAnchor == null && !isMail) {
                softAssert.fail("FAIL [" + iconId + "]: Icon is implemented as a static <div> without an <a> anchor tag wrapper or mail action.");
            }
        }
        softAssert.assertAll();
    }

    private boolean isMailIcon(String iconId, WebElement icon) {
        String lowerId = iconId.toLowerCase();
        String className = icon.getAttribute("class").toLowerCase();
        return lowerId.contains("gmail") || lowerId.contains("mail") || className.contains("gmail") || className.contains("mail");
    }

    private String getIconIdentifier(WebElement element, int index) {
        String id = element.getAttribute("id");
        if (id != null && !id.trim().isEmpty()) {
            return "#" + id;
        }
        String className = element.getAttribute("class");
        return "Icon Position " + (index + 1) + " (" + className + ")";
    }

    /**
     * Smoothly scrolls to the element and highlights it temporarily.
     */
    private void scrollToAndHighlight(WebElement element) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        Thread.sleep(300);

        try {
            js.executeScript(
                    "arguments[0].style.outline = '3px solid #ff0055';" +
                            "arguments[0].style.backgroundColor = 'rgba(255, 255, 0, 0.45)';" +
                            "arguments[0].style.transition = 'all 0.2s ease-in-out';",
                    element
            );
            Thread.sleep(400);
            js.executeScript(
                    "arguments[0].style.outline = '';" +
                            "arguments[0].style.backgroundColor = '';",
                    element
            );
        } catch (Exception ignored) {}
    }

    private void clickElementWithFallback(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws InterruptedException {
        String testIdAndDesc = result.getMethod().getDescription();
        if (testIdAndDesc == null || testIdAndDesc.isEmpty()) {
            testIdAndDesc = result.getMethod().getMethodName();
        }

        String statusStr = switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN";
        };

        testResults.put(testIdAndDesc, statusStr);

        System.out.println("==================================================");
        System.out.println("EXECUTED: " + testIdAndDesc);
        System.out.println("STATUS  : [" + statusStr + "]");
        if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null) {
            System.out.println("REASON  :\n" + result.getThrowable().getMessage());
        }
        System.out.println("==================================================\n");

        Thread.sleep(1000);

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n#########################################################################");
        System.out.println("                SOCIAL MEDIA SUITE EXECUTION SUMMARY REPORT               ");
        System.out.println("#########################################################################");
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
        System.out.println("#########################################################################\n");
    }
}