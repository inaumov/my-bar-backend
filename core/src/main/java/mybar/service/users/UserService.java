package mybar.service.users;

import mybar.api.users.IUser;
import mybar.api.users.RoleName;
import mybar.domain.users.Role;
import mybar.domain.users.User;
import mybar.dto.users.UserDto;
import mybar.exception.users.EmailDuplicatedException;
import mybar.exception.users.UserExistsException;
import mybar.exception.users.UnknownUserException;
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

    @Autowired(required = false)
    private UserDao userDao;

    @Autowired(required = false)
    private RoleDao roleDAO;

    // basic functions

    public IUser createUser(IUser user) throws UserExistsException, EmailDuplicatedException {

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
        if (userDao.exists(username)) {
            throw new UserExistsException(username);
        }
    }

    public IUser editUserInfo(IUser user) {
        User userEntity = toEntity(user);
        return toUserDto(userDao.save(userEntity));
    }

    // admin functions

    public IUser findByUsername(String username) throws UnknownUserException {
        User userEntity = userDao.findOne(username);
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
        User userEntity = userDao.findOne(username);
        if (userEntity != null) {
            userEntity.setActive(true);
            userDao.save(userEntity);
        }
    }

    public void deactivateUser(String username) throws UnknownUserException {
        User userEntity = userDao.findOne(username);
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

    // util functions

    private Collection<User> filterUsers(Iterable<User> users) {
        Role role = roleDAO.findOne(RoleName.ROLE_USER.name());
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

}