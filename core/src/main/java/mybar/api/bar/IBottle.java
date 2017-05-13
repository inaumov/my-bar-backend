package mybar.api.bar;

import mybar.api.bar.ingredient.IBeverage;

import java.math.BigDecimal;

public interface IBottle {

    int getId();

    IBeverage getBeverage();

    String getBrandName();

    double getVolume();

    BigDecimal getPrice();

    boolean isInShelf();

    String getImageUrl();

}