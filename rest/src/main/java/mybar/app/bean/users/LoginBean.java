package mybar.app.bean.users;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginBean {

    @JsonProperty
    private String login;
    @JsonProperty
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
