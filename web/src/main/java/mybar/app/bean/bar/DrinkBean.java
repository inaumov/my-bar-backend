package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.DrinkType;
import mybar.api.bar.IDrink;

public class DrinkBean implements IDrink {

    @JsonView(View.CocktailWithDetails.class)
    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private String name;

    private DrinkType drinkType;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getKind() {
        return name;
    }

    public void setKind(String name) {
        this.name = name;
    }

    @Override
    public DrinkType getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(DrinkType drinkType) {
        this.drinkType = drinkType;
    }

    public static DrinkBean from(IDrink drink) {
        DrinkBean bean = new DrinkBean();
        bean.setId(drink.getId());
        bean.setKind(drink.getKind());
        bean.setDrinkType(drink.getDrinkType());
        return bean;
    }

}