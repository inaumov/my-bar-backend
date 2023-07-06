package mybar.repository.users;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import mybar.api.users.RoleName;
import mybar.domain.users.Role;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("classpath:datasets/usersDataSet.xml")
@ContextConfiguration(classes = RoleDao.class)
public class RoleDaoTest extends UserBaseDaoTest {

    @Autowired
    private RoleDao roleDAO;

    @Test
    public void testSelectAllRoles() {
        int size = roleDAO.findAll().size();
        assertEquals(ROLES_CNT, size);
    }

    @Test
    public void testGetRole() {
        RoleName[] roleNames = RoleName.values();
        for (RoleName roleName : roleNames) {
            Role role = roleDAO.getOne(roleName.name());
            assertNotNull(role);
            assertEquals(roleName.name(), role.getRoleName());
        }
    }

    @Test
    public void findByRoleNameIn() {
        List<Role> roles = roleDAO.findByRoleNameIn(Collections.singletonList(RoleName.ROLE_ADMIN.name()));
        assertEquals(1, roles.size());
    }
}