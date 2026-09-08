import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
        "recipe",
        "recipeDescription",
        "search",
        "wishlist"
})
public class JUnitTests {
}