package recipeDescription;

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
public class FunctionalTest extends RecipeDescriptionTestBase {

    @Test
    @Order(1)
    @DisplayName("RD-F-001 - Verify recipe description is displayed")
    void recipeDescriptionIsDisplayed() {
        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".up6_tittle")
                )
        );

        assertTrue(
                title.isDisplayed(),
                "Recipe title should be displayed"
        );

        assertEquals(
                RECIPE_NAME,
                title.getText().trim(),
                "Displayed recipe should match the selected recipe"
        );

        WebElement procedure = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".process6_data")
                )
        );

        assertTrue(
                procedure.isDisplayed(),
                "Recipe procedure should be displayed"
        );

        assertFalse(
                procedure.getText().isBlank(),
                "Recipe procedure should not be empty"
        );
    }

    @Test
    @Order(2)
    @DisplayName("RD-F-002 - Verify Like functionality")
    void likeFunctionality() {
        WebElement likeButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("icon61")
                )
        );

        assertEquals(
                "rgba(255, 255, 255, 1)",
                likeButton.getCssValue("color"),
                "Like icon should initially be unselected"
        );

        likeButton.click();

        assertEquals(
                "rgba(255, 130, 37, 1)",
                likeButton.getCssValue("color"),
                "Like icon should change to the liked state"
        );
    }

    @Test
    @Order(3)
    @DisplayName("RD-F-003 - Verify Unlike functionality")
    void unlikeFunctionality() {
        WebElement likeButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("icon61")
                )
        );

        likeButton.click();

        assertEquals(
                "rgba(255, 130, 37, 1)",
                likeButton.getCssValue("color"),
                "Recipe should be liked before testing unlike"
        );

        likeButton.click();

        assertEquals(
                "rgba(255, 255, 255, 1)",
                likeButton.getCssValue("color"),
                "Like icon should return to the unliked state"
        );
    }

    @Test
    @Order(4)
    @DisplayName("RD-F-004 - Verify YouTube link functionality")
    void youtubeLinkFunctionality() {
        WebElement video = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".video6 iframe.gif")
                )
        );

        String videoSource = video.getAttribute("src");

        assertNotNull(
                videoSource,
                "YouTube video source should be present"
        );

        assertTrue(
                videoSource.contains("youtube.com/embed/VBgG6-ekVMo"),
                "Recipe should load the correct YouTube video"
        );
    }

    @Test
    @Order(5)
    @DisplayName("RD-F-005 - Verify comment submission")
    void commentSubmission() {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("in")
                )
        );

        input.sendKeys(COMMENT);

        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn6")
                )
        );

        submitButton.click();

        WebElement submittedComment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".comments6 .usr_cmt")
                )
        );

        assertEquals(
                COMMENT,
                submittedComment.getText().trim(),
                "Submitted comment should be displayed correctly"
        );
    }

    @Test
    @Order(6)
    @DisplayName("RD-F-006 - Verify submitted comment is displayed")
    void submittedCommentIsDisplayed() {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("in")
                )
        );

        input.sendKeys(COMMENT);

        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn6")
                )
        );

        submitButton.click();

        WebElement submittedComment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".comments6 .usr_cmt")
                )
        );

        assertTrue(
                submittedComment.isDisplayed(),
                "Submitted comment should be displayed"
        );

        assertEquals(
                COMMENT,
                submittedComment.getText().trim(),
                "Displayed comment should match the submitted comment"
        );
    }
}