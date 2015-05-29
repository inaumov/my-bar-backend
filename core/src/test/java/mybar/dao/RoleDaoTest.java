package mybar.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mybar.WebRole;
import mybar.entity.um.Role;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleDaoTest extends BaseDaoTest {

    @Autowired
    private RoleDAOImpl roleDAO;

    @Test
    public void testSelectAllRoles() throws Exception {
        int size = roleDAO.em.createQuery("select r from Role r", Role.class).getResultList().size();
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