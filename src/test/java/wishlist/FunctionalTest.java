package wishlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FunctionalTest extends WishlistTestBase {

    private static final String RECIPE_NAME = "Avakai Chicken Biryani";

    private static final String RECIPE_VALUE =
            "Avakai Chicken Biryani,5,AndhraPradesh,https://vismaifood.com/storage/app/uploads/public/b3b/132/dc7/thumb__700_0_0_0_auto.jpg";

    @Test
    @Order(1)
    @DisplayName("WL-F-001 - Verify wishlisted recipe is displayed")
    void wishlistedRecipeIsDisplayed() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "localStorage.clear();" +
                        "localStorage.setItem(arguments[0], arguments[1]);",
                RECIPE_NAME,
                RECIPE_VALUE
        );

        driver.navigate().refresh();

        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".card")
                )
        );

        assertTrue(
                card.isDisplayed(),
                "Wishlisted recipe card should be displayed"
        );

        assertTrue(
                card.getText().contains(RECIPE_NAME),
                "Wishlisted recipe name should be displayed"
        );

        WebElement image = card.findElement(
                By.cssSelector("img")
        );

        assertTrue(
                image.isDisplayed(),
                "Wishlisted recipe image should be displayed"
        );

        List<WebElement> stars = card.findElements(
                By.cssSelector(".fa-star")
        );

        assertFalse(
                stars.isEmpty(),
                "Recipe rating should be displayed"
        );

        WebElement heart = card.findElement(
                By.cssSelector(".fa-heart")
        );

        assertTrue(
                heart.isDisplayed(),
                "Wishlist heart icon should be displayed"
        );
    }

    @Test
    @Order(2)
    @DisplayName("WL-F-002 - Verify recipe can be removed from Wishlist")
    void recipeCanBeRemovedFromWishlist() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "localStorage.clear();" +
                        "localStorage.setItem(arguments[0], arguments[1]);",
                RECIPE_NAME,
                RECIPE_VALUE
        );

        driver.navigate().refresh();

        WebElement heart = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".card .fa-heart")
                )
        );

        heart.click();

        wait.until(driver ->
                driver.findElements(By.cssSelector(".card")).isEmpty()
        );

        assertTrue(
                driver.findElements(By.cssSelector(".card")).isEmpty(),
                "Recipe should be removed from the Wishlist"
        );

        assertNull(
                js.executeScript(
                        "return localStorage.getItem(arguments[0]);",
                        RECIPE_NAME
                ),
                "Removed recipe should not remain in localStorage"
        );
    }

    @Test
    @Order(3)
    @DisplayName("WL-F-003 - Verify empty Wishlist state")
    void emptyWishlistState() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("localStorage.clear();");
        driver.navigate().refresh();

        WebElement container = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("cnt")
                )
        );

        assertTrue(
                driver.findElements(By.cssSelector(".card")).isEmpty(),
                "Empty Wishlist should not display recipe cards"
        );

        assertTrue(
                container.getText().isBlank()
                        || container.getText().toLowerCase().contains("no")
                        || container.getText().toLowerCase().contains("empty"),
                "Empty Wishlist should display an appropriate empty state message"
        );
    }

    @Test
    @Order(4)
    @DisplayName("WL-F-004 - Verify multiple wishlisted recipes are displayed")
    void multipleWishlistedRecipesAreDisplayed() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "localStorage.clear();" +
                        "localStorage.setItem(arguments[0], arguments[1]);" +
                        "localStorage.setItem(arguments[2], arguments[3]);" +
                        "localStorage.setItem(arguments[4], arguments[5]);",
                "Avakai Chicken Biryani",
                "Avakai Chicken Biryani,5,AndhraPradesh,https://vismaifood.com/storage/app/uploads/public/b3b/132/dc7/thumb__700_0_0_0_auto.jpg",
                "Pot Biryani",
                "Pot Biryani,4,Vizag,https://lh3.googleusercontent.com/EJvV6DVonRs",
                "Chicken Dum Biryani",
                "Chicken Dum Biryani,4,Hyderabad,https://img.freepik.com/free-photo/chicken-biryani"
        );

        driver.navigate().refresh();

        List<WebElement> cards = wait.until(
                ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector(".card"),
                        3
                )
        );

        assertEquals(
                3,
                cards.size(),
                "All selected wishlist recipes should be displayed"
        );

        String wishlistText = driver.findElement(
                By.id("cnt")
        ).getText();

        assertTrue(
                wishlistText.contains("Avakai Chicken Biryani"),
                "Avakai Chicken Biryani should be displayed"
        );

        assertTrue(
                wishlistText.contains("Pot Biryani"),
                "Pot Biryani should be displayed"
        );

        assertTrue(
                wishlistText.contains("Chicken Dum Biryani"),
                "Chicken Dum Biryani should be displayed"
        );
    }

    @Test
    @Order(5)
    @DisplayName("WL-F-005 - Verify Wishlist state persists after page refresh")
    void wishlistPersistsAfterRefresh() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "localStorage.clear();" +
                        "localStorage.setItem(arguments[0], arguments[1]);",
                RECIPE_NAME,
                RECIPE_VALUE
        );

        driver.navigate().refresh();

        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".card")
                )
        );

        assertTrue(
                card.getText().contains(RECIPE_NAME),
                "Wishlisted recipe should remain after refresh"
        );

        assertNotNull(
                js.executeScript(
                        "return localStorage.getItem(arguments[0]);",
                        RECIPE_NAME
                ),
                "Wishlist entry should remain in localStorage after refresh"
        );
    }

    @Test
    @Order(6)
    @DisplayName("WL-F-006 - Verify clicking a wishlist recipe opens its details")
    void wishlistRecipeOpensDetails() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "localStorage.clear();" +
                        "localStorage.setItem(arguments[0], arguments[1]);",
                RECIPE_NAME,
                RECIPE_VALUE
        );

        driver.navigate().refresh();

        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".card")
                )
        );

        new Actions(driver)
                .moveToElement(card)
                .perform();

        WebElement openButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".card .learn-more")
                )
        );

        openButton.click();

        wait.until(
                ExpectedConditions.urlContains("RecipeViewPage")
        );

        assertTrue(
                driver.getCurrentUrl().contains("RecipeViewPage"),
                "User should be navigated to the Recipe Description page"
        );
    }

    @Test
    @Order(7)
    @DisplayName("WL-F-007 - Verify removed recipe does not reappear after refresh")
    void removedRecipeDoesNotReappearAfterRefresh() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "localStorage.clear();" +
                        "localStorage.setItem(arguments[0], arguments[1]);",
                RECIPE_NAME,
                RECIPE_VALUE
        );

        driver.navigate().refresh();

        WebElement recipeCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'card')][.//*[contains(text(),'Avakai Chicken Biryani')]]")
                )
        );

        WebElement heart = recipeCard.findElement(
                By.cssSelector(".fa-heart")
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(heart)
        ).click();

        wait.until(
                ExpectedConditions.urlContains("emptyCart.html")
        );

        driver.navigate().refresh();

        wait.until(driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );

        String pageText = driver.findElement(
                By.tagName("body")
        ).getText();

        assertFalse(
                pageText.contains(RECIPE_NAME),
                "Removed recipe should not reappear after refresh"
        );

        assertNull(
                js.executeScript(
                        "return localStorage.getItem(arguments[0]);",
                        RECIPE_NAME
                ),
                "Removed recipe should not remain in localStorage"
        );
    }
}