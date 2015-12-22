package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.UnitsValue;
import mybar.api.bar.*;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.app.bean.bar.ingredient.AdditiveBean;
import mybar.app.bean.bar.ingredient.BeverageBean;
import mybar.app.bean.bar.ingredient.DrinkBean;

public class InsideBean implements IInside {

    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private IIngredient ingredient;

    @JsonView(View.CocktailWithDetails.class)
    private double volume;

    @JsonView(View.CocktailWithDetails.class)
    private UnitsValue unitsValue;

    private boolean missing;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public IIngredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(IIngredient ingredient) {
        this.ingredient = ingredient;
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

    public void setUnitsValue(UnitsValue unitsValue) {
        this.unitsValue = unitsValue;
    }

    public static InsideBean from(IInside inside) {
        InsideBean bean = new InsideBean();
        bean.setId(inside.getId());
        IIngredient ingredient = inside.getIngredient();
        if (ingredient instanceof IBeverage) {
            bean.setIngredient(BeverageBean.from((IBeverage) ingredient));
        } else if (ingredient instanceof IDrink) {
            bean.setIngredient(DrinkBean.from((IDrink) ingredient));
        } else if (ingredient instanceof IAdditive) {
            bean.setIngredient(AdditiveBean.from((IAdditive) ingredient));
        }
        bean.setUnitsValue(inside.getUnitsValue());
        bean.setVolume(inside.getVolume());
        return bean;
    }

}