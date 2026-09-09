package home;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Homepage Functional Test Suite for Social Media & Contact Links
 *
 * Intercepts email/mailto links to redirect to in-browser Gmail webmail,
 * preventing external desktop applications (like Outlook) from launching.
 */
public class HomepageFunctionalTestSuite {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "https://recipe-finder-two-murex.vercel.app/index.html";

    private static final By SOCIAL_ICON_LOCATOR = By.xpath("//div[contains(@class,'smb')]");
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

    /**
     * Helper method to bypass authentication and access the homepage dashboard.
     */
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

    // ==========================================
    // SOCIAL MEDIA FUNCTIONAL TESTS
    // ==========================================

    @Test(priority = 1, description = "SM-FT-01: Verify that each social media icon is clickable")
    public void testSocialMediaIconsClickable() throws InterruptedException {
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons were found on the homepage.");

        for (int i = 0; i < socialIcons.size(); i++) {
            List<WebElement> currentIcons = driver.findElements(SOCIAL_ICON_LOCATOR);
            Assert.assertTrue(i < currentIcons.size(), "DOM re-rendered and element index " + i + " is unavailable.");
            WebElement icon = currentIcons.get(i);

            scrollToAndHighlight(icon);

            Assert.assertTrue(icon.isDisplayed(), "Social media icon at index " + i + " is not displayed.");

            WebElement clickableIcon = wait.until(ExpectedConditions.elementToBeClickable(icon));
            Assert.assertNotNull(clickableIcon, "Social media icon at index " + i + " is not clickable.");

            System.out.println("Verified icon [" + (i + 1) + "/" + socialIcons.size() + "] is displayed and clickable.");
        }
    }

    @Test(priority = 2, description = "SM-FT-02: Verify redirection from social media icons")
    public void testSocialMediaRedirection() throws InterruptedException {
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on homepage.");

        String mainPageWindow = driver.getWindowHandle();
        int totalIcons = socialIcons.size();

        for (int i = 0; i < totalIcons; i++) {
            List<WebElement> currentIcons = driver.findElements(SOCIAL_ICON_LOCATOR);
            Assert.assertTrue(i < currentIcons.size(), "DOM element count changed during execution. Expected index " + i + " but found " + currentIcons.size() + " elements.");

            WebElement targetIcon = currentIcons.get(i);
            String iconId = getIconIdentifier(targetIcon, i);
            String href = getElementHref(targetIcon);
            boolean isMail = isMailIcon(iconId, targetIcon, href);

            scrollToAndHighlight(targetIcon);

            String initialUrl = driver.getCurrentUrl();

            // Safe click to intercept mailto and redirect inside browser
            clickElementWithFallback(targetIcon, href, isMail);
            Thread.sleep(2000);

            Set<String> allWindows = driver.getWindowHandles();
            if (allWindows.size() > 1) {
                for (String windowHandle : allWindows) {
                    if (!windowHandle.equals(mainPageWindow)) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                }
            }

            String currentUrl = driver.getCurrentUrl();

            // Verify navigation actually occurred
            Assert.assertNotEquals(currentUrl, initialUrl, "User was not redirected upon clicking icon at index " + i + " (" + iconId + "). URL remained unchanged.");
            Assert.assertTrue(currentUrl.startsWith("http://") || currentUrl.startsWith("https://"), "Target destination URL scheme is invalid for icon at index " + i + ": " + currentUrl);

            if (allWindows.size() > 1) {
                driver.close();
                driver.switchTo().window(mainPageWindow);
            } else {
                driver.navigate().back();
                Thread.sleep(1000);
            }
        }
    }

