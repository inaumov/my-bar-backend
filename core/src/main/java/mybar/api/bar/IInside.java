package mybar.api.bar;

import mybar.UnitsValue;
import mybar.api.bar.ingredient.IIngredient;

public interface IInside {

    int getId();

    <T extends IIngredient> T getIngredient();

    double getVolume();

    UnitsValue getUnitsValue();

}