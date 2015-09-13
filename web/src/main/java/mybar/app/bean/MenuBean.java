package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.IDrink;
import mybar.api.IMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuBean implements IMenu {

    @JsonView(View.Menu.class)
    private int id;

    @JsonView(View.Menu.class)
    private String name;

    private List<DrinkBean> drinks;

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