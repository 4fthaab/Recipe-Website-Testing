package recipe;

import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Recipe Page - Authorization and Security Testing")
public class SecurityTest extends RecipeTestBase {

    private final By addButton = By.cssSelector(".add");
    private final By recipeForm = By.id("recipeForm");

    private final String adminUser = "nryadav";
    private final String adminPassword = "Nryadav@123";

    private final String normalUser = "normalUser";
    private final String normalPassword = "Normal@123";

    @BeforeEach
    void setNormalUser() {
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('user', arguments[0]);" +
                        "localStorage.setItem('pass', arguments[1]);",
                normalUser,
                normalPassword
        );

        driver.navigate().refresh();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(addButton)
        );
    }

    @Test
    @Order(1)
    @DisplayName("RP-S-001 - Verify normal user cannot add recipes")
    void verifyNormalUserCannotAddRecipes() {
        driver.findElement(addButton).click();

        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertEquals(
                "Only admins have the access to add a Recipe!",
                alert.getText()
        );

        alert.accept();

        Assertions.assertFalse(
                driver.findElement(recipeForm).isDisplayed()
        );
    }

    @Test
    @Order(2)
    @DisplayName("RP-S-002 - Verify Admin can access add-recipe functionality")
    void verifyAdminCanAccessAddRecipe() {
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('user', arguments[0]);" +
                        "localStorage.setItem('pass', arguments[1]);",
                adminUser,
                adminPassword
        );

        driver.navigate().refresh();

        WebElement addButtonElement = wait.until(
                ExpectedConditions.elementToBeClickable(addButton)
        );

        addButtonElement.click();

        WebElement form = wait.until(
                ExpectedConditions.visibilityOfElementLocated(recipeForm)
        );

        Assertions.assertTrue(form.isDisplayed());
    }

    @Test
    @Order(3)
    @DisplayName("RP-S-003 - Verify Admin restriction cannot be bypassed")
    void verifyAdminRestrictionCannotBeBypassed() {

        System.out.println("=== RP-S-003: Admin Restriction Bypass Test ===");
        System.out.println("Attempting to submit the recipe form using JavaScript without Admin credentials...");
        System.out.println("Expected: Recipe count should remain unchanged because the user is not an Admin.");

        JavascriptExecutor js = (JavascriptExecutor) driver;

        int initialItems = driver.findElements(
                By.cssSelector(".card")
        ).size();

        js.executeScript(
                "document.getElementById('recipeForm').style.display='block';"
        );

        WebElement form = driver.findElement(recipeForm);

        js.executeScript(
                "document.getElementById('recipeName').value='Security Test Recipe';" +
                        "document.getElementById('imageURL').value='https://example.com/test.jpg';" +
                        "document.getElementById('stars').value='5';" +
                        "document.getElementById('ingredients').value='Test ingredients';" +
                        "document.getElementById('region').value='Test Region';" +
                        "document.getElementById('process').value='Test process';" +
                        "document.getElementById('videoURL').value='https://example.com/test';" +
                        "document.getElementById('category').value=\"Biryani's\";"
        );

        js.executeScript(
                "arguments[0].dispatchEvent(new Event('submit', {bubbles:true,cancelable:true}));",
                form
        );

        int finalItems = driver.findElements(By.cssSelector(".card")).size();

        System.out.println("Initial recipe count: " + initialItems);
        System.out.println("Final recipe count: " + finalItems);

        Assertions.assertEquals(
                initialItems,
                finalItems,
                "SECURITY FAILURE: Normal user was able to bypass the Admin restriction and add a recipe."
        );
    }
}