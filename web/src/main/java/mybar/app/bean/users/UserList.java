package mybar.app.bean.users;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserList {

    @JsonView(View.AdminView.class)
    private int count;
    @JsonView(View.AdminView.class)
    private List<UserBean> users;

    public UserList() {
    }

    public UserList(List<UserBean> users) {
        this.users = users;
        this.count = users.size();
    }

}