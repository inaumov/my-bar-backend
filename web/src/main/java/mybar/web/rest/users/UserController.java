package mybar.web.rest.users;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.users.IUser;
import mybar.api.users.IUserDetails;
import mybar.app.bean.users.BeanFactory;
import mybar.app.bean.users.RegisterUserBean;
import mybar.app.bean.users.UserBean;
import mybar.app.bean.users.UserDetailsBean;
import mybar.app.bean.users.UserList;
import mybar.app.bean.users.View;
import mybar.service.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @JsonView(View.UserView.class)
    @RequestMapping(method = RequestMethod.GET, value = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsBean getUser(@PathVariable("username") String username) {
        IUserDetails user = userService.findByUsername(username);
        return BeanFactory.fromDetails(user);
    }

    @JsonView(View.UserView.class)
    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserDetailsBean updateUser(@RequestBody UserBean userBean) {
        IUserDetails user = userService.editUserInfo(userBean);
        return BeanFactory.fromDetails(user);
    }

    @JsonView(View.UserView.class)
    @RequestMapping(value = "/register", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsBean register(@RequestBody RegisterUserBean registerUserBean) {
        // TODO validate passwords
        IUserDetails user = userService.createUser(registerUserBean);
        return BeanFactory.fromDetails(user);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable String username) {
        userService.deactivateUser(username);
    }

    @JsonView(View.AdminView.class)
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserList getUsers() {
        List<IUser> users = userService.getAllRegisteredUsers();
        return new UserList(BeanFactory.toFullUserList(users));
    }

}