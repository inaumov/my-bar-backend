package mybar.repository.users;

import mybar.domain.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleDao extends JpaRepository<Role, String> {

    List<Role> findByRoleNameIn(List<String> roleNames);
}