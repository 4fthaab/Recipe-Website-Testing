package recipe;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UITest extends RecipeTestBase {

    @Test
    @Order(1)
    @DisplayName("RP-UI-001 - Verify category section layout")
    void categorySectionLayout() {
        List<WebElement> categories = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(".catButton")
                )
        );

        assertFalse(
                categories.isEmpty(),
                "Category cards should be visible"
        );

        for (WebElement category : categories) {
            assertTrue(
                    category.isDisplayed(),
                    "Category card should be visible"
            );

            assertTrue(
                    category.getSize().getWidth() > 0,
                    "Category card should have valid width"
            );

            assertTrue(
                    category.getSize().getHeight() > 0,
                    "Category card should have valid height"
            );

            assertTrue(
                    category.getLocation().getX() >= 0,
                    "Category card should be positioned within the page"
            );

            assertTrue(
                    category.getLocation().getY() >= 0,
                    "Category card should be positioned within the page"
            );
        }
    }

    @Test
    @Order(2)
    @DisplayName("RP-UI-002 - Verify recipe cards display correctly")
    void recipeCardsDisplayCorrectly() {
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

            List<WebElement> images = card.findElements(By.cssSelector("img"));

            assertFalse(
                    images.isEmpty(),
                    "Recipe card should contain an image"
            );

            assertTrue(
                    images.get(0).isDisplayed(),
                    "Recipe image should be visible"
            );

            String cardText = card.getText().trim();

            assertFalse(
                    cardText.isBlank(),
                    "Recipe card should contain recipe information"
            );

            List<WebElement> icons = card.findElements(
                    By.cssSelector("i")
            );

            assertFalse(
                    icons.isEmpty(),
                    "Recipe card should contain a wishlist icon"
            );
        }
    }

    @Test
    @Order(3)
    @DisplayName("RP-UI-003 - Verify navigation and plus controls appearance")
    void navigationAndPlusControlsAppearance() {
        WebElement searchPage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[normalize-space()='Search Page']")
                )
        );

        WebElement wishlist = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[normalize-space()='Wishlist']")
                )
        );

        WebElement addButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.add")
                )
        );

        assertTrue(
                searchPage.isDisplayed(),
                "Search Page control should be visible"
        );

        assertTrue(
                wishlist.isDisplayed(),
                "Wishlist control should be visible"
        );

        assertTrue(
                addButton.isDisplayed(),
                "Plus control should be visible"
        );

        assertTrue(
                searchPage.getSize().getWidth() > 0,
                "Search Page control should have valid width"
        );

        assertTrue(
                searchPage.getSize().getHeight() > 0,
                "Search Page control should have valid height"
        );

        assertTrue(
                wishlist.getSize().getWidth() > 0,
                "Wishlist control should have valid width"
        );

        assertTrue(
                wishlist.getSize().getHeight() > 0,
                "Wishlist control should have valid height"
        );

        assertTrue(
                addButton.getSize().getWidth() > 0,
                "Plus control should have valid width"
        );

        assertTrue(
                addButton.getSize().getHeight() > 0,
                "Plus control should have valid height"
        );

        assertTrue(
                searchPage.getLocation().getX() >= 0,
                "Search Page control should be positioned within the page"
        );

        assertTrue(
                wishlist.getLocation().getX() >= 0,
                "Wishlist control should be positioned within the page"
        );

        assertTrue(
                addButton.getLocation().getX() >= 0,
                "Plus control should be positioned within the page"
        );

        assertTrue(
                searchPage.getLocation().getY() >= 0,
                "Search Page control should be positioned within the page"
        );

        assertTrue(
                wishlist.getLocation().getY() >= 0,
                "Wishlist control should be positioned within the page"
        );

        assertTrue(
                addButton.getLocation().getY() >= 0,
                "Plus control should be positioned within the page"
        );
    }
}