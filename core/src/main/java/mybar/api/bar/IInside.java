package mybar.api.bar;

import mybar.UnitsValue;
import mybar.api.bar.ingredient.IIngredient;

public interface IInside {

    int getIngredientId();

    double getVolume();

    UnitsValue getUnitsValue();

    boolean isMissing();

}