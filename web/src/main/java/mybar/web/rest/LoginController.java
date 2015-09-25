package mybar.web.rest;

import mybar.app.bean.um.LoginBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import mybar.app.AuthenticationService;
import mybar.api.users.WebRole;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Controller
public class LoginController implements Serializable {

    private List<SimpleGrantedAuthority> authorities;
    private boolean loggedIn;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Login operation.
     *
     * @param username
     * @param password
     * @return
     */
    public String doLogin(String username, String password) {

        loggedIn = authenticationService.login(username, password);

        if (loggedIn) {
            authorities = (List<SimpleGrantedAuthority>)
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        }
        return "";
    }

    @RequestMapping(value = "/login1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String authentication(@RequestParam("login") String userName,
                          @RequestParam("password") String password, HttpServletRequest request) {

        doLogin(userName, password);
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SecurityContext securityContext = SecurityContextHolder.getContext();

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            return "success";
        } catch (AuthenticationException ex) {
            return "fail " + ex.getMessage();
        }

    }

    @RequestMapping(value = "/login2", method = RequestMethod.POST, consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView executeLogin(@RequestBody final LoginBean loginBean, HttpServletRequest request) {
        ModelAndView model = null;
        try {

            doLogin(loginBean.getLogin(), loginBean.getPassword());
            if (loggedIn) {
                System.out.println("User Login Successful");
                request.setAttribute("loggedInUser", loginBean.getLogin());
                model = new ModelAndView("welcome");
            } else {
                model = new ModelAndView("login");
                request.setAttribute("loggedInUser", loginBean.getLogin());
                request.setAttribute("message", "Invalid credentials!!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return model;
    }

    /**
     * Logout operation.
     *
     * @return
     */
    public String doLogout() {

        authenticationService.logout();

        // Set the parameter indicating that user is logged in to false
        loggedIn = false;

        return "";
    }

    public boolean isSuper() {
        return hasRole(WebRole.ROLE_SUPER);
    }

    public boolean isAdmin() {
        return hasRole(WebRole.ROLE_ADMIN);
    }

    public boolean isAnalyst() {
        return hasRole(WebRole.ROLE_ANALYST);
    }

    public boolean isCock() {
        return hasRole(WebRole.ROLE_KITCHEN_SERVICE);
    }

    public boolean isDelivery() {
        return hasRole(WebRole.ROLE_DELIVERY);
    }

    public boolean isClient() {
        return hasRole(WebRole.ROLE_CLIENT);
    }

    public boolean isAnonymous() {
        return authorities == null;
    }

    private boolean hasRole(WebRole role) {
        if (!isAnonymous()) {
            for (SimpleGrantedAuthority sga : authorities) {
                if (sga.getAuthority().equals(role.name())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasShoppingBasket() {
        return isClient() ^ isAnonymous();
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : Collections.<SimpleGrantedAuthority>emptyList();
    }

}