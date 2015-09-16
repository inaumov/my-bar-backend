package mybar.api;

import mybar.QuantityValue;

public interface IInside {

    int getId();

    <T extends IDrink> T getDrink();

    double getVolume();

    QuantityValue getQuantityValue();

}