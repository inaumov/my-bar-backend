package mybar.repository.users;

import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import mybar.repository.BaseDaoTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class UserBaseDaoTest extends BaseDaoTest {

    public static final String CLIENT1_ID = "client"; // with orders
    public static final String CLIENT2_ID = "JohnDoe"; // to remove
    public static final String CLIENT1_NAME = "client";
    public static final String CLIENT2_NAME = "JohnDoe";

    protected final int ROLES_CNT = 3;
    protected final int USERS_CNT = 5;
    protected final int USER_HAS_ROLES_CNT = 7;

    @Autowired
    private ApplicationContext appContext;

    @Test
    @ExpectedDatabase(value = "classpath:datasets/usersDataSet.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPreconditions() {
        // do nothing, just load and check dataSet and context loads
        Assertions.assertThat(appContext.getBean(RoleDao.class)).isNotNull();
        Assertions.assertThat(appContext.getBean(UserDao.class)).isNotNull();
    }

}