package mybar.api.bar;

import mybar.QuantityValue;

public interface IInside {

    int getId();

    <T extends IIngredient> T getIngredient();

    double getVolume();

    QuantityValue getQuantityValue();

}