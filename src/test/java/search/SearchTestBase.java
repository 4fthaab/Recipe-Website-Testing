package search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class SearchTestBase {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final String SEARCH_PAGE_URL =
            "https://food-recipe-finder-two.vercel.app/Searchpage/Searchpage/api.html";

    @BeforeEach
    void setUp() {
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().maximize();
        driver.get(SEARCH_PAGE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void performSearch(String recipeName) {
        WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("search-input")
                )
        );

        searchBox.clear();
        searchBox.sendKeys(recipeName);

        WebElement searchButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("search-btn")
                )
        );

        searchButton.click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("meal")
                )
        );
    }
}
