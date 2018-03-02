package mybar.app.bean.users;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import mybar.api.users.IUser;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserBean extends UserDetailsBean implements IUser {

    @JsonView({View.AdminView.class})
    private List<String> roles = new ArrayList<>();
    @JsonView({View.AdminView.class})
    private boolean active;

    private String password;

}