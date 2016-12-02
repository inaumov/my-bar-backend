package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.UnitsValue;
import mybar.api.bar.ICocktailIngredient;

public class CocktailIngredientBean implements ICocktailIngredient {

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private int ingredientId;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private double volume;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private UnitsValue unitsValue;

    @JsonView(View.Cocktail.class)
    private Boolean missing;

    @Override
    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public UnitsValue getUnitsValue() {
        return unitsValue;
    }

    public Boolean isMissing() {
        return missing;
    }

    public void setMissing(Boolean missing) {
        this.missing = missing;
    }

    public void setUnitsValue(UnitsValue unitsValue) {
        this.unitsValue = unitsValue;
    }

}