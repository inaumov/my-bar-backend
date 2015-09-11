package mybar.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import mybar.ActiveStatus;
import mybar.entity.um.Role;
import mybar.entity.um.User;

import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class UserDaoTest extends BaseDaoTest {

    @Autowired
    private UserDAO userDao;

    @Test
    public void testReadUserById() throws Exception {
        User client1 = userDao.read(CLIENT1_ID);
        User client2 = userDao.read(CLIENT2_ID);
        assertEquals(CLIENT1_NAME, client1.getName());
        assertEquals(CLIENT2_NAME, client2.getName());
    }

    @Test
    public void testSelectAllUsers() throws Exception {
        List<User> all = userDao.findAll();
        assertEquals(USERS_CNT, all.size());
        assertUsers(all);
    }

    @Test
    public void testSaveUser() throws Exception {
        User user = new User();
        user.setLogin("Login");
        user.setPassword("Passport");
        user.setName("Philippe");
        user.setSurname("Prescott");
        user.setEmail("mail@prescott.com");
        user.setAddress("46 Kildare Street, Dublin");
        user.setActiveStatus(ActiveStatus.ENABLED);
        Role roleReference = em.getReference(Role.class, 6);
        user.setRoles(Arrays.asList(new Role[]{roleReference}));
        User saved = userDao.create(user);
        assertFalse(saved.getId() == 0);
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u", User.class);
        assertEquals(USERS_CNT + 1, q.getResultList().size());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User courier = userDao.read(COURIER_ID);
        assertEquals("courier", courier.getName());
        assertEquals("courier", courier.getSurname());
        assertEquals("courier@mybar.com", courier.getEmail());
        assertEquals("", courier.getAddress());
        courier.setName("Johny");
        courier.setSurname("Walker");
        courier.setEmail("mail@johndoe.com");
        courier.setAddress("Gustav Mahlerlaan 40, 1082 MC Amsterdam");
        User updated = userDao.update(courier);
        assertEquals("Johny", updated.getName());
        assertEquals("Walker", updated.getSurname());
        assertEquals("mail@johndoe.com", updated.getEmail());
        assertEquals("Gustav Mahlerlaan 40, 1082 MC Amsterdam", updated.getAddress());
    }

    @Test
    public void testDeleteUser() throws Exception {
        userDao.delete(CLIENT2_ID);
        assertEquals(USERS_CNT - 1, getAndAssertUsers().size());
    }

    @Ignore
    @Test
    public void testThrowUserHasOrdersExceptionWhenDelete() throws Exception {
        userDao.delete(CLIENT1_ID);
    }

    @Test
    public void testFindByUserName() throws Exception {
        User user = userDao.findByUsername(CLIENT1_NAME);
        assertNotNull(user);
        assertEquals("client", user.getName());
    }

    @Test
    public void testFindByEmail() throws Exception {
        User user = userDao.findByEmail("super@mybar.com");
        assertNotNull(user);
        assertEquals("super", user.getName());
        assertEquals("super@mybar.com", user.getEmail());
    }

}