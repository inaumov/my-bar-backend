package mybar.exception.users;

import lombok.Getter;

public class EmailDuplicatedException extends RuntimeException {
    @Getter
    private String email;
    public EmailDuplicatedException(String email) {
        this.email = email;
    }
}