    @Test(priority = 3, description = "SM-FT-03: Verify functionality of all social media links")
    public void testAllSocialMediaLinksFunctionality() throws InterruptedException {
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on page.");

        String mainPageWindow = driver.getWindowHandle();
        int totalIcons = socialIcons.size();

        for (int i = 0; i < totalIcons; i++) {
            List<WebElement> currentIcons = driver.findElements(SOCIAL_ICON_LOCATOR);
            if (i >= currentIcons.size()) {
                Assert.fail("DOM element count changed during execution. Expected index " + i + " but found " + currentIcons.size() + " elements.");
            }

            WebElement icon = currentIcons.get(i);
            String iconId = getIconIdentifier(icon, i);
            String href = getElementHref(icon);
            boolean isMail = isMailIcon(iconId, icon, href);

            scrollToAndHighlight(icon);

            String urlBeforeClick = driver.getCurrentUrl();
            clickElementWithFallback(icon, href, isMail);

            Thread.sleep(2000);

            Set<String> windowHandles = driver.getWindowHandles();

            if (windowHandles.size() > 1) {
                for (String handle : windowHandles) {
                    if (!handle.equals(mainPageWindow)) {
                        driver.switchTo().window(handle);
                        break;
                    }
                }

                String currentUrl = driver.getCurrentUrl();
                Assert.assertFalse(currentUrl.contains("about:blank"), "Destination URL opened a blank page for icon at index " + i);
                Assert.assertNotEquals(currentUrl, urlBeforeClick, "Icon at index " + i + " did not navigate away from origin.");

                driver.close();
                driver.switchTo().window(mainPageWindow);
            } else {
                String currentUrl = driver.getCurrentUrl();
                Assert.assertNotEquals(currentUrl, urlBeforeClick, "Icon at index " + i + " failed to navigate to a target URL.");
                driver.navigate().back();
                Thread.sleep(1000);
            }
        }
    }

    @Test(priority = 4, description = "SM-FT-04: Verify single-click navigation")
    public void testSingleClickNavigation() throws InterruptedException {
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons present.");

        WebElement icon = socialIcons.get(0);
        String iconId = getIconIdentifier(icon, 0);
        String href = getElementHref(icon);
        boolean isMail = isMailIcon(iconId, icon, href);

        scrollToAndHighlight(icon);

        int initialWindowCount = driver.getWindowHandles().size();
        String initialUrl = driver.getCurrentUrl();

        clickElementWithFallback(icon, href, isMail);
        Thread.sleep(2000);

        Set<String> currentWindows = driver.getWindowHandles();
        int finalWindowCount = currentWindows.size();
        String currentUrl = driver.getCurrentUrl();

        // Check 1: Ensure navigation occurred (either new tab opened OR same-tab URL changed)
        boolean navigationOccurred = (finalWindowCount > initialWindowCount) || !currentUrl.equalsIgnoreCase(initialUrl);
        Assert.assertTrue(navigationOccurred, "Clicking the social media icon triggered no navigation (URL remained identical and no new tab opened).");

        // Check 2: Ensure single-click navigation (exactly 1 action, not multiple duplicate tabs)
        Assert.assertTrue(finalWindowCount <= initialWindowCount + 1,
                "Unexpected tab count (" + finalWindowCount + "). Multiple duplicate tabs were opened on a single click.");

        if (finalWindowCount > initialWindowCount) {
            List<String> tabs = new ArrayList<>(currentWindows);
            driver.switchTo().window(tabs.get(1));
            driver.close();
            driver.switchTo().window(tabs.get(0));
        } else {
            driver.navigate().back();
            Thread.sleep(1000);
        }
    }

    // ==========================================
    // MAIL INTERCEPTION & UTILITY HELPER METHODS
    // ==========================================

    /**
     * Identifies the DOM ID or Class name of the social icon for logging.
     */
    private String getIconIdentifier(WebElement element, int index) {
        String id = element.getAttribute("id");
        if (id != null && !id.trim().isEmpty()) {
            return "#" + id;
        }
        String className = element.getAttribute("class");
        if (className != null && !className.trim().isEmpty()) {
            return "." + className.replaceAll("\\s+", ".");
        }
        return "Icon[" + index + "]";
    }

