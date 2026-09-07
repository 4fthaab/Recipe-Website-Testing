package search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
@DisplayName("Functional Testing")
public class FunctionalTest extends SearchTestBase {

@Test
@Order(1)
@DisplayName("SE-F-001 - Search for a Valid Recipe")
void validRecipeSearch() {

    performSearch("Chicken Mandi");

    List<WebElement> results =
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(".meal-item")
            ));

    assertFalse(
            results.isEmpty(),
            "Search results should be displayed for Chicken Mandi"
    );

    boolean recipeFound = results.stream()
            .anyMatch(result ->
                    result.getText()
                            .toLowerCase()
                            .contains("chicken mandi")
            );

    assertTrue(
            recipeFound,
            "Chicken Mandi should be present in the search results"
    );

    WebElement recipeButton = results.get(0)
            .findElement(By.cssSelector(".recipe-btn"));

    recipeButton.click();

    WebElement recipeDetails = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".meal-details-content")
            )
    );

    assertTrue(
            recipeDetails.isDisplayed(),
            "Recipe details should be displayed"
    );

    WebElement recipeTitle = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".recipe-title")
            )
    );

    assertFalse(
            recipeTitle.getText().isBlank(),
            "Recipe title should be displayed"
    );
}

@Test
@Order(2)
@DisplayName("SE-F-002 - Search Using a Different Valid Recipe")
void differentValidRecipeSearch() {

    performSearch("Pizza");

    List<WebElement> results =
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(".meal-item")
            ));

    assertFalse(
            results.isEmpty(),
            "Search should return results for Pizza"
    );

    boolean pizzaFound = results.stream()
            .anyMatch(result ->
                    result.getText()
                            .toLowerCase()
                            .contains("pizza")
            );

    assertTrue(
            pizzaFound,
            "Pizza-related recipe should be present in search results"
    );

    String resultText = driver
            .findElement(By.id("meal"))
            .getText()
            .toLowerCase();

    assertFalse(
            resultText.contains("error"),
            "Search should not display an error"
    );
}

@Test
@Order(3)
@DisplayName("SE-F-003 - Search Using Partial Recipe Name")
void partialRecipeSearch() {

    performSearch("Chick");

    List<WebElement> results =
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(".meal-item")
            ));

    assertFalse(
            results.isEmpty(),
            "Partial search for 'Chick' should return matching recipes"
    );

    boolean relevantResultFound = results.stream()
            .anyMatch(result ->
                    result.getText()
                            .toLowerCase()
                            .contains("chick")
            );

    assertTrue(
            relevantResultFound,
            "Search results should contain recipes matching 'Chick'"
    );
}
}
