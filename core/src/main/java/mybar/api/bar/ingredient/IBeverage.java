package mybar.api.bar.ingredient;

import mybar.BeverageType;

public interface IBeverage extends IIngredient {

    BeverageType getBeverageType();

}