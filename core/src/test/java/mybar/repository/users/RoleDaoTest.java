package mybar.repository.users;

import mybar.api.users.WebRole;
import mybar.domain.users.Role;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
@Ignore
public class RoleDaoTest extends UmBaseDaoTest {

    @Autowired
    private RoleDaoImpl roleDAO;

    @Test
    public void testSelectAllRoles() throws Exception {
        int size = em.createQuery("select r from Role r", Role.class).getResultList().size();
        assertTrue(size == ROLES_CNT);
    }

    @Test
    public void testGetRole() throws Exception {
        WebRole[] webRoles = WebRole.values();
        for (int i = 1; i <= webRoles.length; i++) {
            Role role = roleDAO.getRole(i);
            assertNotNull(role);
            assertEquals(i, role.getId());
            assertEquals(webRoles[i - 1], role.getWebRole());
        }
    }

    @Test
    public void testGetRoleWhenUnknownId() throws Exception {
        assertNull(roleDAO.getRole(101));
    }

}