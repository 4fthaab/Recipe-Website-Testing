package home;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class HomepageNegativeTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "https://recipe-finder-two-murex.vercel.app/";

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
    // SM-NG-01: INVALID URL TESTING
    // ==========================================

    @Test(priority = 1, description = "SM-NG-01: Verify behavior for an invalid social media URL")
    public void testInvalidSocialMediaUrl() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on the page.");

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = socialIcons.get(i);
            String iconId = getIconIdentifier(icon, i);
            scrollToElement(icon);

            WebElement anchor = getAnchorElement(icon);
            String href = (anchor != null) ? anchor.getAttribute("href") : null;

            if (href != null && !href.isEmpty()) {
                boolean isValidProtocol = href.startsWith("http://") || href.startsWith("https://");
                softAssert.assertTrue(isValidProtocol, "FAIL [" + iconId + "]: Invalid scheme in URL: " + href);

                int statusCode = getHttpResponseCode(href);
                boolean isBrokenLink = (statusCode >= 400 || statusCode == -1);

                if (isBrokenLink) {
                    String currentUrlBefore = driver.getCurrentUrl();
                    try {
                        clickElementSafely(anchor != null ? anchor : icon);
                        Thread.sleep(1000);

                        boolean pageAlive = driver.findElements(By.tagName("body")).size() > 0;
                        softAssert.assertTrue(pageAlive, "FAIL [" + iconId + "]: App crashed upon clicking broken link.");
                    } catch (Exception e) {
                        softAssert.fail("FAIL [" + iconId + "]: Exception thrown clicking link: " + e.getMessage());
                    } finally {
                        if (!driver.getCurrentUrl().equals(currentUrlBefore)) {
                            driver.navigate().back();
                            Thread.sleep(1000);
                        }
                    }
                }
            }
        }
        softAssert.assertAll();
    }

    // ==========================================
    // SM-NG-02: MISSING LINK TESTING
    // ==========================================

    @Test(priority = 2, description = "SM-NG-02: Verify behavior when a social media link is missing")
    public void testMissingSocialMediaLinks() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        String[] requiredPlatforms = {"facebook", "twitter", "instagram", "linkedin", "youtube"};

        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on page.");

        for (String platform : requiredPlatforms) {
            WebElement platformIcon = findIconByPlatform(socialIcons, platform);

            if (platformIcon != null) {
                scrollToElement(platformIcon);
                WebElement anchor = getAnchorElement(platformIcon);
                String href = (anchor != null) ? anchor.getAttribute("href") : null;

                boolean isLinkMissing = (href == null || href.trim().isEmpty() || href.trim().equals("#") || href.contains("javascript:void(0)"));

                if (isLinkMissing) {
                    String urlBeforeClick = driver.getCurrentUrl();

                    clickElementSafely(platformIcon);
                    Thread.sleep(1000);

                    String urlAfterClick = driver.getCurrentUrl();
                    boolean pageStable = urlBeforeClick.equalsIgnoreCase(urlAfterClick) || urlAfterClick.contains("localhost");

                    softAssert.assertTrue(pageStable, "FAIL [" + platform + "]: Icon with missing link caused unintended navigation to: " + urlAfterClick);

                    boolean bodyVisible = driver.findElement(By.tagName("body")).isDisplayed();
                    softAssert.assertTrue(bodyVisible, "FAIL [" + platform + "]: App crashed after clicking icon with missing link.");
                }
            }
        }
        softAssert.assertAll();
    }

    // ==========================================
    // HELPER & VISUAL HIGHLIGHT UTILITIES
    // ==========================================

    private String getIconIdentifier(WebElement element, int index) {
        String id = element.getAttribute("id");
        if (id != null && !id.trim().isEmpty()) {
            return "#" + id;
        }
        String className = element.getAttribute("class");
        return "Icon Position " + (index + 1) + " (" + className + ")";
    }

    private void scrollToElement(WebElement element) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        Thread.sleep(300);
        highlightElement(element);
    }

    private void highlightElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
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

    private WebElement getAnchorElement(WebElement icon) {
        try {
            return icon.findElement(By.xpath("ancestor-or-self::a"));
        } catch (Exception e) {
            return null;
        }
    }

    private WebElement findIconByPlatform(List<WebElement> icons, String platformName) {
        for (WebElement icon : icons) {
            String combined = (icon.getAttribute("id") + " " + icon.getAttribute("class") + " " + icon.getAttribute("innerHTML")).toLowerCase();
            if (combined.contains(platformName.toLowerCase()) || (platformName.equalsIgnoreCase("twitter") && combined.contains("x"))) {
                return icon;
            }
        }
        return null;
    }

    private int getHttpResponseCode(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            return connection.getResponseCode();
        } catch (Exception e) {
            return -1;
        }
    }

    private void clickElementSafely(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // ==========================================
    // EXECUTION LOGGING & SUMMARY REPORT
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
        System.out.println("            SOCIAL MEDIA NEGATIVE SUITE EXECUTION REPORT                 ");
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