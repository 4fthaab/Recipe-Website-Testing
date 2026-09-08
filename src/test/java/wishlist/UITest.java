package wishlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UITest extends WishlistTestBase {

    private static final String RECIPE_NAME = "Avakai Chicken Biryani";

    private static final String RECIPE_VALUE =
            "Avakai Chicken Biryani,5,AndhraPradesh,https://vismaifood.com/storage/app/uploads/public/b3b/132/dc7/thumb__700_0_0_0_auto.jpg";

    @Test
    @Order(1)
    @DisplayName("WL-UI-001 - Verify wishlist recipe card layout")
    void wishlistRecipeCardLayout() {
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
                "Wishlist recipe card should be displayed"
        );

        WebElement image = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".card .photo img")
                )
        );

        assertTrue(
                image.isDisplayed(),
                "Recipe image should be clearly visible"
        );

        WebElement name = wait.until(driver -> {
            List<WebElement> elements = card.findElements(
                    By.cssSelector(".name")
            );

            for (WebElement element : elements) {
                if (element.isDisplayed() && !element.getText().isBlank()) {
                    return element;
                }
            }

            return null;
        });

        assertTrue(
                name.isDisplayed(),
                "Recipe name should be clearly visible"
        );

        assertFalse(
                name.getText().isBlank(),
                "Recipe name should not be empty"
        );

        assertFalse(
                name.getText().isBlank(),
                "Recipe name should not be empty"
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
                "Wishlist heart icon should be clearly visible"
        );

        Rectangle cardRect = card.getRect();
        Rectangle imageRect = image.getRect();
        Rectangle nameRect = name.getRect();
        Rectangle heartRect = heart.getRect();

        assertTrue(
                imageRect.getX() >= cardRect.getX()
                        && imageRect.getX() + imageRect.getWidth() <= cardRect.getX() + cardRect.getWidth(),
                "Recipe image should remain within the card"
        );

        assertTrue(
                nameRect.getX() >= cardRect.getX()
                        && nameRect.getX() + nameRect.getWidth() <= cardRect.getX() + cardRect.getWidth(),
                "Recipe name should remain within the card"
        );

        assertTrue(
                heartRect.getX() >= cardRect.getX()
                        && heartRect.getX() + heartRect.getWidth() <= cardRect.getX() + cardRect.getWidth(),
                "Wishlist heart should remain within the card"
        );

        assertTrue(
                imageRect.getY() >= cardRect.getY(),
                "Recipe image should be properly positioned"
        );

        assertTrue(
                nameRect.getY() >= cardRect.getY(),
                "Recipe name should be properly positioned"
        );

        assertTrue(
                heartRect.getY() >= cardRect.getY(),
                "Wishlist heart should be properly positioned"
        );
    }

    @Test
    @Order(2)
    @DisplayName("WL-UI-002 - Verify empty Wishlist container layout")
    void emptyWishlistContainerLayout() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("localStorage.clear();");
        driver.navigate().refresh();

        WebElement container = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("cnt")
                )
        );

        assertTrue(
                container.isDisplayed(),
                "Empty Wishlist container should be displayed"
        );

        assertTrue(
                container.getSize().getWidth() > 0,
                "Empty Wishlist container should have valid width"
        );

        assertTrue(
                container.getSize().getHeight() > 0,
                "Empty Wishlist container should have valid height"
        );

        assertTrue(
                driver.findElements(By.cssSelector(".card")).isEmpty(),
                "Empty Wishlist should not display recipe cards"
        );

        List<WebElement> children = container.findElements(
                By.xpath("./*")
        );

        assertTrue(
                children.isEmpty(),
                "Empty Wishlist should not contain unnecessary elements"
        );

        Rectangle containerRect = container.getRect();

        assertTrue(
                containerRect.getX() >= 0,
                "Wishlist container should be positioned within the page"
        );

        assertTrue(
                containerRect.getY() >= 0,
                "Wishlist container should be positioned within the page"
        );
    }

    @Test
    @Order(3)
    @DisplayName("WL-UI-003 - Verify wishlist heart icon appearance")
    void wishlistHeartIconAppearance() {
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

        WebElement heart = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".card .fa-heart")
                )
        );

        assertTrue(
                heart.isDisplayed(),
                "Wishlist heart icon should be displayed"
        );

        String classes = heart.getAttribute("class");

        assertTrue(
                classes.contains("fa-heart"),
                "Wishlist icon should use the heart icon"
        );

        String color = heart.getCssValue("color");

        assertTrue(
                color.contains("255, 0, 0")
                        || color.equalsIgnoreCase("red"),
                "Wishlist heart should display the active red state"
        );

        String fontWeight = heart.getCssValue("font-weight");

        assertTrue(
                Integer.parseInt(fontWeight) >= 600,
                "Active wishlist heart should have a highlighted appearance"
        );

        Rectangle cardRect = card.getRect();
        Rectangle heartRect = heart.getRect();

        assertTrue(
                heartRect.getX() >= cardRect.getX()
                        && heartRect.getX() + heartRect.getWidth() <= cardRect.getX() + cardRect.getWidth(),
                "Wishlist heart should be correctly positioned within the card"
        );

        assertTrue(
                heartRect.getY() >= cardRect.getY()
                        && heartRect.getY() + heartRect.getHeight() <= cardRect.getY() + cardRect.getHeight(),
                "Wishlist heart should be correctly positioned vertically"
        );
    }
}