package mybar.api.bar;

import mybar.BeverageType;

public interface IBeverage extends IIngredient {

    BeverageType getBeverageType();

}