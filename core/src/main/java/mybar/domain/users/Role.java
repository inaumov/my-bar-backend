package mybar.domain.users;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @Column(name = "ROLE_NAME")
    private String roleName;

    @Basic
    @Column(name = "DESCRIPTION")
    private String description;

}