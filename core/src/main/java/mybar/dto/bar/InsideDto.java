package mybar.dto.bar;

import mybar.UnitsValue;
import mybar.api.bar.IInside;

public class InsideDto implements IInside {

    private int ingredientId;
    private double volume;
    private UnitsValue unitsValue;
    private boolean missing;

    @Override
    public int getIngredientId() {
        return ingredientId;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public UnitsValue getUnitsValue() {
        return unitsValue;
    }

    @Override
    public boolean isMissing() {
        return missing;
    }

}