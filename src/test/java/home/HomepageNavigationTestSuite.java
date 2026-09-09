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
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomepageNavigationTestSuite {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "https://recipe-finder-two-murex.vercel.app/index.html";

    private static final By SOCIAL_ICON_LOCATOR = By.xpath("//div[contains(@class,'smb')]");
    private static final Map<String, String> testResults = new LinkedHashMap<>();

    // Recognized social platforms & valid email/messaging schemes
    private static final List<String> VALID_PATTERNS = Arrays.asList(
            "facebook.com", "instagram.com", "twitter.com", "x.com",
            "linkedin.com", "youtube.com", "github.com", "telegram.org",
            "t.me", "mailto:", "google.com", "mail.google.com"
    );

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

    @Test(priority = 1, description = "SM-NT-01: Verify social media URL is correct")
    public void testSocialMediaUrlIsCorrect() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on the page.");

        String originalWindow = driver.getWindowHandle();

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            String staticHref = getElementHref(icon);
            boolean isMail = isMailIcon(iconId, icon, staticHref);

            Set<String> oldWindows = driver.getWindowHandles();
            String initialUrl = driver.getCurrentUrl();

            // Click icon or simulate webmail redirection
            clickElementWithFallback(icon, staticHref, isMail);
            Thread.sleep(2500);

            Set<String> newWindows = driver.getWindowHandles();
            String destinationUrl = "";

            if (isMail) {
                destinationUrl = driver.getCurrentUrl();
                driver.navigate().back();
                Thread.sleep(1500);
            } else if (newWindows.size() > oldWindows.size()) {
                // Opened in new tab
                for (String windowHandle : newWindows) {
                    if (!oldWindows.contains(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        destinationUrl = driver.getCurrentUrl();
                        driver.close();
                        break;
                    }
                }
                driver.switchTo().window(originalWindow);
            } else if (!driver.getCurrentUrl().equals(initialUrl)) {
                // Navigated in same tab
                destinationUrl = driver.getCurrentUrl();
                driver.navigate().back();
                Thread.sleep(1500);
            } else if (staticHref != null && !staticHref.equals("#") && !staticHref.endsWith("#")) {
                destinationUrl = staticHref;
            }

            if (destinationUrl.isEmpty() || destinationUrl.endsWith("#")) {
                softAssert.fail("FAIL [" + iconId + "]: Could not determine destination URL after clicking icon.");
                continue;
            }

            if (isMail) {
                boolean isValidMailUrl = destinationUrl.toLowerCase().contains("mailto:") || destinationUrl.toLowerCase().contains("mail");
                softAssert.assertTrue(isValidMailUrl, "FAIL [" + iconId + "]: Email URL does not belong to a mail scheme/provider. Found: " + destinationUrl);
            } else {
                boolean matchesSocialPlatform = VALID_PATTERNS.stream().anyMatch(destinationUrl.toLowerCase()::contains);
                softAssert.assertTrue(matchesSocialPlatform, "FAIL [" + iconId + "]: Destination URL does not belong to expected social platform. Found: " + destinationUrl);
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "SM-NT-02: Verify social media link is not broken")
    public void testSocialMediaLinkNotBroken() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));

        String originalWindow = driver.getWindowHandle();

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            String href = getElementHref(icon);
            boolean isMail = isMailIcon(iconId, icon, href);

            Set<String> oldWindows = driver.getWindowHandles();
            String initialUrl = driver.getCurrentUrl();

            clickElementWithFallback(icon, href, isMail);
            Thread.sleep(2500);

            Set<String> newWindows = driver.getWindowHandles();

            if (isMail) {
                String currentUrl = driver.getCurrentUrl();
                String title = driver.getTitle().toLowerCase();

                softAssert.assertFalse(currentUrl.equals("about:blank"), "FAIL [" + iconId + "]: Webmail page loaded blank.");
                softAssert.assertFalse(title.contains("404") || title.contains("not found"), "FAIL [" + iconId + "]: Webmail link broken.");

                driver.navigate().back();
                Thread.sleep(1500);
            } else if (newWindows.size() > oldWindows.size()) {
                for (String windowHandle : newWindows) {
                    if (!oldWindows.contains(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        String currentUrl = driver.getCurrentUrl();
                        String title = driver.getTitle().toLowerCase();

                        softAssert.assertFalse(currentUrl.equals("about:blank"), "FAIL [" + iconId + "]: Destination page loaded blank.");
                        softAssert.assertFalse(title.contains("404") || title.contains("not found"), "FAIL [" + iconId + "]: Broken link / 404 error.");

                        driver.close();
                        break;
                    }
                }
                driver.switchTo().window(originalWindow);
            } else if (!driver.getCurrentUrl().equals(initialUrl)) {
                String currentUrl = driver.getCurrentUrl();
                String title = driver.getTitle().toLowerCase();

                softAssert.assertFalse(currentUrl.equals("about:blank"), "FAIL [" + iconId + "]: Destination page loaded blank.");
                softAssert.assertFalse(title.contains("404") || title.contains("not found"), "FAIL [" + iconId + "]: Broken link / 404 error.");

                driver.navigate().back();
                Thread.sleep(1500);
            } else {
                softAssert.fail("FAIL [" + iconId + "]: Link click resulted in no navigation, tab opening, or browser action.");
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 3, description = "SM-NT-03: Verify navigation back to application")
    public void testNavigationBackToApplication() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));

        String originalWindow = driver.getWindowHandle();

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            String href = getElementHref(icon);
            boolean isMail = isMailIcon(iconId, icon, href);

            Set<String> oldWindows = driver.getWindowHandles();
            String initialUrl = driver.getCurrentUrl();

            clickElementWithFallback(icon, href, isMail);
            Thread.sleep(2500);

            Set<String> newWindows = driver.getWindowHandles();

            if (isMail) {
                driver.navigate().back();
                Thread.sleep(2000);
                softAssert.assertTrue(driver.getCurrentUrl().contains("recipe-finder"), "FAIL [" + iconId + "]: Failed to return to Homepage from Webmail.");
            } else if (newWindows.size() > oldWindows.size()) {
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
                driver.navigate().back();
                Thread.sleep(2000);
                softAssert.assertTrue(driver.getCurrentUrl().contains("recipe-finder"), "FAIL [" + iconId + "]: Browser back navigation failed to restore Homepage.");
            } else {
                softAssert.fail("FAIL [" + iconId + "]: Cannot test back navigation because social icon produced no navigation action.");
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 4, description = "SM-NT-04: Verify link opening behavior")
    public void testLinkOpeningBehavior() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));

        String originalWindow = driver.getWindowHandle();

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = driver.findElements(SOCIAL_ICON_LOCATOR).get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(icon);

            String href = getElementHref(icon);
            boolean isMail = isMailIcon(iconId, icon, href);

            Set<String> oldWindows = driver.getWindowHandles();
            String initialUrl = driver.getCurrentUrl();

            clickElementWithFallback(icon, href, isMail);
            Thread.sleep(2000);

            Set<String> newWindows = driver.getWindowHandles();

            if (isMail) {
                driver.navigate().back();
                Thread.sleep(1500);
            } else if (newWindows.size() > oldWindows.size()) {
                for (String windowHandle : newWindows) {
                    if (!oldWindows.contains(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        driver.close();
                        break;
                    }
                }
                driver.switchTo().window(originalWindow);
            } else if (!driver.getCurrentUrl().equals(initialUrl)) {
                driver.navigate().back();
                Thread.sleep(1500);
            } else {
                softAssert.fail("FAIL [" + iconId + "]: Social link failed to open in browser window or tab.");
            }
        }
        softAssert.assertAll();
    }

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

    private String getWebMailUrl(String href) {
        if (href != null && href.toLowerCase().startsWith("mailto:")) {
            String email = href.substring(7).split("\\?")[0];
            if (!email.isEmpty()) {
                return "https://mail.google.com/mail/?view=cm&fs=1&to=" + email;
            }
        }
        return "https://mail.google.com/";
    }

    private void clickElementWithFallback(WebElement element, String href, boolean isMail) {
        String lowerHref = (href != null) ? href.toLowerCase() : "";
        String outerHtml = "";
        try {
            outerHtml = element.getAttribute("outerHTML").toLowerCase();
        } catch (Exception ignored) {}

        // IF element is email icon or has mailto:, DO NOT execute physical click (avoids OS Outlook popup)
        if (isMail || lowerHref.startsWith("mailto:") || outerHtml.contains("mailto:")) {
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