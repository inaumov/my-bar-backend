package mybar.app.bean.bar;

import mybar.api.bar.IBottle;

public class BottleBean implements IBottle {

    private int id;
    private BeverageBean beverage;
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
        BottleBean bean = new BottleBean();
        bean.setId(bottle.getId());
        bean.setBeverage(bottle.getBeverage());
        bean.setBrandName(bottle.getBrandName());
        bean.setVolume(bottle.getVolume());
        bean.setPrice(bottle.getPrice());
        bean.setInShelf(bottle.isInShelf());
        bean.setImageUrl(bottle.getImageUrl());
        return bean;
    }

}