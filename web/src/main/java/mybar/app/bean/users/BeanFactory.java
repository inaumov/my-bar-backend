package mybar.app.bean.users;

import mybar.api.users.IUser;
import mybar.api.users.IUserDetails;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanFactory {

    public static UserDetailsBean fromDetails(final IUserDetails user) {
        UserDetailsBean userDetailsBean = new UserDetailsBean();
        BeanUtils.copyProperties(user, userDetailsBean);
        return userDetailsBean;
    }

    public static UserBean fromFullUser(final IUser user) {
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);
        return userBean;
    }

    public static List<UserBean> toFullUserList(List<? extends IUser> users) {
        List<UserBean> userBeans = new ArrayList<>();
        for (IUser user : users) {
            userBeans.add(fromFullUser(user));
        }
        return userBeans;
    }

}