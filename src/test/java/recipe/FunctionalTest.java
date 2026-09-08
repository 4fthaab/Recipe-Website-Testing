package recipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Recipe Page - Functional Testing")
public class FunctionalTest extends RecipeTestBase {

    private final By biryaniCategory = By.xpath(
            "//catButton[contains(@class,'catButton')]//p[normalize-space()=\"Biryani's\"]"
    );
    private final By wishlistLink = By.xpath(
            "//a[.//p[normalize-space()='Wishlist']]"
    );
    private final By searchPageLink = By.xpath(
            "//a[.//p[normalize-space()='Search Page']]"
    );

    @BeforeEach
    void openRecipePage() {
        driver.get("https://recipe-finder-two-murex.vercel.app/RecipePage/Recipe.html");
        wait.until(ExpectedConditions.urlContains("/RecipePage/Recipe.html"));
    }

    @Test
    @Order(1)
    @DisplayName("RP-F-001 - Verify category selection")
    void verifyCategorySelection() {
        WebElement category = wait.until(
                ExpectedConditions.elementToBeClickable(biryaniCategory)
        );
        category.click();

        WebElement item = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p[contains(normalize-space(),'Avakai Chicken Biryani')]")
                )
        );

        Assertions.assertTrue(item.isDisplayed());
    }

    @Test
    @Order(2)
    @DisplayName("RP-F-002 - Verify navigation to Search Page")
    void verifyNavigationToSearchPage() {
        WebElement searchPage = wait.until(
                ExpectedConditions.elementToBeClickable(searchPageLink)
        );
        searchPage.click();

        wait.until(ExpectedConditions.urlContains("/Searchpage/"));

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/Searchpage/")
        );
    }

    @Test
    @Order(3)
    @DisplayName("RP-F-003 - Verify navigation to Wishlist")
    void verifyNavigationToWishlist() {
        WebElement wishlist = wait.until(
                ExpectedConditions.elementToBeClickable(wishlistLink)
        );
        wishlist.click();

        wait.until(ExpectedConditions.urlContains("/Catpage2/"));

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/Catpage2/")
        );
    }
}