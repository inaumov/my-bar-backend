package mybar.domain.um;

import mybar.api.um.IRole;
import mybar.api.um.IUser;
import mybar.domain.um.Role;
import mybar.domain.um.User;

import java.util.Collection;

public class UmEntityFactory {

    public static final Role from(final IRole role) {
        Role entity = new Role();
        entity.setId(role.getId());
        entity.setWebRole(role.getWebRole());
        return entity;
    }

    public static final User from (final IUser user) {
        User entity = new User();
        entity.setId(user.getId());
        entity.setLogin(user.getLogin());
        entity.setPassword(user.getPassword());
        entity.setAddress(user.getAddress());
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        entity.setSurname(user.getSurname());
        Collection<? extends IRole> roles = user.getRoles();
        for(IRole r : roles) {
            entity.addRole(from(r));
        }
        entity.setActiveStatus(user.getActiveStatus());
        return entity;
    }

}