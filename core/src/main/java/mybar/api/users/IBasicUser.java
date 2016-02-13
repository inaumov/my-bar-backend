package mybar.api.users;

import java.util.Collection;

public interface IBasicUser {

    int getId();

    String getLogin();

    String getPassword();

    String getName();

    String getSurname();

    String getEmail();

    String getAddress();

    Collection<? extends IRole> getRoles();

}