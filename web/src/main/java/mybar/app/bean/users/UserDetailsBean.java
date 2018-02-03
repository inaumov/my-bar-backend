package mybar.app.bean.users;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import mybar.api.users.IUserDetails;

@Getter
@Setter
public class UserDetailsBean implements IUserDetails {

    @JsonView({View.UserView.class, View.AdminView.class})
    private String username;
    @JsonView({View.UserView.class, View.AdminView.class})
    private String email;
    @JsonView({View.UserView.class, View.AdminView.class})
    private String name;
    @JsonView({View.UserView.class, View.AdminView.class})
    private String surname;

}