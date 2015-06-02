package mybar.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import mybar.app.AuthenticationService;
import mybar.WebRole;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class LoginController implements Serializable {

    private static final long serialVersionUID = 7765876811740798583L;

    private List<SimpleGrantedAuthority> authorities;
    private boolean loggedIn;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Login operation.
     *
     * @return
     * @param username
     * @param password
     */
    public String doLogin(String username, String password) {

        loggedIn = authenticationService.login(username, password);

        if (loggedIn) {
            authorities = (List<SimpleGrantedAuthority>)
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        }
        return "";
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