package mybar.repository.users;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import mybar.api.users.RoleName;
import mybar.domain.users.Role;
import mybar.domain.users.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("classpath:datasets/usersDataSet.xml")
public class UserToRoleRelationsTest extends UserBaseDaoTest {

    @Autowired
    private RoleDao roleDAO;

    @Autowired
    private UserDao userDao;

    @Test
    public void testUserHasMoreThenOneRole() throws Exception {
        User user = userDao.getOne(CLIENT2_ID);
        assertNotNull(user);
        Collection<Role> roles = user.getRoles();
        assertEquals(ROLES_CNT, roles.size());
        for (Role role : roles) {
            assertEquals(role, roleDAO.getOne(role.getRoleName()));
        }
    }

    @Test
    public void testAddRole() throws Exception {
        User user = userDao.getOne(CLIENT2_ID);
        assertNotNull(user);

        Role role = roleDAO.getOne(RoleName.ROLE_ADMIN.name());
        assertNotNull(role);
        user.addRole(role);
        userDao.save(user);
        em.flush();

        assertEquals(USERS_CNT, countRowsInTable("USERS"));
        assertEquals(ROLES_CNT, countRowsInTable("ROLES"));
        assertEquals(USER_HAS_ROLES_CNT + 1, countRowsInTable("USER_HAS_ROLES"));
    }

    @Test
    public void testRemoveRole() throws Exception {
        User user = userDao.getOne(CLIENT2_ID);
        assertNotNull(user);

        int nmbOfRoles = user.getRoles().size();
        assertEquals(3, nmbOfRoles);
        user.getRoles().clear();
        userDao.save(user);
        em.flush();

        assertEquals(USERS_CNT, countRowsInTable("USERS"));
        assertEquals(ROLES_CNT, countRowsInTable("ROLES"));
        assertEquals(USER_HAS_ROLES_CNT - nmbOfRoles, countRowsInTable("USER_HAS_ROLES"));
    }

    @Test
    public void testAddNullRole() throws Exception {
        User user = userDao.getOne(CLIENT2_ID);
        assertNotNull(user);
        user.addRole(null);
        userDao.save(user);
        em.flush();

        assertEquals(USERS_CNT, countRowsInTable("USERS"));
        assertEquals(ROLES_CNT, countRowsInTable("ROLES"));
        assertEquals(USER_HAS_ROLES_CNT, countRowsInTable("USER_HAS_ROLES"));
    }

}