package mybar.app.bean.users;

import mybar.api.users.WebRole;
import mybar.api.users.IRole;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "role")
@XmlJavaTypeAdapter(RoleAdapter.class)
public class RoleBean implements IRole {

    private int id;
    private WebRole webRole;

    @Override
    @XmlTransient
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