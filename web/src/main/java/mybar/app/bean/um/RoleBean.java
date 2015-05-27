package mybar.app.bean.um;

import mybar.WebRole;
import mybar.api.um.IRole;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "role")
public class RoleBean implements IRole {

    private int id;
    private WebRole webRole;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public WebRole getWebRole() {
        return webRole;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWebRole(WebRole webRole) {
        this.webRole = webRole;
    }

}