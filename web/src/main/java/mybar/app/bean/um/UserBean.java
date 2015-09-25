package mybar.app.bean.um;

import mybar.State;
import mybar.api.users.IUser;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "user")
@XmlType(propOrder = {"login", "name", "surname", "email", "roles"})
public class UserBean implements IUser {

    private int id;
    private String login;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String address;
    private List<RoleBean> roles = new ArrayList<>();
    private State state;

    @Override
    @XmlTransient
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    @XmlElement(name = "role")
    @XmlElementWrapper(name = "roles")
    public List<RoleBean> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleBean> roles) {
        this.roles = roles;
    }

    @Override
    @XmlTransient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    @XmlTransient
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    @XmlAttribute(name = "active")
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}