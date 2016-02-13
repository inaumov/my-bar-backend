package mybar.app;

public interface AuthenticationService {

    boolean login(String username, String password);

    void logout();

}