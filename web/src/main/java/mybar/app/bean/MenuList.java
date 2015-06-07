package mybar.app.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "menu.list")
public class MenuList {

    private List<MenuBean> menus;

    public MenuList() {
    }

    public MenuList(List<MenuBean> menus) {
        this.menus = menus;
    }

    @XmlElement(name = "menu")
    @XmlElementWrapper
    public List<MenuBean> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuBean> menus) {
        this.menus = menus;
    }

}