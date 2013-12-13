package mybar.app.managedbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mybar.api.um.IRole;
import mybar.api.um.IUser;
import mybar.service.UserManagementService;

import java.util.List;

@Component
public abstract class AbstractUserBean implements IUser {

    private int id;
    private String login;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String address;
    private List<IRole> roles;

    @Autowired
    UserManagementService userManagementService;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public List<IRole> getRoles() {
        return roles;
    }

    public void setRoles(List<IRole> roles) {
        this.roles = roles;
    }

    @Override
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
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}