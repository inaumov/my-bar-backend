package mybar.domain.users;

import mybar.api.users.WebRole;
import mybar.api.users.IRole;

import javax.persistence.*;

@Entity
@Table(name="ROLES")
public class Role implements IRole {
	
	@Id
	private int id;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private WebRole webRole;
	
    @Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    @Override
	public WebRole getWebRole() {
		return webRole;
	}

	public void setWebRole(WebRole webRole) {
		this.webRole = webRole;
	}

}