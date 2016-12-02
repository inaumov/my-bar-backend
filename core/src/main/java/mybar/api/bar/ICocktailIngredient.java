package mybar.api.bar;

import mybar.UnitsValue;

public interface ICocktailIngredient {

    int getIngredientId();

    double getVolume();

    UnitsValue getUnitsValue();

}