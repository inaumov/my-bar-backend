package mybar.dao;

import org.springframework.stereotype.Repository;
import mybar.entity.um.Role;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role> implements IRoleDao {

    public Role getRole(int id) {
        Role role = read(id);
        return role;
    }

}