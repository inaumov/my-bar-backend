package mybar.app.bean.users;

import lombok.Getter;
import lombok.Setter;
import mybar.api.users.IUser;

@Getter
@Setter
public class RegisterUserBean extends UserBean implements IUser {
    private String password;
    private String passwordConfirm;
}