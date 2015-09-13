package mybar.api;

import mybar.QuantityValue;

public interface IIngredient {

    int getId();

    <T extends IDrink> T getDrink();

    double getVolume();

    QuantityValue getQuantity();

}