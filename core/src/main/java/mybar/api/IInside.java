package mybar.api;

import mybar.QuantityValue;

public interface IInside {

    int getId();

    <T extends IIngredient> T getIngredient();

    double getVolume();

    QuantityValue getQuantityValue();

}