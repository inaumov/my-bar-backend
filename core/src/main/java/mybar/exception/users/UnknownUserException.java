package mybar.exception.users;

import lombok.Getter;

public class UnknownUserException extends RuntimeException {
    @Getter
    private String username;
    public UnknownUserException(String username) {
        this.username = username;
    }
}
