package mybar.app.bean.users;

import mybar.api.users.IRole;
import mybar.api.users.IUser;

public class BeanFactory {

    public static final RoleBean from(final IRole role) {
        RoleBean bean = new RoleBean();
        bean.setId(role.getId());
        bean.setWebRole(role.getWebRole());
        return bean;
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
        for (IRole r : user.getRoles()) {
            bean.getRoles().add(from(r));
        }
        bean.setState(user.getState());
        return bean;
    }

}