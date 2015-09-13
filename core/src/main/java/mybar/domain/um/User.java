package mybar.domain.um;

import mybar.State;
import mybar.api.um.IUser;
import mybar.domain.Order;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "USERS")
@SequenceGenerator(name="USER_SEQUENCE", sequenceName="USER_SEQUENCE", allocationSize=3, initialValue = 1)
public class User implements IUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="USER_SEQUENCE")
    private int id;

    private String login;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="USER_HAS_ROLES",
            joinColumns = {@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns = {@JoinColumn(name="ROLE_ID", referencedColumnName="ID")}
    )
    private Collection<Role> roles;

    private String name;
    private String surname;
    private String email;
    private String address;

    @Column(name = "ACTIVE")
    @Enumerated(EnumType.ORDINAL)
    private State state;

    @OneToMany(mappedBy = "user", fetch=FetchType.LAZY)
    private Collection<Order> orders;

    @Override
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
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
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

    @Override
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

}