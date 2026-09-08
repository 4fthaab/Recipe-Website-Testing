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
public class ValidationTest extends RecipeDescriptionTestBase {

    @Test
    @Order(1)
    @DisplayName("RD-V-001 - Submit an empty comment")
    void submitEmptyComment() {

        driver.get(RECIPE_DESCRIPTION_URL);

        WebElement comment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("in")
                )
        );

        // Make sure the comment field is empty
        comment.clear();

        // Capture the recipe content before submitting
        WebElement video = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".gif")
                )
        );

        WebElement image = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".img6")
                )
        );

        String videoBefore = video.getAttribute("src");
        String imageBefore = image.getAttribute("src");

        WebElement submit = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn6")
                )
        );

        submit.click();

        /*
         * EXPECTED BEHAVIOUR:
         * Empty comment should be rejected without reloading/breaking
         * the recipe description page.
         *
         * ACTUAL APPLICATION:
         * event.preventDefault() is not called when the value is empty,
         * causing the form submission and recipe content to disappear.
         *
         * Therefore this test should FAIL on the current application.
         */

        WebElement videoAfter = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".gif")
                )
        );

        WebElement imageAfter = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".img6")
                )
        );

        String videoAfterSrc = videoAfter.getAttribute("src");
        String imageAfterSrc = imageAfter.getAttribute("src");

        assertEquals(
                videoBefore,
                videoAfterSrc,
                "Recipe video should remain unchanged after empty comment submission"
        );

        assertEquals(
                imageBefore,
                imageAfterSrc,
                "Recipe image should remain unchanged after empty comment submission"
        );

        assertTrue(
                driver.findElement(By.id("in")).isDisplayed(),
                "Comment field should remain available after empty submission"
        );
    }


    @Test
    @Order(2)
    @DisplayName("RD-V-002 - Submit a comment containing only spaces")
    void submitSpacesOnlyComment() {

        driver.get(RECIPE_DESCRIPTION_URL);

        WebElement comment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("in")
                )
        );

        String spacesComment = "     ";

        comment.clear();
        comment.sendKeys(spacesComment);

        WebElement submit = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn6")
                )
        );

        /*
         * Count the existing comments before submission.
         */
        int commentsBefore = driver.findElements(
                By.cssSelector(".usr_cmt")
        ).size();

        submit.click();

        /*
         * EXPECTED BEHAVIOUR:
         * A spaces-only comment should be rejected.
         *
         * ACTUAL APPLICATION:
         * cmt6() checks:
         *
         *     if(a.value!="")
         *
         * "     " is not equal to "", so the application accepts
         * and displays the spaces-only comment.
         *
         * Therefore this test should FAIL on the current application.
         */

        int commentsAfter = driver.findElements(
                By.cssSelector(".usr_cmt")
        ).size();

        assertEquals(
                commentsBefore,
                commentsAfter,
                "Spaces-only comment should not be submitted"
        );

        /*
         * Extra verification:
         * There should not be a submitted comment containing only
         * whitespace.
         */
        boolean spacesCommentDisplayed = driver.findElements(
                        By.cssSelector(".usr_cmt")
                ).stream()
                .anyMatch(element ->
                        element.getText().equals(spacesComment)
                );

        assertFalse(
                spacesCommentDisplayed,
                "Spaces-only comment should not appear in the comments section"
        );
    }


    @Test
    @Order(3)
    @DisplayName("RD-V-003 - Submit a very long comment")
    void submitVeryLongComment() {

        driver.get(RECIPE_DESCRIPTION_URL);

        WebElement comment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("in")
                )
        );

        String longComment =
                "This is a very long comment used to validate the recipe description page comment field. "
                        .repeat(15);

        comment.clear();
        comment.sendKeys(longComment);

        WebElement submit = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn6")
                )
        );

        submit.click();

        /*
         * The application should remain usable after submitting
         * a long comment.
         */

        assertTrue(
                driver.findElement(By.tagName("body")).isDisplayed(),
                "Recipe Description page should remain displayed after submitting a long comment"
        );

        assertTrue(
                driver.findElement(By.id("in")).isDisplayed(),
                "Comment field should remain visible after submitting a long comment"
        );
    }
}