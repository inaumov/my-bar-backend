package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.QuantityValue;
import mybar.api.IBasis;

public class BasisBean implements IBasis {

    private int id;

    @JsonView(View.DrinkWithDetails.class)
    private IngredientBean ingredient;

    @JsonView(View.DrinkWithDetails.class)
    private double volume;

    @JsonView(View.DrinkWithDetails.class)
    private QuantityValue quantity;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public IngredientBean getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientBean ingredient) {
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
    public QuantityValue getQuantity() {
        return quantity;
    }

    public void setQuantity(QuantityValue quantity) {
        this.quantity = quantity;
    }

    public static BasisBean from(IBasis basis) {
        BasisBean bean = new BasisBean();
        bean.setId(basis.getId());
        bean.setIngredient(IngredientBean.from(basis.getIngredient()));
        bean.setQuantity(basis.getQuantity());
        bean.setVolume(basis.getVolume());

        return bean;
    }

}
