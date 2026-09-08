package search;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        FunctionalTest.class,
        ValidationTest.class,
        UITest.class,
        UsabilityTest.class
})
public class SearchTest {
}
