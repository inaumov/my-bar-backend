package mybar.repository.users;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Iterables;
import mybar.api.users.RoleName;
import mybar.domain.users.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@DatabaseSetup("classpath:datasets/usersDataSet.xml")
public class RoleDaoTest extends UserBaseDaoTest {

    @Autowired
    private RoleDao roleDAO;

    @Test
    public void testSelectAllRoles() throws Exception {
        int size = Iterables.size(roleDAO.findAll());
        assertTrue(size == ROLES_CNT);
    }

    @Test
    public void testGetRole() throws Exception {
        RoleName[] roleNames = RoleName.values();
        for (RoleName roleName : roleNames) {
            Role role = roleDAO.findOne(roleName.name());
            assertNotNull(role);
            assertEquals(roleName.name(), role.getRoleName());
        }
    }

    @Test
    public void testGetRoleWhenUnknownId() throws Exception {
        assertNull(roleDAO.findOne("ROLE_UNKNOWN"));
    }

    @Test
    public void findByRoleNameIn() throws Exception {
        List<Role> roles = roleDAO.findByRoleNameIn(Collections.singletonList(RoleName.ROLE_ADMIN.name()));
        assertEquals(1, roles.size());
    }
}