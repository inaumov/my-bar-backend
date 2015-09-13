package mybar.repository;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import mybar.domain.um.Role;
import mybar.domain.um.User;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserToRoleRelationsTest extends BaseDaoTest {

    @Autowired
    private RoleDaoImpl roleDAO;

    @Autowired
    private UserDao userDao;

    @Test
    public void testUserHasMoreThenOneRole() throws Exception {
        User user = userDao.read(CLIENT1_ID);
        Collection<Role> roles = user.getRoles();
        assertEquals(2, roles.size());
        for (Role role : roles) {
            assertEquals(role, roleDAO.getRole(role.getId()));
        }
    }

    @Test
    public void testAddRole() throws Exception {
        User user = userDao.read(CLIENT2_ID);
        Role role = roleDAO.getRole(2);
        user.getRoles().add(role);
        userDao.update(user);
        em.flush();
        assertEquals(USERS_CNT, getAndAssertUsers().size());
        assertEquals(ROLES_CNT, getAndAssertRoles().size());
        assertEquals(USER_HAS_ROLES_CNT + 1, countRelationsSize());
    }

    @Test
    public void testRemoveRole() throws Exception {
        User user = userDao.read(CLIENT2_ID);
        user.getRoles().clear();
        userDao.update(user);
        em.flush();
        assertEquals(USERS_CNT, getAndAssertUsers().size());
        assertEquals(ROLES_CNT, getAndAssertRoles().size());
        assertEquals(USER_HAS_ROLES_CNT - 1, countRelationsSize());
    }

    @Test
    public void testAddNullRole() throws Exception {
        User user = userDao.read(CLIENT2_ID);
        user.getRoles().add(null);
        userDao.update(user);
        em.flush();
        assertEquals(USERS_CNT, getAndAssertUsers().size());
        assertEquals(ROLES_CNT, getAndAssertRoles().size());
        assertEquals(USER_HAS_ROLES_CNT, countRelationsSize());
    }

}