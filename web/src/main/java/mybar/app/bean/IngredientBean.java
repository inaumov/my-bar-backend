package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.QuantityValue;
import mybar.api.IIngredient;

public class IngredientBean implements IIngredient {

    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private DrinkBean drink;

    @JsonView(View.CocktailWithDetails.class)
    private double volume;

    @JsonView(View.CocktailWithDetails.class)
    private QuantityValue quantity;

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
    public QuantityValue getQuantity() {
        return quantity;
    }

    public void setQuantity(QuantityValue quantity) {
        this.quantity = quantity;
    }

    public static IngredientBean from(IIngredient ingredient) {
        IngredientBean bean = new IngredientBean();
        bean.setId(ingredient.getId());
        bean.setDrink(DrinkBean.from(ingredient.getDrink()));
        bean.setQuantity(ingredient.getQuantity());
        bean.setVolume(ingredient.getVolume());

        return bean;
    }

}
