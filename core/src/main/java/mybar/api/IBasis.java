package mybar.api;

import mybar.QuantityValue;

public interface IBasis {

    int getId();

    <T extends IIngredient>T getIngredient();

    double getVolume();

    QuantityValue getValue();

}