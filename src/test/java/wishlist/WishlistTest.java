package wishlist;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        FunctionalTest.class,
        UITest.class
})

public class WishlistTest {
}
