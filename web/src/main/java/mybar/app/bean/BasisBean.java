package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.QuantityValue;
import mybar.api.IBasis;

public class BasisBean implements IBasis {

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

    public static BasisBean from(IBasis basis) {
        BasisBean bean = new BasisBean();
        bean.setId(basis.getId());
        bean.setDrink(DrinkBean.from(basis.getDrink()));
        bean.setQuantity(basis.getQuantity());
        bean.setVolume(basis.getVolume());

        return bean;
    }

}
