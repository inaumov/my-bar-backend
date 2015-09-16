package mybar.api;

import mybar.DrinkType;

public interface IDrink extends IIngredient {

    DrinkType getDrinkType();

}