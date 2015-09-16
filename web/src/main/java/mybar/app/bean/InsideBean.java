package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.QuantityValue;
import mybar.api.IInside;

public class InsideBean implements IInside {

    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private DrinkBean drink;

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
    public DrinkBean getDrink() {
        return drink;
    }

    public void setDrink(DrinkBean drink) {
        this.drink = drink;
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

    public static InsideBean from(IInside ingredient) {
        InsideBean bean = new InsideBean();
        bean.setId(ingredient.getId());
        bean.setDrink(DrinkBean.from(ingredient.getDrink()));
        bean.setQuantityValue(ingredient.getQuantityValue());
        bean.setVolume(ingredient.getVolume());

        return bean;
    }

}
