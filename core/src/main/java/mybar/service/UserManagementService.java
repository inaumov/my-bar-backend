package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.ActiveStatus;
import mybar.WebRole;
import mybar.api.um.UmEntityFactory;
import mybar.api.EntityFactory;
import mybar.api.um.IUser;
import mybar.dao.RoleDAOImpl;
import mybar.dao.UserDAO;
import mybar.entity.um.Role;
import mybar.entity.um.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserManagementService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAOImpl roleDAO;

    // basic functions

    @Transactional
    public boolean createUser(IUser user) {
        User u = UmEntityFactory.from(user);
        u.setActiveStatus(ActiveStatus.ENABLED);
        Role role = roleDAO.getRole(6);
        u.addRole(role);
        return userDAO.create(u).getId() != 0;
    }

    public boolean isEmailDuplicated(String email) {
        User user = userDAO.findByEmail(email);
        return user != null;
    }

    public void editUserInfo(IUser user) {
        User u = UmEntityFactory.from(user);
        userDAO.update(u);
    }

    // admin functions

    public IUser findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public void activateUser(IUser user) {
        User u = UmEntityFactory.from(user);
        u.setActiveStatus(ActiveStatus.ENABLED);
        userDAO.update(u);
    }

    public void deactivateUser(IUser user) {
        User u = UmEntityFactory.from(user);
        u.setActiveStatus(ActiveStatus.DISABLED);
        userDAO.update(u);
    }

    public void assignRole(IUser user, WebRole webRole) {
        User u = UmEntityFactory.from(user);
        //u.getRole().setWebRole(webRole);
        userDAO.update(u);
    }

    public List<IUser> getAllRegisteredUsers() {
        List<User> users = userDAO.findAll();
        return new ArrayList<IUser>(filterUsers(users));
    }

    // util functions

    private Collection<User> filterUsers(List<User> users) {
        IPredicate<User> isClient = new IPredicate<User>() {
            @Override
            public boolean apply(User type) {
                return /*type.getRole().getWebRole() != WebRole.ROLE_SUPER*/ false;
            }
        };
        return filter(users, isClient);
    }

    private interface IPredicate<T> {
        boolean apply(T type);
    }

    public static <T> Collection<T> filter(Collection<T> target, IPredicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element : target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

}