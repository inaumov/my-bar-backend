package mybar.web.rest;

import mybar.WebRole;
import mybar.api.um.IUser;
import mybar.app.bean.um.BeanFactory;
import mybar.app.bean.um.RoleBean;
import mybar.app.bean.um.UserBean;
import mybar.app.bean.um.UserList;
import mybar.service.UserManagementService;
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

@Controller
public class UserController {

    private static final String XML_VIEW_NAME = "users";

    @Autowired
    UserManagementService userManagementService;

    @Autowired
    private Jaxb2Marshaller marshaller;

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}")
    public ModelAndView getUser(@PathVariable("id") String id) {
        IUser user = userManagementService.findByUsername(id);
        return new ModelAndView(XML_VIEW_NAME, "user", user);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/user/{id}")
    public ModelAndView updateUser(@RequestParam String body) {
        Source source = new StreamSource(new StringReader(body));
        UserBean userBean = (UserBean) marshaller.unmarshal(source);
        userManagementService.editUserInfo(userBean);
        return new ModelAndView(XML_VIEW_NAME, "user", userBean);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public ModelAndView addUser(@RequestBody String body) {
        Source source = new StreamSource(new StringReader(body));
        UserBean userBean = (UserBean) marshaller.unmarshal(source);
        RoleBean role = new RoleBean();
        role.setWebRole(WebRole.ROLE_CLIENT);
        userBean.setRoles(Arrays.<RoleBean>asList(role));
        userManagementService.createUser(userBean);
        return new ModelAndView(XML_VIEW_NAME, "user", userBean);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/user/{id}")
    public ModelAndView removeUser(@PathVariable String id) {
        IUser user = userManagementService.findByUsername(id);
        userManagementService.deactivateUser(user);
        List<IUser> users = userManagementService.getAllRegisteredUsers();
        return new ModelAndView(XML_VIEW_NAME, "users", new UserList(toBeans(users)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public ModelAndView getUsers() {
        List<IUser> users = userManagementService.getAllRegisteredUsers();
        return new ModelAndView(XML_VIEW_NAME, "users", new UserList(toBeans(users)));
    }

    public boolean isEmailDuplicated(String email) {
        return userManagementService.isEmailDuplicated(email);
    }

    private static List<UserBean> toBeans(List<IUser> users) {
        List<UserBean> userBeans = new ArrayList<>();
        for (IUser user : users) {
            userBeans.add(BeanFactory.from(user));
        }
        return userBeans;
    }

}