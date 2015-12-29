package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.UnitsValue;
import mybar.api.bar.IInside;
import org.modelmapper.ModelMapper;

public class InsideBean implements IInside {

    @JsonView(View.CocktailWithDetails.class)
    private int ingredientId;

    @JsonView(View.CocktailWithDetails.class)
    private double volume;

    @JsonView(View.CocktailWithDetails.class)
    private UnitsValue unitsValue;

    private boolean missing;

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

    @Override
    public boolean isMissing() {
        return false;
    }

    public void setUnitsValue(UnitsValue unitsValue) {
        this.unitsValue = unitsValue;
    }

    public static InsideBean from(IInside inside) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(inside, InsideBean.class);
    }

}