package mybar.service.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.State;
import mybar.api.users.WebRole;
import mybar.domain.users.UmEntityFactory;
import mybar.api.users.IUser;
import mybar.repository.users.RoleDaoImpl;
import mybar.repository.users.UserDao;
import mybar.domain.users.Role;
import mybar.domain.users.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDaoImpl roleDAO;

    // basic functions

    @Transactional
    public boolean createUser(IUser user) {
        User u = UmEntityFactory.from(user);
        u.setState(State.AVAILABLE);
        Role role = roleDAO.getRole(6);
        u.addRole(role);
        return userDao.create(u).getId() != 0;
    }

    public boolean isEmailDuplicated(String email) {
        User user = userDao.findByEmail(email);
        return user != null;
    }

    public void editUserInfo(IUser user) {
        User u = UmEntityFactory.from(user);
        userDao.update(u);
    }

    // admin functions

    public IUser findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public void activateUser(IUser user) {
        User u = UmEntityFactory.from(user);
        u.setState(State.AVAILABLE);
        userDao.update(u);
    }

    public void deactivateUser(IUser user) {
        User u = UmEntityFactory.from(user);
        u.setState(State.NOT_AVAILABLE);
        userDao.update(u);
    }

    public void assignRole(IUser user, WebRole webRole) {
        User u = UmEntityFactory.from(user);
        //u.getRole().setWebRole(webRole);
        userDao.update(u);
    }

    public List<IUser> getAllRegisteredUsers() {
        List<User> users = userDao.findAll();
        return new ArrayList<IUser>(filterUsers(users));
    }

    // util functions

    private Collection<User> filterUsers(List<User> users) {
        IPredicate<User> isClient = new IPredicate<User>() {
            @Override
            public boolean apply(User type) {
                return /*type.getRole().getWebRole() != WebRole.ROLE_SUPER*/ true;
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