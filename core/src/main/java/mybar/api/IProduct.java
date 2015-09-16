package mybar.api;

public interface IProduct {

    int getId();

    <T extends IDrink> T getDrink();

    String getBrandName();

    double getVolume();

    double getPrice();

}