package mybar.repository.users;

import mybar.repository.GenericDaoImpl;
import org.springframework.stereotype.Repository;
import mybar.domain.users.Role;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role> implements IRoleDao {

    public Role getRole(int id) {
        Role role = read(id);
        return role;
    }

}