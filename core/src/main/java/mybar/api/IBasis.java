package mybar.api;

import mybar.QuantityValue;

public interface IBasis {

    int getId();

    <T extends IDrink> T getDrink();

    double getVolume();

    QuantityValue getQuantity();

}