package recipe;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsabilityTest extends RecipeTestBase {

    @Test
    @Order(1)
    @DisplayName("RP-U-001 - Verify categories are easy to understand")
    void categoriesAreEasyToUnderstand() {
        List<WebElement> categories = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(".catButton")
                )
        );

        assertFalse(
                categories.isEmpty(),
                "Category cards should be displayed"
        );

        for (WebElement category : categories) {
            assertTrue(
                    category.isDisplayed(),
                    "Category should be visible"
            );

            String text = category.getText().trim();

            assertFalse(
                    text.isBlank(),
                    "Category should have a meaningful name"
            );

            List<WebElement> images = category.findElements(
                    By.cssSelector("img")
            );

            assertFalse(
                    images.isEmpty(),
                    "Category should contain an image"
            );

            assertTrue(
                    images.get(0).isDisplayed(),
                    "Category image should be visible"
            );
        }
    }

    @Test
    @Order(2)
    @DisplayName("RP-U-002 - Verify recipe cards are easy to interact with")
    void recipeCardsAreEasyToInteractWith() {
        List<WebElement> cards = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(".card")
                )
        );

        assertFalse(
                cards.isEmpty(),
                "Recipe cards should be displayed"
        );

        for (WebElement card : cards) {
            assertTrue(
                    card.isDisplayed(),
                    "Recipe card should be visible"
            );

            String cardText = card.getText().trim();

            assertFalse(
                    cardText.isBlank(),
                    "Recipe card should contain recipe information"
            );

            List<WebElement> images = card.findElements(
                    By.cssSelector("img")
            );

            assertFalse(
                    images.isEmpty(),
                    "Recipe card should contain a recipe image"
            );

            assertTrue(
                    images.get(0).isDisplayed(),
                    "Recipe image should be visible"
            );

            List<WebElement> icons = card.findElements(
                    By.cssSelector("i")
            );

            assertFalse(
                    icons.isEmpty(),
                    "Recipe card should contain a wishlist icon"
            );

            assertTrue(
                    icons.stream().anyMatch(icon ->
                            icon.getAttribute("class") != null &&
                                    icon.getAttribute("class").contains("heart")
                    ),
                    "Recipe card should contain a heart icon for Wishlist"
            );
        }
    }

    @Test
    @Order(3)
    @DisplayName("RP-U-003 - Verify Admin restriction message is understandable")
    void adminRestrictionMessageIsUnderstandable() {
        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("div.add")
                )
        );

        addButton.click();

        wait.until(driver -> {
            try {
                return driver.switchTo().alert() != null;
            } catch (Exception e) {
                return false;
            }
        });

        String message = driver.switchTo().alert().getText();

        assertTrue(
                message.toLowerCase().contains("admin"),
                "Restriction message should mention Admin"
        );

        assertTrue(
                message.toLowerCase().contains("add") ||
                        message.toLowerCase().contains("recipe"),
                "Restriction message should explain that adding recipes is restricted"
        );

        driver.switchTo().alert().accept();
    }
}