package search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(3)
@DisplayName("UI Testing")
public class UITest extends SearchTestBase {

@Test
@Order(1)
@DisplayName("SE-UI-001 - Verify Search Input Appearance")
void verifySearchInputAppearance() {

    WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-input")
            )
    );

    assertTrue(
            searchInput.isDisplayed(),
            "Search input should be visible"
    );

    assertTrue(
            searchInput.getSize().getWidth() > 0,
            "Search input should have a valid width"
    );

    assertTrue(
            searchInput.getSize().getHeight() > 0,
            "Search input should have a valid height"
    );

    assertTrue(
            searchInput.getLocation().getX() >= 0,
            "Search input should be positioned within the page"
    );

    assertTrue(
            searchInput.getLocation().getY() >= 0,
            "Search input should be positioned within the page"
    );

    String placeholder = searchInput.getAttribute("placeholder");

    assertNotNull(
            placeholder,
            "Search input should have placeholder text"
    );

    assertFalse(
            placeholder.isBlank(),
            "Search input placeholder should not be empty"
    );

    String display = searchInput.getCssValue("display");

    assertNotEquals(
            "none",
            display,
            "Search input should not be hidden"
    );

    String visibility = searchInput.getCssValue("visibility");

    assertNotEquals(
            "hidden",
            visibility,
            "Search input should be visible"
    );

    searchInput.clear();
    searchInput.sendKeys("Test Recipe");

    assertEquals(
            "Test Recipe",
            searchInput.getAttribute("value"),
            "Search input should accept text"
    );

    driver.manage().window().setSize(new Dimension(1920, 1080));

    assertTrue(
            searchInput.isDisplayed(),
            "Search input should be usable on desktop screen size"
    );

    driver.manage().window().setSize(new Dimension(1024, 768));

    assertTrue(
            searchInput.isDisplayed(),
            "Search input should be usable on tablet screen size"
    );

    driver.manage().window().setSize(new Dimension(390, 844));

    assertTrue(
            searchInput.isDisplayed(),
            "Search input should be usable on mobile screen size"
    );
}

@Test
@Order(2)
@DisplayName("SE-UI-002 - Verify Search Button/Icon Appearance")
void verifySearchButtonAppearance() {

    WebElement searchButton = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-btn")
            )
    );

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be visible"
    );

    assertTrue(
            searchButton.isEnabled(),
            "Search button should be enabled"
    );

    assertTrue(
            searchButton.getSize().getWidth() > 0,
            "Search button should have a valid width"
    );

    assertTrue(
            searchButton.getSize().getHeight() > 0,
            "Search button should have a valid height"
    );

    assertTrue(
            searchButton.getLocation().getX() >= 0,
            "Search button should be positioned within the page"
    );

    assertTrue(
            searchButton.getLocation().getY() >= 0,
            "Search button should be positioned within the page"
    );

    String display = searchButton.getCssValue("display");

    assertNotEquals(
            "none",
            display,
            "Search button should not be hidden"
    );

    String visibility = searchButton.getCssValue("visibility");

    assertNotEquals(
            "hidden",
            visibility,
            "Search button should be visible"
    );

    String cursor = searchButton.getCssValue("cursor");

    assertEquals(
            "pointer",
            cursor,
            "Search button should have a clickable cursor"
    );

    driver.manage().window().setSize(new Dimension(1920, 1080));

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be displayed on desktop screen size"
    );

    driver.manage().window().setSize(new Dimension(1024, 768));

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be displayed on tablet screen size"
    );

    driver.manage().window().setSize(new Dimension(390, 844));

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be displayed on mobile screen size"
    );
}

@Test
@Order(3)
@DisplayName("SE-UI-003 - Verify Page Layout")
void verifyPageLayout() {

    WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-input")
            )
    );

    WebElement searchButton = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-btn")
            )
    );

    assertTrue(
            searchInput.isDisplayed(),
            "Search input should be displayed"
    );

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be displayed"
    );

    String pageText = driver.findElement(By.tagName("body"))
            .getText();

    assertTrue(
            pageText.contains("Get Your"),
            "Page heading should be displayed"
    );

    assertTrue(
            pageText.contains("Instant Wished Recipe"),
            "Main heading text should be displayed"
    );

    assertTrue(
            pageText.contains("In Seconds"),
            "Supporting heading text should be displayed"
    );

    assertTrue(
            pageText.contains("Real food doesn't have ingredients"),
            "Supporting text should be displayed"
    );

    assertTrue(
            searchInput.getLocation().getY() >= 0,
            "Search input should be positioned within the page"
    );

    assertTrue(
            searchButton.getLocation().getY() >= 0,
            "Search button should be positioned within the page"
    );

    assertTrue(
            searchButton.getLocation().getX() > searchInput.getLocation().getX(),
            "Search button should be positioned beside the search input"
    );

    assertTrue(
            searchInput.getSize().getWidth() > 200,
            "Search input should have sufficient width"
    );

    assertTrue(
            searchInput.getSize().getHeight() > 30,
            "Search input should have sufficient height"
    );

    driver.manage().window().setSize(new Dimension(1920, 1080));

    assertTrue(
            searchInput.isDisplayed(),
            "Search layout should be displayed on desktop"
    );

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be displayed on desktop"
    );

    driver.manage().window().setSize(new Dimension(1024, 768));

    assertTrue(
            searchInput.isDisplayed(),
            "Search layout should be displayed on tablet"
    );

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be displayed on tablet"
    );

    driver.manage().window().setSize(new Dimension(390, 844));

    assertTrue(
            searchInput.isDisplayed(),
            "Search layout should be displayed on mobile"
    );

    assertTrue(
            searchButton.isDisplayed(),
            "Search button should be displayed on mobile"
    );
}
}
