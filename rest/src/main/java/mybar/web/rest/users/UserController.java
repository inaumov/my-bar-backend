package mybar.web.rest.users;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.users.IUser;
import mybar.api.users.IUserDetails;
import mybar.app.bean.users.*;
import mybar.service.users.UserService;
import mybar.web.rest.users.exception.InvalidPasswordException;
import mybar.web.rest.users.exception.PasswordConfirmationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @JsonView(View.UserView.class)
    @RequestMapping(method = RequestMethod.GET, value = "/{username}")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed("ROLE_USER")
    public UserDetailsBean getUser(@PathVariable("username") String username) {
        IUserDetails user = userService.findByUsername(username);
        return BeanFactory.fromDetails(user);
    }

    @JsonView(View.UserView.class)
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed("ROLE_USER")
    public UserDetailsBean updateUser(@RequestBody UserBean userBean) {
        IUserDetails user = userService.editUserInfo(userBean);
        return BeanFactory.fromDetails(user);
    }

    @JsonView(View.UserView.class)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsBean register(@RequestHeader HttpHeaders headers,
                                    @RequestBody RegisterUserBean registerUserBean) {
        validatePasswordConfirmation(registerUserBean);

        String pwd = new String(tryDecodeBase64(registerUserBean.getPassword()), StandardCharsets.UTF_8);
        registerUserBean.setPassword(passwordEncoder.encode(pwd));

        IUserDetails user = userService.createUser(registerUserBean);
        return BeanFactory.fromDetails(user);
    }

    public byte[] tryDecodeBase64(String pwd) {
        try {
            return Base64.getDecoder().decode(pwd);
        } catch (IllegalArgumentException e) {
            throw new InvalidPasswordException("Password is not Base64 encoded.");
        }
    }

    private void validatePasswordConfirmation(RegisterUserBean user) {
        if (user.getPasswordConfirm() == null || !Objects.equals(user.getPassword(), user.getPasswordConfirm())) {
            throw new PasswordConfirmationException();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed("ROLE_USER")
    public void deactivate(@PathVariable String username) {
        userService.deactivateUser(username);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<MappingJacksonValue> getUsers() {
        List<IUser> users = userService.getAllRegisteredUsers();

        UserList userList = new UserList(BeanFactory.toFullUserList(users));

        MappingJacksonValue wrapper = new MappingJacksonValue(userList);
        wrapper.setSerializationView(View.AdminView.class);

        return new ResponseEntity<>(wrapper, HttpStatus.OK);
    }

}