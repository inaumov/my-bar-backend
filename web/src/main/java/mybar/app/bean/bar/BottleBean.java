package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.api.bar.IBottle;
import mybar.app.bean.bar.ingredient.BeverageBean;
import org.modelmapper.ModelMapper;

public class BottleBean implements IBottle {

    @JsonView(View.Shelf.class)
    private int id;

    @JsonView(View.Shelf.class)
    private BeverageBean beverage;

    @JsonView(View.Shelf.class)
    private String brandName;

    @JsonView(View.Shelf.class)
    private double volume;

    @JsonView(View.Shelf.class)
    private double price;

    @JsonView(View.Shelf.class)
    private boolean inShelf;

    @JsonView(View.Shelf.class)
    private String imageUrl;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public BeverageBean getBeverage() {
        return beverage;
    }

    public void setBeverage(BeverageBean beverage) {
        this.beverage = beverage;
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

    @Override
    public boolean isInShelf() {
        return inShelf;
    }

    public void setInShelf(boolean inShelf) {
        this.inShelf = inShelf;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static BottleBean from(IBottle bottle) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(bottle, BottleBean.class);
    }

}