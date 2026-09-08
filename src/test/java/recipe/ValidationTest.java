package recipe;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Recipe Page - Validation Testing")
public class ValidationTest extends RecipeTestBase {

    private final By addButton = By.cssSelector(".add");
    private final By addRecipeForm = By.id("recipeForm");
    private final String adminRestrictionMessage = "Only admins have the access to add a Recipe!";

    @BeforeEach
    void loginAsNormalUser() {
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(addButton));
    }

    @Test
    @Order(1)
    @DisplayName("RP-V-001 - Verify normal user cannot add a recipe")
    void verifyNormalUserCannotAddRecipe() {
        WebElement add = wait.until(
                ExpectedConditions.elementToBeClickable(addButton)
        );

        add.click();

        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertEquals(
                adminRestrictionMessage,
                alert.getText()
        );

        alert.accept();

        Assertions.assertFalse(
                driver.findElement(addRecipeForm).isDisplayed()
        );
    }

    @Test
    @Order(2)
    @DisplayName("RP-V-002 - Verify empty category is handled properly")
    void verifyEmptyCategoryIsHandledProperly() {

        System.out.println("=== RP-V-002: Empty Category Handling Test ===");
        System.out.println("Test cannot be executed because no category with an empty recipe list is available.");
        System.out.println("Test Status: BLOCKED - Required test data is not available on the current Recipe page.");

        Assertions.fail(
                "TEST BLOCKED: No empty category is available in the current Recipe page data."
        );
    }
    @Test
    @Order(3)
    @DisplayName("RP-V-003 - Verify repeated clicks on '+' are handled properly")
    void verifyRepeatedClicksOnAddButton() {
        int initialCards = driver.findElements(
                By.cssSelector(".card")
        ).size();

        for (int i = 0; i < 3; i++) {
            WebElement add = wait.until(
                    ExpectedConditions.elementToBeClickable(addButton)
            );

            add.click();

            Alert alert = wait.until(
                    ExpectedConditions.alertIsPresent()
            );

            Assertions.assertEquals(
                    adminRestrictionMessage,
                    alert.getText()
            );

            alert.accept();
        }

        int finalCards = driver.findElements(
                By.cssSelector(".card")
        ).size();

        Assertions.assertEquals(initialCards, finalCards);

        Assertions.assertFalse(
                driver.findElement(addRecipeForm).isDisplayed()
        );
    }
}