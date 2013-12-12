package mybar.dao;

import org.springframework.stereotype.Repository;
import mybar.entity.um.Role;

@Repository
public class RoleDAOImpl extends GenericDaoImpl<Role> implements IRoleDAO {

    public Role getRole(int id) {
        Role role = read(id);
        return role;
    }

}