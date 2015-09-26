package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.QuantityValue;
import mybar.api.bar.*;

public class InsideBean implements IInside {

    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private IIngredient ingredient;

    @JsonView(View.CocktailWithDetails.class)
    private double volume;

    @JsonView(View.CocktailWithDetails.class)
    private QuantityValue quantityValue;

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
    public QuantityValue getQuantityValue() {
        return quantityValue;
    }

    public void setQuantityValue(QuantityValue quantityValue) {
        this.quantityValue = quantityValue;
    }

    public static InsideBean from(IInside inside) {
        InsideBean bean = new InsideBean();
        bean.setId(inside.getId());
        IIngredient ingredient = inside.getIngredient();
        if (ingredient instanceof IBeverage) {
            bean.setIngredient(BeverageBean.from((IBeverage) ingredient));
        } else if (ingredient instanceof IDrink) {
            bean.setIngredient(DrinkBean.from((IDrink) ingredient));
        } else if (ingredient instanceof IAdditional) {
            bean.setIngredient(AdditionalBean.from((IAdditional) ingredient));
        }
        bean.setQuantityValue(inside.getQuantityValue());
        bean.setVolume(inside.getVolume());
        return bean;
    }

}