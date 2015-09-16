package mybar.app.bean;

import mybar.api.IProduct;
import mybar.domain.Product;

public class ProductBean implements IProduct {

    private int id;
    private DrinkBean drink;
    private String brandName;
    private double volume;
    private double price;

    @Override
    public int getId() {
        return 0;
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
        bean.setDrink(DrinkBean.from(product.getDrink()));
        bean.setBrandName(product.getBrandName());
        bean.setVolume(product.getVolume());
        bean.setPrice(product.getPrice());

        return bean;
    }
}