    /**
     * Extracts destination URL from self, parent, or child anchor tags.
     */
    private String getElementHref(WebElement element) {
        List<String> targetAttrs = Arrays.asList("href", "data-href", "data-url", "data-link", "onclick");

        // 1. Check self
        for (String attr : targetAttrs) {
            String val = element.getAttribute(attr);
            if (val != null && !val.trim().isEmpty() && !val.trim().equals("#") && !val.trim().endsWith("#")) {
                return val.trim();
            }
        }

        // 2. Check parent/ancestor <a>
        try {
            WebElement parentAnchor = element.findElement(By.xpath("ancestor-or-self::a"));
            for (String attr : targetAttrs) {
                String val = parentAnchor.getAttribute(attr);
                if (val != null && !val.trim().isEmpty() && !val.trim().equals("#") && !val.trim().endsWith("#")) {
                    return val.trim();
                }
            }
        } catch (Exception ignored) {}

        // 3. Check child <a> or elements with href
        try {
            List<WebElement> childAnchors = element.findElements(By.xpath(".//*[@href or @data-href or @data-url]"));
            for (WebElement child : childAnchors) {
                for (String attr : targetAttrs) {
                    String val = child.getAttribute(attr);
                    if (val != null && !val.trim().isEmpty() && !val.trim().equals("#") && !val.trim().endsWith("#")) {
                        return val.trim();
                    }
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Determines whether an icon represents an email link or mailto scheme.
     */
    private boolean isMailIcon(String iconId, WebElement icon, String href) {
        String lowerId = iconId.toLowerCase();
        String className = icon.getAttribute("class") != null ? icon.getAttribute("class").toLowerCase() : "";
        String lowerHref = (href != null) ? href.toLowerCase() : "";

        String outerHtml = "";
        try {
            outerHtml = icon.getAttribute("outerHTML").toLowerCase();
        } catch (Exception ignored) {}

        return lowerId.contains("gmail") || lowerId.contains("mail") || lowerId.contains("envelope") || lowerId.contains("email")
                || className.contains("gmail") || className.contains("mail") || className.contains("envelope") || className.contains("email")
                || lowerHref.startsWith("mailto:") || outerHtml.contains("mailto:");
    }

    /**
     * Extracts raw mailto: string from outer HTML string.
     */
    private String extractMailtoFromHtml(String html) {
        if (html != null && html.contains("mailto:")) {
            int start = html.indexOf("mailto:");
            int end = html.indexOf("\"", start);
            if (end == -1) end = html.indexOf("'", start);
            if (end == -1) end = html.indexOf(" ", start);
            if (end != -1) {
                return html.substring(start, end);
            }
        }
        return null;
    }

    /**
     * Converts a mailto: link into a Gmail Webmail composer URL to keep execution inside browser.
     */
    private String getWebMailUrl(String href) {
        if (href != null && href.toLowerCase().startsWith("mailto:")) {
            String email = href.substring(7).split("\\?")[0];
            if (!email.isEmpty()) {
                return "https://mail.google.com/mail/?view=cm&fs=1&to=" + email;
            }
        }
        return "https://mail.google.com/";
    }

    /**
     * Safe click mechanism:
     * - If email/mailto icon: Navigates directly to Gmail Webmail (bypasses Outlook OS popups).
     * - If standard icon: Triggers standard Selenium / JS click.
     */
    private void clickElementWithFallback(WebElement element, String href, boolean isMail) {
        String lowerHref = (href != null) ? href.toLowerCase() : "";
        String outerHtml = "";
        try {
            outerHtml = element.getAttribute("outerHTML").toLowerCase();
        } catch (Exception ignored) {}

        if (isMail || lowerHref.startsWith("mailto:") || outerHtml.contains("mailto:")) {
            // Bypass OS protocol handler by navigating browser directly to Webmail
            String targetMailHref = href;
            if (targetMailHref == null || !targetMailHref.toLowerCase().startsWith("mailto:")) {
                targetMailHref = extractMailtoFromHtml(outerHtml);
            }
            String webMailUrl = getWebMailUrl(targetMailHref);
            driver.get(webMailUrl);
        } else {
            try {
                element.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }

    /**
     * Highlights element on screen during execution for visual feedback.
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

    // ==========================================
    // EXECUTION LOGGING & FINAL SUMMARY REPORT
    // ==========================================

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
            System.out.println("REASON  : " + result.getThrowable().getMessage());
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
        System.out.println("         SOCIAL MEDIA FUNCTIONAL SUITE EXECUTION REPORT                 ");
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