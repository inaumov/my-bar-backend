package mybar.repository;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mybar.domain.users.Role;
import mybar.domain.users.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:mybar/spring-test-config.xml")
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class},
        inheritListeners = false)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@DatabaseSetup("/dataset.xml")
public class BaseDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    static final int COURIER_ID = 5;
    static final int CLIENT1_ID = 6; // with orders
    static final int CLIENT2_ID = 7; // to remove
    static final String CLIENT1_NAME = "client";
    static final String CLIENT2_NAME = "JohnDoe";

    @PersistenceContext
    protected EntityManager em;

    protected final int ROLES_CNT = 6;
    protected final int USERS_CNT = 7;
    protected final int USER_HAS_ROLES_CNT = 8;

    protected final int MENUS_CNT = 4;
    protected final int COCKTAILS_CNT = 17;
    protected final int ORDERS_CNT = 6;

    @Test
    @ExpectedDatabase(value = "/dataset.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void testPreconditions() throws Exception {
        // do nothing, just load and check dataset
    }

    protected List<User> getAndAssertUsers() {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u", User.class);
        List<User> userList = q.getResultList();
        assertUsers(userList);
        return userList;
    }

    protected List<Role> getAndAssertRoles() {
        TypedQuery<Role> q = em.createQuery("SELECT r FROM Role r", Role.class);
        List<Role> roleList = q.getResultList();
        assertRoles(roleList);
        return roleList;
    }

    protected int countRelationsSize() {
        Query result = em.createNativeQuery("select count(ALL user_id) from user_has_roles");
        return (Integer) result.getSingleResult();
    }

    protected void assertUsers(List<User> all) {
        Iterator<User> it = all.iterator();
        for (int id = 1; id <= all.size(); id++) {
            assertEquals(id, it.next().getId());
        }
    }

    protected void assertRoles(List<Role> all) {
        Iterator<Role> it = all.iterator();
        for (int id = 1; id <= all.size(); id++) {
            assertEquals(id, it.next().getId());
        }
    }

}