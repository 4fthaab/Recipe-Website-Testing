package home;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class HomepageUiTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "https://recipe-finder-two-murex.vercel.app/";

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

    // ==========================================
    // SOCIAL MEDIA UI TEST CASES
    // ==========================================

    @Test(priority = 1, description = "SM-UI-01: Verify proper social media icons are displayed")
    public void testProperSocialMediaIconsDisplayed() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on the homepage.");

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = socialIcons.get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToElement(icon);

            WebElement parentLink = null;
            try {
                parentLink = icon.findElement(By.xpath("ancestor-or-self::a"));
            } catch (Exception ignored) {}

            String href = (parentLink != null) ? parentLink.getAttribute("href") : icon.getAttribute("href");
            String idAttr = icon.getAttribute("id");
            String className = icon.getAttribute("class");
            String innerHtml = icon.getAttribute("innerHTML");
            String ariaLabel = icon.getAttribute("aria-label");

            String combinedDetails = (idAttr + " " + href + " " + className + " " + innerHtml + " " + ariaLabel).toLowerCase();

            // Includes telegram now
            boolean isRecognizableBrand = combinedDetails.contains("facebook") ||
                    combinedDetails.contains("twitter") ||
                    combinedDetails.contains("x") ||
                    combinedDetails.contains("instagram") ||
                    combinedDetails.contains("linkedin") ||
                    combinedDetails.contains("youtube") ||
                    combinedDetails.contains("gmail") ||
                    combinedDetails.contains("mail") ||
                    combinedDetails.contains("telegram");

            softAssert.assertTrue(isRecognizableBrand, "FAIL [" + iconId + "]: Icon does not represent a recognized social media brand.");
        }
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "SM-UI-02: Verify visibility of social media icons")
    public void testVisibilityOfSocialMediaIcons() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on the homepage.");

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = socialIcons.get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToElement(icon);

            boolean isVisible = icon.isDisplayed();
            softAssert.assertTrue(isVisible, "FAIL [" + iconId + "]: Social media icon is present in DOM but not visible on page.");
        }
        softAssert.assertAll();
    }

    @Test(priority = 3, description = "SM-UI-03: Verify alignment and size of icons")
    public void testAlignmentAndSizeOfIcons() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertTrue(socialIcons.size() >= 2, "At least 2 social media icons are required to test alignment and overlap.");

        Point previousLocation = null;
        Dimension previousSize = null;

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = socialIcons.get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToElement(icon);

            Point currentLocation = icon.getLocation();
            Dimension currentSize = icon.getSize();

            // Verify icon dimensions are valid
            boolean hasValidDimensions = currentSize.getWidth() > 0 && currentSize.getHeight() > 0;
            softAssert.assertTrue(hasValidDimensions, "FAIL [" + iconId + "]: Icon width/height must be greater than 0.");

            if (previousLocation != null && previousSize != null) {
                int yDiff = Math.abs(currentLocation.getY() - previousLocation.getY());

                if (yDiff < 20) {
                    // Icons on the same row: verify no horizontal overlap
                    boolean noOverlap = currentLocation.getX() >= (previousLocation.getX() + previousSize.getWidth());
                    softAssert.assertTrue(noOverlap, "FAIL [" + iconId + "]: Icon overlaps horizontally with adjacent icon on the same row.");
                } else {
                    // New grid row started: verify the new row is positioned below the previous row
                    boolean isBelowPreviousRow = currentLocation.getY() >= (previousLocation.getY() + previousSize.getHeight());
                    softAssert.assertTrue(isBelowPreviousRow, "FAIL [" + iconId + "]: New grid row overlaps vertically with the row above it.");
                }
            }

            previousLocation = currentLocation;
            previousSize = currentSize;
        }
        softAssert.assertAll();
    }

    @Test(priority = 4, description = "SM-UI-04: Verify hover effect on icons")
    public void testHoverEffectOnIcons() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> socialIcons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SOCIAL_ICON_LOCATOR));
        Assert.assertFalse(socialIcons.isEmpty(), "No social media icons found on the homepage.");

        Actions actions = new Actions(driver);

        for (int i = 0; i < socialIcons.size(); i++) {
            WebElement icon = socialIcons.get(i);
            String iconId = getIconIdentifier(icon, i);

            scrollToElement(icon);

            String initialColor = icon.getCssValue("color");
            String initialTransform = icon.getCssValue("transform");
            String initialOpacity = icon.getCssValue("opacity");

            actions.moveToElement(icon).perform();
            Thread.sleep(800);

            String hoveredColor = icon.getCssValue("color");
            String hoveredTransform = icon.getCssValue("transform");
            String hoveredOpacity = icon.getCssValue("opacity");
            String cursorType = icon.getCssValue("cursor");

            boolean visualChanged = !initialColor.equals(hoveredColor) ||
                    !initialTransform.equals(hoveredTransform) ||
                    !initialOpacity.equals(hoveredOpacity);

            boolean isInteractive = visualChanged || "pointer".equalsIgnoreCase(cursorType);

            softAssert.assertTrue(isInteractive, "FAIL [" + iconId + "]: Hovering produced no visual feedback or pointer cursor.");
        }
        softAssert.assertAll();
    }

    // ==========================================
    // UTILITY HELPER METHODS
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
        Thread.sleep(500);
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
        System.out.println("               SOCIAL MEDIA UI SUITE EXECUTION SUMMARY REPORT            ");
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