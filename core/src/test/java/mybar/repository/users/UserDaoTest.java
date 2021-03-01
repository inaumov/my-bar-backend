package mybar.repository.users;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Iterables;
import mybar.api.users.RoleName;
import mybar.domain.users.Role;
import mybar.domain.users.User;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("classpath:datasets/usersDataSet.xml")
public class UserDaoTest extends UserBaseDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void testFindUserById() throws Exception {
        User user = userDao.getOne(CLIENT2_ID);
        assertNotNull(user);
        assertTrue(user.isActive());
        assertEquals("JohnDoe", user.getUsername());
        assertEquals("John", user.getName());
        assertEquals("Doe", user.getSurname());
        assertEquals("john.doe@mybar.com", user.getEmail());

        assertEquals("JhnD", user.getPassword());
    }

    @Test
    public void testSelectAllUsers() throws Exception {
        Iterable<User> all = userDao.findAll();
        assertEquals(USERS_CNT, Iterables.size(all));
    }

    @ExpectedDatabase(
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
            value = "classpath:datasets/expected/users-create.xml", table = "USERS")
    @Test
    public void testSaveUser() throws Exception {
        User user = new User();
        user.setUsername("phlp111");
        user.setPassword("Passport");
        user.setName("Philippe");
        user.setSurname("Prescott");
        user.setEmail("mail@prescott.com");
        user.setActive(true);
        Role roleRefAnalyst = em.getReference(Role.class, RoleName.ROLE_ADMIN.name());
        user.setRoles(Collections.singletonList(roleRefAnalyst));

        User saved = userDao.save(user);
        em.flush();

        assertNotNull(saved);
    }

    @ExpectedDatabase(
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
            value = "classpath:datasets/expected/users-update.xml", table = "USERS")
    @Test
    public void testUpdateUser() throws Exception {
        User user = userDao.getOne("analyst");
        assertNotNull(user);
        user.setName("Johny");
        user.setSurname("Walker");
        user.setEmail("mail@johnyw.com");
        Role roleRefUser = em.getReference(Role.class, RoleName.ROLE_USER.name());
        user.addRole(roleRefUser);
        User updated = userDao.save(user);
        em.flush();
        assertNotNull(updated);
    }

    @ExpectedDatabase(
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED,
            value = "classpath:datasets/expected/users-delete.xml", table = "USERS")
    @Test
    public void testDeleteUser() throws Exception {
        userDao.deleteById(CLIENT2_ID);
        em.flush();
    }

    @Ignore // TODO
    @Test
    public void testThrowUserHasOrdersExceptionWhenDelete() throws Exception {
        userDao.deleteById(CLIENT1_ID);
    }

    @Test
    public void testFindByEmail() throws Exception {
        User user = userDao.findByEmail("super@mybar.com");
        assertNotNull(user);
        assertEquals("super", user.getUsername());
        assertEquals("super@mybar.com", user.getEmail());
    }

}