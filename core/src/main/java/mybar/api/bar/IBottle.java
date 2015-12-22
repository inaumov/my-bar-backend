package mybar.api.bar;

import mybar.api.bar.ingredient.IBeverage;

public interface IBottle {

    int getId();

    IBeverage getBeverage();

    String getBrandName();

    double getVolume();

    double getPrice();

    boolean isInShelf();

    String getImageUrl();

}