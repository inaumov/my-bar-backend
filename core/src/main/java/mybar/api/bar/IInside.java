package mybar.api.bar;

import mybar.UnitsValue;

public interface IInside {

    int getId();

    <T extends IIngredient> T getIngredient();

    double getVolume();

    UnitsValue getUnitsValue();

}