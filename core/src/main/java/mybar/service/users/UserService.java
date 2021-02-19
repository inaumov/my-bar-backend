package mybar.service.users;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import mybar.api.users.IUser;
import mybar.api.users.RoleName;
import mybar.domain.users.Role;
import mybar.domain.users.User;
import mybar.dto.users.UserDto;
import mybar.exception.users.EmailDuplicatedException;
import mybar.exception.users.UnknownUserException;
import mybar.exception.users.UserExistsException;
import mybar.repository.users.RoleDao;
import mybar.repository.users.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserDao userDao;

    private final RoleDao roleDAO;

    @Autowired
    public UserService(UserDao userDao, RoleDao roleDAO) {
        this.userDao = userDao;
        this.roleDAO = roleDAO;
    }

    public IUser createUser(IUser user) throws UserExistsException, EmailDuplicatedException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getUsername()), "Username is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getEmail()), "Email is required.");
        checkUsernameDuplicated(user.getUsername());
        checkEmailDuplicated(user.getEmail());

        User userEntity = toEntity(user);
        userEntity.setActive(true);
        if (CollectionUtils.isEmpty(user.getRoles())) {
            Role roleUser = roleDAO.getOne(RoleName.ROLE_USER.name());
            userEntity.addRole(roleUser);
        } else {
            List<Role> assignedRoles = roleDAO.findByRoleNameIn(user.getRoles());
            assignedRoles.forEach(userEntity::addRole);
        }
        return toUserDto(userDao.save(userEntity));
    }

    public boolean isEmailDuplicated(String email) {
        User userEntity = userDao.findByEmail(email);
        return userEntity != null;
    }

    private void checkEmailDuplicated(String email) {
        if (isEmailDuplicated(email)) {
            throw new EmailDuplicatedException(email);
        }
    }

    private void checkUsernameDuplicated(String username) {
        boolean exists = userDao.existsById(username);
        if (exists) {
            throw new UserExistsException(username);
        }
    }

    // admin functions

    public IUser editUserInfo(IUser user) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getUsername()), "Username is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getEmail()), "Email is required.");
        User userEntityByEmail = userDao.findByEmail(user.getEmail());
        if (Objects.equals(userEntityByEmail.getEmail(), user.getEmail())) {
            throw new UserExistsException("Email already belongs to another user");
        }
        User entity = new User();
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        entity.setSurname(user.getSurname());
        if (CollectionUtils.isEmpty(user.getRoles())) {
            Role roleUser = roleDAO.getOne(RoleName.ROLE_USER.name());
            entity.addRole(roleUser);
        } else {
            List<Role> roles = roleDAO.findByRoleNameIn(user.getRoles());
            entity.setRoles(roles);
        }

        return toUserDto(userDao.save(entity));
    }

    public IUser findByUsername(String username) throws UnknownUserException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "Username is required.");

        User userEntity = userDao.getOne(username);
        if (userEntity != null) {
            return toUserDto(userEntity);
        }
        throw new UnknownUserException(username);
    }

    private UserDto toUserDto(User userEntity) {
        UserDto dto = new UserDto();
        dto.setUsername(userEntity.getUsername());
        dto.setPassword(userEntity.getPassword());
        dto.setName(userEntity.getName());
        dto.setSurname(userEntity.getSurname());
        dto.setEmail(userEntity.getEmail());
        dto.setRoles(userEntity.getRoles()
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList())
        );
        dto.setActive(userEntity.isActive());
        return dto;
    }

    public void activateUser(String username) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "Username is required.");

        User userEntity = userDao.getOne(username);
        if (userEntity != null) {
            userEntity.setActive(true);
            userDao.save(userEntity);
        }
    }

    public void deactivateUser(String username) throws UnknownUserException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "Username is required.");

        User userEntity = userDao.getOne(username);
        if (userEntity != null) {
            userEntity.setActive(false);
            userDao.save(userEntity);
        }
        throw new UnknownUserException(username);
    }

    public void assignRole(IUser user, RoleName roleName) {
        User userEntity = toEntity(user);
        Role role = roleDAO.getOne(roleName.name());
        userEntity.addRole(role);
        userDao.save(userEntity);
    }

    public List<IUser> getAllRegisteredUsers() {
        Iterable<User> users = userDao.findAll();
        return filterUsers(users)
                .stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    // util methods

    private Collection<User> filterUsers(Iterable<User> users) {
        Role role = roleDAO.getOne(RoleName.ROLE_USER.name());
        if (role != null) {
            Predicate<User> predicate = user -> user.getRoles().contains(role);
            return filter(users, predicate);
        }
        return Collections.emptyList();
    }

    public static Collection<User> filter(Iterable<User> users, Predicate<User> predicate) {
        Collection<User> result = new ArrayList<>();
        for (User element : users) {
            if (predicate.test(element)) {
                result.add(element);
            }
        }
        return result;
    }

    private User toEntity(final IUser user) {
        User entity = new User();
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        entity.setSurname(user.getSurname());
        List<Role> roles = roleDAO.findByRoleNameIn(user.getRoles());
        entity.setRoles(roles);
        entity.setActive(user.isActive());
        return entity;
    }

    public void changePassword(IUser user, String password) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getUsername()), "Username is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "Password must not be empty.");

        final User one = userDao.getOne(user.getUsername());
        one.setPassword(password);
    }

}