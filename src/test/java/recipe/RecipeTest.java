package recipe;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        FunctionalTest.class,
        ValidationTest.class,
        SecurityTest.class,
        UITest.class,
        UsabilityTest.class
})


public class RecipeTest {
}
