package search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(4)
@DisplayName("Usability Testing")
public class UsabilityTest extends SearchTestBase {

@Test
@Order(1)
@DisplayName("SE-U-001 - Verify Search Field Is Easy to Identify")
void verifySearchFieldIsEasyToIdentify() {

    WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-input")
            )
    );

    assertTrue(
            searchInput.isDisplayed(),
            "Search field should be visible"
    );

    assertTrue(
            searchInput.getSize().getWidth() > 200,
            "Search field should be large enough to be easily identified"
    );

    assertTrue(
            searchInput.getSize().getHeight() > 30,
            "Search field should have sufficient height"
    );

    assertTrue(
            searchInput.getLocation().getX() >= 0,
            "Search field should be positioned within the page"
    );

    assertTrue(
            searchInput.getLocation().getY() >= 0,
            "Search field should be positioned within the page"
    );

    String borderRadius = searchInput.getCssValue("border-radius");

    assertFalse(
            borderRadius.isBlank(),
            "Search field should have distinguishable styling"
    );

    String backgroundColor = searchInput.getCssValue("background-color");

    assertFalse(
            backgroundColor.isBlank(),
            "Search field should have a defined background"
    );

    String placeholder = searchInput.getAttribute("placeholder");

    assertNotNull(
            placeholder,
            "Search field should provide guidance through placeholder text"
    );

    assertFalse(
            placeholder.isBlank(),
            "Search field placeholder should not be empty"
    );

    searchInput.clear();
    searchInput.sendKeys("Chicken Mandi");

    assertEquals(
            "Chicken Mandi",
            searchInput.getAttribute("value"),
            "Search field should be easy to use for entering a recipe name"
    );
}

@Test
@Order(2)
@DisplayName("SE-U-002 - Verify Search Button Is Understandable")
void verifySearchButtonIsUnderstandable() {

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

    assertEquals(
            "pointer",
            searchButton.getCssValue("cursor"),
            "Search button should appear clickable"
    );

    WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-input")
            )
    );

    searchInput.clear();
    searchInput.sendKeys("Chicken Mandi");

    searchButton.click();

    wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("meal")
            )
    );

    assertTrue(
            driver.findElement(By.id("meal")).isDisplayed(),
            "Search action should be initiated using the search button"
    );
}

@Test
@Order(3)
@DisplayName("SE-U-003 - Verify Placeholder Provides Clear Guidance")
void verifyPlaceholderProvidesClearGuidance() {

    WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-input")
            )
    );

    String placeholder = searchInput.getAttribute("placeholder");

    assertNotNull(
            placeholder,
            "Search field should have placeholder text"
    );

    assertEquals(
            "Enter Your Recipe Name",
            placeholder,
            "Placeholder should clearly explain what the user should enter"
    );

    assertTrue(
            searchInput.getAttribute("value").isEmpty(),
            "Placeholder should be visible before entering text"
    );

    searchInput.sendKeys("Chicken Mandi");

    assertEquals(
            "Chicken Mandi",
            searchInput.getAttribute("value"),
            "Search field should accept the entered recipe name"
    );

    assertFalse(
            searchInput.getAttribute("value").isEmpty(),
            "Entered text should replace the placeholder"
    );

    searchInput.clear();

    assertEquals(
            "",
            searchInput.getAttribute("value"),
            "Placeholder should become available again after clearing the input"
    );
}
}
