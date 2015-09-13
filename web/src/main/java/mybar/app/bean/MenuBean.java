package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.ICocktail;
import mybar.api.IMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuBean implements IMenu {

    @JsonView(View.Menu.class)
    private int id;

    @JsonView(View.Menu.class)
    private String name;

    private List<CocktailBean> cocktails;

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

    public List<CocktailBean> getCocktails() {
        return cocktails;
    }

    public void setCocktails(List<CocktailBean> cocktails) {
        this.cocktails = cocktails;
    }

    public static MenuBean from(IMenu menu) {
        MenuBean bean = new MenuBean();
        bean.setId(menu.getId());
        bean.setName(menu.getName());
        List<CocktailBean> cocktailBeans = new ArrayList<>();
        for (ICocktail d : menu.getCocktails()) {
            cocktailBeans.add(CocktailBean.from(d));
        }
        bean.setCocktails(cocktailBeans);

        return bean;
    }

}