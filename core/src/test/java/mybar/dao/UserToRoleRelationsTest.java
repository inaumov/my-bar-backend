package mybar.dao;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import mybar.entity.um.Role;
import mybar.entity.um.User;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        for (Role role : roles) {
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