package mybar.app.managedbean;

import mybar.api.um.IRole;
import org.springframework.stereotype.Component;
import mybar.ActiveStatus;
import mybar.WebRole;
import mybar.entity.um.Role;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;

@Component
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean extends AbstractUserBean {

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String addUser() {
        Role role = new Role();
        role.setId(0);
        role.setWebRole(WebRole.ROLE_CLIENT);
        this.setRoles(Arrays.<IRole>asList(role));
        boolean result = userManagementService.createUser(this);
        if (result) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "User Registration Successful!!!", null));
            return "success";
        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "User Registration Failed!!!", null));
            return "failure";
        }
    }

    public boolean isEmailDuplicated(String email) {
        return userManagementService.isEmailDuplicated(email);
    }

    @Override
    public ActiveStatus getActiveStatus() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}