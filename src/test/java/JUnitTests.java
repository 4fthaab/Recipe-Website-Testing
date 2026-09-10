
import org.junit.platform.suite.api.SelectClasses;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.SelectPackages;

import org.junit.platform.suite.api.Suite;
import utils.JUnitFailureWatcher;

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
@ExtendWith(JUnitFailureWatcher.class)
public class JUnitTests {
}