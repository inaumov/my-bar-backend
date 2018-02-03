package mybar.exception.users;

import lombok.Getter;

public class UserExistsException extends RuntimeException {
    @Getter
    private String username;
    public UserExistsException(String username) {
        this.username = username;
    }
}
