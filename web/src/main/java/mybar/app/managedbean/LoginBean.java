package mybar.app.managedbean;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import mybar.app.AuthenticationService;
import mybar.WebRole;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 7765876811740798583L;

    private String username;
    private String password;
    private List<SimpleGrantedAuthority> authorities;
    private boolean loggedIn;

    @ManagedProperty(value = "#{navigationController}")
    private NavigationController navigationController;

    @ManagedProperty(value = "#{authenticationService}")
    private AuthenticationService authenticationService; // injected Spring defined service

    /**
     * Login operation.
     *
     * @return
     */
    public String doLogin() {

        loggedIn = authenticationService.login(username, password);

        if (loggedIn) {
            authorities = (List<SimpleGrantedAuthority>)
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            System.out.println("User [" + username + "] is logged in: " + loggedIn);
            return navigationController.toWelcome();
        }

        // Set login ERROR
        FacesMessage msg = new FacesMessage("Login error!", "ERROR MSG");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        // To to login page
        return navigationController.toFailure();

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

        // Set logout message
        FacesMessage msg = new FacesMessage("Logout success!", "INFO MSG");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return navigationController.toIndex();
    }

    // ------------------------------
    // Getters & Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
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
        if(!isAnonymous()) {
            for (SimpleGrantedAuthority sga : authorities) {
                if(sga.getAuthority().equals(role.name())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasShoppingCart() {
        return isClient() ^ isAnonymous();
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : Collections.<SimpleGrantedAuthority>emptyList();
    }

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

}