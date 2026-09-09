import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import recipe.RecipeTest;
import recipeDescription.RecipeDescriptionTest;
import search.SearchTest;
import wishlist.WishlistTest;

@Suite
@SelectClasses({
        SearchTest.class,
        RecipeTest.class,
        RecipeDescriptionTest.class,
        WishlistTest.class
})
public class JUnitTests {
}