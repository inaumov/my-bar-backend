package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mybar.api.bar.IBeverage;
import mybar.api.bar.IIngredient;
import mybar.api.bar.IProduct;

public class Bottle implements IProduct {

    private int id;
    private String beverageKind;
    private String brandName;
    private double volume;
    private double price;
    private boolean active;

    @JsonIgnore
    private IIngredient ingredient;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBeverageKind() {
        return beverageKind;
    }

    public void setBeverageKind(String beverageKind) {
        this.beverageKind = beverageKind;
    }

    @Override
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public IIngredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(IIngredient ingredient) {
        this.ingredient = ingredient;
    }

    public static Bottle from(IProduct product) {
        Bottle bean = new Bottle();
        bean.setId(product.getId());
        IIngredient ingredient = product.getIngredient();
        if (ingredient instanceof IBeverage) {
            bean.setBeverageKind(ingredient.getKind());
        }
        bean.setBrandName(product.getBrandName());
        bean.setVolume(product.getVolume());
        bean.setPrice(product.getPrice());

        return bean;
    }

}
