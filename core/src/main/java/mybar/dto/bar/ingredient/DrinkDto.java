package mybar.dto.bar.ingredient;

import mybar.DrinkType;
import mybar.api.bar.ingredient.IDrink;

public class DrinkDto extends IngredientBaseDto implements IDrink {

    private DrinkType drinkType;

    @Override
    public DrinkType getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(DrinkType drinkType) {
        this.drinkType = drinkType;
    }

}