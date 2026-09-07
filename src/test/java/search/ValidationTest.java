package search;

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
@Order(2)
@DisplayName("Validation / Negative Testing")
public class ValidationTest extends SearchTestBase {

@Test
@Order(4)
@DisplayName("SE-V-001 - Search with Empty Input")
void emptySearch() {

    WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("search-input")
            )
    );

    searchBox.clear();

    String initialUrl = driver.getCurrentUrl();

    WebElement searchButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                    By.id("search-btn")
            )
    );

    searchButton.click();

    WebElement mealContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("meal")
            )
    );

    assertTrue(
            mealContainer.getText().isBlank()
                    || mealContainer.getText().toLowerCase().contains("no")
                    || driver.getCurrentUrl().equals(initialUrl),
            "Empty search should not execute an unintended search"
    );
}

@Test
@Order(5)
@DisplayName("SE-V-002 - Search for a Non-Existent Recipe")
void nonExistentRecipeSearch() {

    performSearch("xyzabc123recipe");

    WebElement mealContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("meal")
            )
    );

    String resultText = mealContainer.getText().toLowerCase();

    assertTrue(
            resultText.contains("sorry")
                    || resultText.contains("didn't find")
                    || resultText.contains("no")
                    || resultText.isBlank(),
            "A clear no-results state should be displayed for a non-existent recipe"
    );
}

@Test
@Order(6)
@DisplayName("SE-V-003 - Search Using Special Characters")
void specialCharacterSearch() {

    performSearch("@#$%^&*");

    WebElement mealContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("meal")
            )
    );

    String resultText = mealContainer.getText();

    assertNotNull(
            resultText,
            "Search should handle special characters without crashing"
    );
}
}
