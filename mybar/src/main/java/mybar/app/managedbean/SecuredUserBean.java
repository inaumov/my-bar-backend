package mybar.app.managedbean;

import mybar.ActiveStatus;
import mybar.WebRole;
import mybar.api.um.IRole;
import mybar.api.um.IUser;
import mybar.entity.um.Role;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;

@Component
@ManagedBean(name = "securedUserBean")
@SessionScoped
public class SecuredUserBean extends AbstractUserBean implements IUser {

    private boolean isActive;
    private String[] allowedRoles;
    private String selection;
    private int id;

    public void init() {
        IUser user = userManagementService.findByUsername(getLogin());
        setId(user.getId());
        super.setName(user.getName());
        super.setSurname(user.getSurname());
        super.setEmail(user.getEmail());
        super.setAddress(user.getAddress());
        super.setSurname(user.getSurname());
        this.setActive(user.getActiveStatus() == ActiveStatus.ENABLED ? true : false);
        this.selection = user.getRoles().iterator().next().getWebRole().name();  // TODO: FIX
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public ActiveStatus getActiveStatus() {
        return ActiveStatus.ENABLED;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String save() {
        boolean result = false;
        try {
            IUser user = userManagementService.findByUsername(getLogin());
            Role r = new Role();
            r.setWebRole(WebRole.valueOf(selection));
            //r.setId(user.getRole().getId());
            this.setRoles(Arrays.<IRole>asList(r));
            setId(user.getId());
            userManagementService.editUserInfo(this);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        if (result) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "User Registration Successful!!!", null));
            return "success";
        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "User Registration Failed!!!", null));
            return "failure";
        }
    }

    public String[] getAllowedRoles() {
        allowedRoles = new String[]{
                WebRole.ROLE_ADMIN.name(),
                WebRole.ROLE_ANALYST.name(),
                WebRole.ROLE_KITCHEN_SERVICE.name(),
                WebRole.ROLE_DELIVERY.name(),
                WebRole.ROLE_CLIENT.name()
        };
        return allowedRoles;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public List<IUser> getUsers() {
        return userManagementService.getAllRegisteredUsers();
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public List<IRole> getRoles() {
        return null;
    }
}