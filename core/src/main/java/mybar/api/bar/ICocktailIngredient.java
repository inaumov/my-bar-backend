package mybar.api.bar;

import mybar.UnitOfMeasurement;

public interface ICocktailIngredient {

    int getIngredientId();

    double getVolume();

    UnitOfMeasurement getUnitOfMeasurement();

}