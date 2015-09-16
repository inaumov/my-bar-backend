package mybar.api;

import mybar.BeverageType;

public interface IBeverage extends IIngredient {

    BeverageType getBeverageType();

}