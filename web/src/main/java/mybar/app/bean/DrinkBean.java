package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.IDrink;

public class DrinkBean implements IDrink {

    @JsonView(View.CocktailWithDetails.class)
    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private String name;

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

    public static DrinkBean from(IDrink ingredient) {
        DrinkBean bean = new DrinkBean();
        bean.setId(ingredient.getId());
        bean.setName(ingredient.getName());

        return bean;
    }

}