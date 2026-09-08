package recipeDescription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UITest extends RecipeDescriptionTestBase {

    @Test
    @Order(1)
    @DisplayName("RD-UI-001 - Verify recipe description layout")
    void recipeDescriptionLayout() {

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".up6_tittle")
                )
        );

        WebElement image = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".img6")
                )
        );

        WebElement video = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".gif")
                )
        );

        WebElement ingredients = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".ing6_data")
                )
        );

        WebElement procedure = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".process6_data")
                )
        );

        assertTrue(
                title.isDisplayed(),
                "Recipe name should be clearly visible"
        );

        assertFalse(
                title.getText().isBlank(),
                "Recipe name should not be empty"
        );

        assertTrue(
                image.isDisplayed(),
                "Recipe image should be clearly visible"
        );

        assertFalse(
                image.getAttribute("src").isBlank(),
                "Recipe image should have a valid source"
        );

        assertTrue(
                video.isDisplayed(),
                "Recipe video should be visible"
        );

        assertFalse(
                video.getAttribute("src").isBlank(),
                "Recipe video should have a valid source"
        );

        assertTrue(
                ingredients.isDisplayed(),
                "Ingredients section should be visible"
        );

        assertFalse(
                ingredients.getText().isBlank(),
                "Ingredients should not be empty"
        );

        assertTrue(
                procedure.isDisplayed(),
                "Procedure section should be visible"
        );

        assertFalse(
                procedure.getText().isBlank(),
                "Procedure should not be empty"
        );

        Rectangle titleRect = title.getRect();
        Rectangle imageRect = image.getRect();
        Rectangle videoRect = video.getRect();
        Rectangle ingredientsRect = ingredients.getRect();
        Rectangle procedureRect = procedure.getRect();

        assertTrue(
                titleRect.getX() >= 0,
                "Recipe name should remain within the page"
        );

        assertTrue(
                imageRect.getX() >= 0,
                "Recipe image should remain within the page"
        );

        assertTrue(
                videoRect.getX() >= 0,
                "Recipe video should remain within the page"
        );

        assertTrue(
                ingredientsRect.getX() >= 0,
                "Ingredients should remain within the page"
        );

        assertTrue(
                procedureRect.getX() >= 0,
                "Procedure should remain within the page"
        );

        assertFalse(
                overlaps(titleRect, imageRect),
                "Recipe name and image should not overlap"
        );

        assertFalse(
                overlaps(imageRect, ingredientsRect),
                "Recipe image and ingredients should not overlap"
        );

        assertFalse(
                overlaps(ingredientsRect, procedureRect),
                "Ingredients and procedure should not overlap"
        );
    }

    @Test
    @Order(2)
    @DisplayName("RD-UI-002 - Verify Like/Unlike icon appearance")
    void likeUnlikeIconAppearance() {

        WebElement like = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("icon61")
                )
        );

        WebElement dislike = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("icon62")
                )
        );

        assertTrue(
                like.isDisplayed(),
                "Like icon should be clearly visible"
        );

        assertTrue(
                dislike.isDisplayed(),
                "Unlike icon should be clearly visible"
        );

        String initialLikeColor = like.getCssValue("color");

        like.click();

        wait.until(driver ->
                !like.getCssValue("color").equals(initialLikeColor)
        );

        String likedColor = like.getCssValue("color");

        assertNotEquals(
                initialLikeColor,
                likedColor,
                "Like icon visual state should change after clicking"
        );

        assertTrue(
                like.isDisplayed(),
                "Like icon should remain visible after clicking"
        );

        like.click();

        wait.until(driver ->
                like.getCssValue("color").equals(initialLikeColor)
        );

        assertEquals(
                initialLikeColor,
                like.getCssValue("color"),
                "Like icon should return to its original state"
        );

        assertTrue(
                dislike.isDisplayed(),
                "Unlike icon should remain visible"
        );
    }

    @Test
    @Order(3)
    @DisplayName("RD-UI-003 - Verify comments section layout")
    void commentsSectionLayout() {

        WebElement commentsSection = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".comments6")
                )
        );

        WebElement commentInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("in")
                )
        );

        WebElement submitButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".btn6")
                )
        );

        assertTrue(
                commentsSection.isDisplayed(),
                "Comments section should be visible"
        );

        assertTrue(
                commentInput.isDisplayed(),
                "Comment field should be visible"
        );

        assertTrue(
                commentInput.isEnabled(),
                "Comment field should be enabled"
        );

        assertTrue(
                submitButton.isDisplayed(),
                "Submit button should be visible"
        );

        assertTrue(
                submitButton.isEnabled(),
                "Submit button should be enabled"
        );

        List<WebElement> comments = commentsSection.findElements(
                By.cssSelector(".cmt1")
        );

        assertFalse(
                comments.isEmpty(),
                "Existing comments should be displayed"
        );

        for (WebElement comment : comments) {

            assertTrue(
                    comment.isDisplayed(),
                    "Comment should be clearly visible"
            );

            List<WebElement> usernames = comment.findElements(
                    By.cssSelector(".usr_id")
            );

            List<WebElement> commentTexts = comment.findElements(
                    By.cssSelector(".usr_cmt")
            );

            assertFalse(
                    usernames.isEmpty(),
                    "Comment should contain a username"
            );

            assertFalse(
                    commentTexts.isEmpty(),
                    "Comment should contain comment text"
            );

            assertTrue(
                    usernames.get(0).isDisplayed(),
                    "Username should be visible"
            );

            assertTrue(
                    commentTexts.get(0).isDisplayed(),
                    "Comment text should be visible"
            );
        }

        Rectangle inputRect = commentInput.getRect();
        Rectangle buttonRect = submitButton.getRect();
        Rectangle containerRect = commentsSection.getRect();

        assertFalse(
                overlaps(inputRect, buttonRect),
                "Comment field and Submit button should not overlap"
        );

        assertTrue(
                inputRect.getX() >= containerRect.getX(),
                "Comment field should remain inside the comments container"
        );

        assertTrue(
                buttonRect.getX() >= containerRect.getX(),
                "Submit button should remain inside the comments container"
        );

        for (WebElement comment : comments) {

            WebElement commentText = comment.findElement(
                    By.cssSelector(".usr_cmt")
            );

            Rectangle textRect = commentText.getRect();

            assertTrue(
                    textRect.getX() >= containerRect.getX(),
                    "Comment text should remain inside the comments container horizontally"
            );

            assertTrue(
                    textRect.getX() + textRect.getWidth()
                            <= containerRect.getX() + containerRect.getWidth(),
                    "Comment text should not extend outside the comments container"
            );
        }
    }

    private boolean overlaps(Rectangle first, Rectangle second) {
        return first.getX() < second.getX() + second.getWidth()
                && first.getX() + first.getWidth() > second.getX()
                && first.getY() < second.getY() + second.getHeight()
                && first.getY() + first.getHeight() > second.getY();
    }
}