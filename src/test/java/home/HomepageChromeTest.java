package home;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

public class HomepageChromeTest {

    private static final String APP_URL = "http://localhost:3000/";
    private static final By SOCIAL_ICON_LOCATOR = By.xpath("//div[contains(@class,'smb')]");

    private WebDriver driver;
    private static final Map<String, String> testResults = new LinkedHashMap<>();

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test(priority = 1, description = "SM-CT-01: Verify social media icons in Chrome")
    public void testSocialMediaIconsChrome() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        SoftAssert softAssert = new SoftAssert();

        driver.get(APP_URL);
        Thread.sleep(1000);

        registerAndLogin(driver, wait);

        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "[Chrome] No social media icons found on the page.");

        String originalWindow = driver.getWindowHandle();

        for (int i = 0; i < socialIcons.size(); i++) {
            List<WebElement> currentIcons = driver.findElements(SOCIAL_ICON_LOCATOR);
            WebElement icon = currentIcons.get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToAndHighlight(driver, icon);

            // Check 1: Display verification
            boolean isDisplayed = icon.isDisplayed();
            softAssert.assertTrue(isDisplayed, "FAIL [Chrome - " + iconId + "]: Icon is not displayed on UI.");

            // Check 2: Functional verification
            Set<String> oldWindows = driver.getWindowHandles();
            String initialUrl = driver.getCurrentUrl();

            String href = icon.getAttribute("href");
            String onClick = icon.getAttribute("onclick");

            if (href == null && !icon.findElements(By.tagName("a")).isEmpty()) {
                href = icon.findElement(By.tagName("a")).getAttribute("href");
            }

            clickElementWithFallback(driver, icon);
            Thread.sleep(1500);

            Set<String> newWindows = driver.getWindowHandles();
            boolean actionOccurred = false;

            if (newWindows.size() > oldWindows.size()) {
                actionOccurred = true;
                for (String handle : newWindows) {
                    if (!oldWindows.contains(handle)) {
                        driver.switchTo().window(handle);
                        driver.close();
                        break;
                    }
                }
                driver.switchTo().window(originalWindow);

            } else if (!driver.getCurrentUrl().equals(initialUrl)) {
                actionOccurred = true;
                driver.navigate().back();
                Thread.sleep(1000);

            } else if (href != null || onClick != null || isMailIcon(iconId, icon)) {
                actionOccurred = true;
            }

            softAssert.assertTrue(actionOccurred, "FAIL [Chrome - " + iconId + "]: Icon click produced no navigation, action, or valid href attribute.");
        }

        softAssert.assertAll();
    }

    private void registerAndLogin(WebDriver driver, WebDriverWait wait) throws InterruptedException {
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

    private void scrollToAndHighlight(WebDriver driver, WebElement element) throws InterruptedException {
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

    private void clickElementWithFallback(WebDriver driver, WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String testKey = "SM-CT-01 [Chrome] - Verify social media icons";
        String statusStr = switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN";
        };

        testResults.put(testKey, statusStr);

        System.out.println("==================================================");
        System.out.println("EXECUTED: " + testKey);
        System.out.println("STATUS  : [" + statusStr + "]");
        if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null) {
            System.out.println("REASON  :\n" + result.getThrowable().getMessage());
        }
        System.out.println("==================================================\n");

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public void printFinalSummaryReport() {
        System.out.println("\n#########################################################################");
        System.out.println("                  CHROME EXECUTION REPORT                               ");
        System.out.println("#########################################################################");
        System.out.printf("%-12s | %-45s | %-8s%n", "TEST ID", "TEST DESCRIPTION", "STATUS");
        System.out.println("-------------------------------------------------------------------------");

        for (Map.Entry<String, String> entry : testResults.entrySet()) {
            System.out.printf("%-12s | %-45s | %-8s%n", "SM-CT-01", entry.getKey(), entry.getValue());
        }
        System.out.println("#########################################################################\n");
    }
}