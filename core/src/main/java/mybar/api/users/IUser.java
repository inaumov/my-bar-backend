package mybar.api.users;

import java.util.List;

public interface IUser extends IUserDetails {

    boolean isActive();

    List<String> getRoles();

    String getPassword();
}