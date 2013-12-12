package mybar.dao;

import mybar.entity.um.Role;
import mybar.entity.um.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:mybar/spring-test-config.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class UserToRoleRelationsTest extends BaseDaoTest {

    @Autowired
    private RoleDAOImpl roleDAO;

    @Autowired
    private UserDAO userDAO;

    @Test
    public void testUserHasMoreThenOneRole() throws Exception {
        User user = userDAO.read(CLIENT1_ID);
        Collection<Role> roles = user.getRoles();
        assertEquals(2, roles.size());
        for(Role role : roles) {
            assertEquals(role, roleDAO.getRole(role.getId()));
        }
    }

    @Test
    public void testAddRole() throws Exception {
        User user = userDAO.read(CLIENT2_ID);
        Role role = roleDAO.getRole(2);
        user.getRoles().add(role);
        userDAO.update(user);
        em.flush();
        assertEquals(USERS_CNT, getAndAssertUsers().size());
        assertEquals(ROLES_CNT, getAndAssertRoles().size());
        assertEquals(USER_HAS_ROLES_CNT + 1, countRelationsSize());
    }

    @Test
    public void testRemoveRole() throws Exception {
        User user = userDAO.read(CLIENT2_ID);
        user.getRoles().clear();
        userDAO.update(user);
        em.flush();
        assertEquals(USERS_CNT, getAndAssertUsers().size());
        assertEquals(ROLES_CNT, getAndAssertRoles().size());
        assertEquals(USER_HAS_ROLES_CNT - 1, countRelationsSize());
    }

    @Test
    public void testAddNullRole() throws Exception {
        User user = userDAO.read(CLIENT2_ID);
        user.getRoles().add(null);
        userDAO.update(user);
        em.flush();
        assertEquals(USERS_CNT, getAndAssertUsers().size());
        assertEquals(ROLES_CNT, getAndAssertRoles().size());
        assertEquals(USER_HAS_ROLES_CNT, countRelationsSize());
    }

}