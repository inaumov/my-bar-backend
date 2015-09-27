package mybar.app.bean;

import mybar.api.bar.IBeverage;
import mybar.api.bar.IIngredient;
import mybar.api.bar.IProduct;

public class ProductBean implements IProduct {

    private int id;
    private String beverageKind;
    private String brandName;
    private double volume;
    private double price;

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

    public static ProductBean from(IProduct product) {
        ProductBean bean = new ProductBean();
        bean.setId(product.getId());
        IIngredient ingredient = product.getIngredient();
        if (ingredient instanceof IBeverage) {
            bean.setIngredient(BeverageBean.from((IBeverage) ingredient));
        }
        bean.setBrandName(product.getBrandName());
        bean.setVolume(product.getVolume());
        bean.setPrice(product.getPrice());

        return bean;
    }

}
