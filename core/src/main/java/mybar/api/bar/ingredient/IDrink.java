package mybar.api.bar.ingredient;

import mybar.DrinkType;

public interface IDrink extends IIngredient {

    public static final String GROUP_NAME = "drinks";

    DrinkType getDrinkType();

}