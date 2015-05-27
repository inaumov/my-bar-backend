package mybar.app.bean.um;

import mybar.api.um.IRole;
import mybar.api.um.IUser;
import mybar.entity.um.Role;

import java.util.Collection;

public class BeanFactory {

    public static final Role from(final IRole role) {
        Role entity = new Role();
        entity.setId(role.getId());
        entity.setWebRole(role.getWebRole());
        return entity;
    }

    public static final UserBean from(final IUser user) {
        UserBean bean = new UserBean();
        bean.setId(user.getId());
        bean.setLogin(user.getLogin());
        bean.setPassword(user.getPassword());
        bean.setAddress(user.getAddress());
        bean.setEmail(user.getEmail());
        bean.setName(user.getName());
        bean.setSurname(user.getSurname());
        Collection<? extends IRole> roles = user.getRoles();
        for (IRole r : roles) {
            bean.getRoles().add(from(r));
        }
        bean.setActiveStatus(user.getActiveStatus());
        return bean;
    }

}