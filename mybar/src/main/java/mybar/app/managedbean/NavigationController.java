package mybar.app.managedbean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "navigationController", eager = true)
@SessionScoped
public class NavigationController implements Serializable {

    private static final long serialVersionUID = 1520318172495977648L;

    /**
     * Go to login page.
     *
     * @return Login page name.
     */
    public String toLogin() {
        return "/login.xhtml?faces-redirect=true";
    }

    public String toIndex() {
        return "/index.xhtml?faces-redirect=true";
    }

    public String toFailure() {
        return "/loginFailed.xhtml?authfailed=true";
    }

    /**
     * Redirect to welcome page.
     *
     * @return Welcome page name.
     */
    public String toAccounts() {
        return "/secured/manage/user/accounts.xhtml?faces-redirect=true";
    }

    public String toWelcome() {
        return "/secured/welcome.xhtml?faces-redirect=true";
    }
}