package mybar.dto.bar;

import mybar.api.bar.IBeverage;
import mybar.api.bar.IBottle;

public class BottleDto implements IBottle {

    private int id;
    private IBeverage beverage;
    private String brandName;
    private double volume;
    private double price;
    private boolean inShelf;
    private String imageUrl;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public IBeverage getBeverage() {
        return beverage;
    }

    public void setBeverage(IBeverage ingredient) {
        this.beverage = ingredient;
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

}