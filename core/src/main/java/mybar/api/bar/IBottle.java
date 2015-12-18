package mybar.api.bar;

public interface IBottle {

    int getId();

    IBeverage getBeverage();

    String getBrandName();

    double getVolume();

    double getPrice();

    boolean isInShelf();

    String getImageUrl();

}