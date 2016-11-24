package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.DrinkType;
import mybar.api.bar.ingredient.IDrink;
import mybar.app.bean.bar.View;

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

}