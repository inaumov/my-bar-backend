package mybar.web.rest.users;

import mybar.api.users.WebRole;
import mybar.api.users.IUser;
import mybar.app.bean.users.BeanFactory;
import mybar.app.bean.users.RoleBean;
import mybar.app.bean.users.UserBean;
import mybar.app.bean.users.UserList;
import mybar.service.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Controller
public class UserController {

    private static final String XML_VIEW_NAME = "users";

    @Autowired
    UserService userService;

    @Autowired
    private Jaxb2Marshaller umMarshaller;

    @RequestMapping(method = RequestMethod.GET, value = "/user/{username}")
    public ModelAndView getUser(@PathVariable("username") String username) {
        IUser user = userService.findByUsername(username);
        return new ModelAndView(XML_VIEW_NAME, "user", BeanFactory.from(user));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{id}")
    public ModelAndView updateUser(@RequestParam String body) {
        Source source = new StreamSource(new StringReader(body));
        UserBean userBean = (UserBean) umMarshaller.unmarshal(source);
        userService.editUserInfo(userBean);
        return new ModelAndView(XML_VIEW_NAME, "user", userBean);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public ModelAndView addUser(@RequestBody String body) {
        Source source = new StreamSource(new StringReader(body));
        UserBean userBean = (UserBean) umMarshaller.unmarshal(source);
        RoleBean role = new RoleBean();
        role.setWebRole(WebRole.ROLE_CLIENT);
        userBean.setRoles(Arrays.<RoleBean> asList(role));
        userService.createUser(userBean);
        return new ModelAndView(XML_VIEW_NAME, "user", userBean);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/user/{username}")
    public ModelAndView removeUser(@PathVariable String username) {
        IUser user = userService.findByUsername(username);
        userService.deactivateUser(user);
        List<IUser> users = userService.getAllRegisteredUsers();
        return new ModelAndView(XML_VIEW_NAME, "users", new UserList(toBeans(users)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public ModelAndView getUsers() {
        List<IUser> users = userService.getAllRegisteredUsers();
        return new ModelAndView(XML_VIEW_NAME, "users", new UserList(toBeans(users)));
    }

    public boolean isEmailDuplicated(String email) {
        return userService.isEmailDuplicated(email);
    }

    private static List<UserBean> toBeans(List<IUser> users) {
        List<UserBean> userBeans = new ArrayList<>();
        for (IUser user : users) {
            userBeans.add(BeanFactory.from(user));
        }
        return userBeans;
    }

}