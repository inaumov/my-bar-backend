package mybar.api.bar.ingredient;

import mybar.BeverageType;

public interface IBeverage extends IIngredient {

    public static final String GROUP_NAME = "beverages";

    BeverageType getBeverageType();

}