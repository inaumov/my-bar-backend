package mybar.app.bean;

import mybar.api.IDrink;
import mybar.api.IMenu;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "menu")
public class MenuBean implements IMenu {

    private int id;

    private String name;

    private List<DrinkBean> drinks;

    @XmlAttribute
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    @Override
    public List<DrinkBean> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<DrinkBean> drinks) {
        this.drinks = drinks;
    }

    public static MenuBean from(IMenu menu) {
        MenuBean bean = new MenuBean();
        bean.setId(menu.getId());
        bean.setName(menu.getName());
        List<DrinkBean> drinkBeans = new ArrayList<>();
        for (IDrink d : menu.getDrinks()) {
            drinkBeans.add(DrinkBean.from(d));
        }
        bean.setDrinks(drinkBeans);
        return bean;
    }

}