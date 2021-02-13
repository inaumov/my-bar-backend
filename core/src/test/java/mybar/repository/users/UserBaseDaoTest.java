package mybar.repository.users;

import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import mybar.repository.BaseDaoTest;
import org.junit.jupiter.api.*;

public abstract class UserBaseDaoTest extends BaseDaoTest {

    public static final String CLIENT1_ID = "client"; // with orders
    public static final String CLIENT2_ID = "JohnDoe"; // to remove
    public static final String CLIENT1_NAME = "client";
    public static final String CLIENT2_NAME = "JohnDoe";

    protected final int ROLES_CNT = 4;
    protected final int USERS_CNT = 5;
    protected final int USER_HAS_ROLES_CNT = 7;

    @Test
    @ExpectedDatabase(value = "classpath:datasets/usersDataSet.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() throws Exception {
        // do nothing, just load and check dataSet
    }

}