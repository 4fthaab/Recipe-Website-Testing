package recipeDescription;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.JUnitFailureWatcher;

@ExtendWith(JUnitFailureWatcher.class)
public class RecipeDescriptionTestBase {
    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final String RECIPE_DESCRIPTION_URL =
            "https://recipe-finder-two-murex.vercel.app/RecipeViewPageOverAll/RecipeViewPage/RecipeViewPage.html";

    protected static final String RECIPE_NAME = "Avakai Chicken Biryani";
    protected static final String COMMENT = "This recipe looks delicious!";

    @BeforeEach
    void setUp() {
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        openRecipeDescriptionPage();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void openRecipeDescriptionPage() {
        driver.get(RECIPE_DESCRIPTION_URL);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "sessionStorage.clear();" +
                        "sessionStorage.setItem(arguments[0], arguments[1]);" +
                        "sessionStorage.setItem('check', 'true');",
                RECIPE_NAME,
                RECIPE_NAME
        );

        driver.navigate().refresh();
    }
}