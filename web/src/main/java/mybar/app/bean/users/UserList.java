package mybar.app.bean.users;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "user.list")
public class UserList {

    private int count;
    private List<UserBean> users;

    public UserList() {
    }

    public UserList(List<UserBean> users) {
        this.users = users;
        this.count = users.size();
    }

    @XmlAttribute
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @XmlElement(name = "user")
    @XmlElementWrapper
    public List<UserBean> getUsers() {
        return users;
    }

    public void setUsers(List<UserBean> users) {
        this.users = users;
    }

}