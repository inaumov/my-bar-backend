package mybar.dto.users;

import lombok.Getter;
import lombok.Setter;
import mybar.api.users.IUser;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto implements IUser {

    private String username;
    private String password;

    private String name;
    private String surname;

    private String email;

    private List<String> roles = new ArrayList<>();
    private boolean active;

}