package mybar.repository.users;

import mybar.domain.users.Role;
import mybar.domain.users.User;
import mybar.repository.BaseDaoTest;
import org.junit.Ignore;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
@Ignore
public class UmBaseDaoTest extends BaseDaoTest {

    public static final int COURIER_ID = 5;
    public static final int CLIENT1_ID = 6; // with orders
    public static final int CLIENT2_ID = 7; // to remove
    public static final String CLIENT1_NAME = "client";
    public static final String CLIENT2_NAME = "JohnDoe";

    protected final int ROLES_CNT = 6;
    protected final int USERS_CNT = 7;
    protected final int USER_HAS_ROLES_CNT = 8;
    
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
        return ((Number) result.getSingleResult()).intValue();
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