package mybar.repository;

import org.springframework.stereotype.Repository;
import mybar.domain.um.Role;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role> implements IRoleDao {

    public Role getRole(int id) {
        Role role = read(id);
        return role;
    }

}