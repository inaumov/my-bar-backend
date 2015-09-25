package mybar.api.users;

import mybar.State;

public interface IUser extends IBasicUser {

    State getState();

}