package mybar.api.um;

import mybar.ActiveStatus;

public interface IUser extends IBasicUser {

    ActiveStatus getActiveStatus();

}