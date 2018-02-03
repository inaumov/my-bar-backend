package mybar.app.bean.users;

import lombok.Getter;
import lombok.Setter;
import mybar.api.users.IUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
public class RegisterUserBean extends UserBean implements IUser {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private String password;
    private String passwordConfirm;

    @SuppressWarnings("UnusedDeclaration")
    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = PASSWORD_ENCODER.encode(passwordConfirm);
    }

}